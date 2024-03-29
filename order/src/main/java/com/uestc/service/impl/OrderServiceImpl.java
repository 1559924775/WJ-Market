package com.uestc.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;

import com.alibaba.dubbo.config.annotation.Service;
import com.uestc.cart.vo.Cart;
import com.uestc.dao.TbOrderItemMapper;
import com.uestc.dao.TbOrderMapper;
import com.uestc.domain.TbOrder;
import com.uestc.domain.TbOrderItem;
import com.uestc.order.OrderService;
import com.uestc.order.constants.UpdateStockStatus;
import com.uestc.order.vo.OrderItemVO;
import com.uestc.order.vo.OrderVO;
import com.uestc.service.TbOrderItemService;
import com.uestc.service.TbOrderService;
import com.uestc.service.producer.OrderlyProducer;
import com.uestc.service.utils.FastJsonConvertUtil;
import com.uestc.stock.StockService;
import com.uestc.stock.vo.StockVO;
import org.apache.rocketmq.common.message.Message;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Propagation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 问题：现在下单时,对于每一个购物车，先拿到库存，把库存不足的数量设为0，设置为库存不足。
 * 库存够的，插入订单表和订单详情表。
 *
 * 先用CAS操作冻结库存，在执行TCC事务插入两张表，插入成功在confirm方法中把冻结的去掉，插入失败，把冻结的加回去。
 */

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderService tbOrderService;
	@Autowired
	private TbOrderItemService tbOrderItemService;

	@Autowired
	private TbOrderMapper tbOrderMapper;

	@Reference
	private StockService stockService;
    
    @Autowired
    private OrderlyProducer orderlyProducer;

	@Autowired
	RedisTemplate redisTemplate;

	/**
	 * 从redis中取出购物车，对于每一个商品查看库存，构建新的Order，
	 */

	/**
	 * 返回值是用来展示在支付页面的
	 * @param orderVO 从前端传过来的包含一些公共信息
	 * @return
	 */

	@Override
	public void createOrder(OrderVO orderVO,String token) {
		//接口调用去重 token在浏览页面时生成。
		if(redisTemplate.boundSetOps("createOrder").isMember(token)){
			return ;
		}
		redisTemplate.boundSetOps("createOrder").add(token);

		//从redis中获取到购物车
		List<Cart> cartList= (List<Cart>) redisTemplate.boundHashOps("cartList").get(orderVO.getUserId());

		for(Cart cart:cartList){
			//一个商家一个购物车
			//先拿到库存，把库存不够num设置为0
			udpateStockBeforeOrder(cart.getOrderItemList());
			doCreateOrder(orderVO,cart);
		}
	}

	//TCC事务
	@Override
    @Compensable(propagation = Propagation.REQUIRES_NEW,confirmMethod = "confirmDoCreateOrder", cancelMethod = "cancelDoCreateOrder", asyncConfirm = true)
	public void  doCreateOrder(OrderVO orderVO,Cart cart){
		//创建TbOrder 订单表
		TbOrder tbOrder=new TbOrder();
		tbOrder.setUserId(orderVO.getUserId());
		tbOrder.setSellerId(cart.getSellerId());//orderVO只是一些前端公共数据,没有sellerId
		//....
		//把库存不够的去除

		BigDecimal payment=new BigDecimal(0);
		String id=UUID.randomUUID().toString();
		//支付金额会更新，最后再插入。
		List<TbOrderItem> tbOrderItems=new ArrayList<>();
		for(OrderItemVO orderItemVO:cart.getOrderItemList()){
			String itemId=UUID.randomUUID().toString();
			TbOrderItem tbOrderItem=new TbOrderItem();
			tbOrderItem.setId(itemId);
			tbOrderItem.setPicPath(tbOrderItem.getPicPath());
			tbOrderItem.setOrderId(id);
			tbOrderItem.setGoodsId(orderItemVO.getGoodsId());
			tbOrderItem.setTotalFee(orderItemVO.getTotalFee());
			tbOrderItem.setNum(orderItemVO.getNum());
			tbOrderItem.setStatusTcc("inserting");
			payment.add(tbOrderItem.getTotalFee());
			tbOrderItems.add(tbOrderItem);
		}
		//插入订单详情表
		tbOrderItemService.insertAll(tbOrderItems);
		//重新计算金额
		orderVO.setPayment(payment);
		//插入订单表
		tbOrderService.insert(tbOrder);

	}

	public void  confirmDoCreateOrder(OrderVO orderVO,Cart cart){

	}

	public void   cancelDoCreateOrder(OrderVO orderVO,Cart cart){

	}


	/**
	 * 返回库存够得的商品列表 ，检查库存CAS操作
	 * @return
	 */
	@Override
	public void udpateStockBeforeOrder(List<OrderItemVO> orderItemVOs){
		ExecutorService executorService= Executors.newCachedThreadPool();
		final CountDownLatch countDownLatch=new CountDownLatch(orderItemVOs.size());
		for(final OrderItemVO orderItemVO:orderItemVOs){
			Runnable runnable=new Runnable() {
				@Override
				public void run() {
					UpdateStockStatus status=updateStock(orderItemVO);
					if(status.equals(UpdateStockStatus.SUCCESS)){
						//拿到库存
					}else if(status.equals(UpdateStockStatus.CAS_FAIL)){
						int cnt=0;//重试次数
						while(!status.equals(UpdateStockStatus.SUCCESS)&&cnt<5){
							status=updateStock(orderItemVO);
							cnt++;
						}
						if(status.equals(UpdateStockStatus.SUCCESS)){
							//拿到库存
						}
						//库存不够num设置为0
						orderItemVO.setNum(0);
						orderItemVO.setMessage("库存不足");
					}
					countDownLatch.countDown();
				}
			};
			executorService.submit(runnable);
			executorService.shutdown();
			try {
				//等待全部检查完
				countDownLatch.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 检查商品的库存够不够，
	 * @param orderItemVO
	 * @return
	 */
	@Override
	public UpdateStockStatus updateStock(OrderItemVO orderItemVO){
		StockVO stockVO=stockService.selectOne(orderItemVO.getGoodsId());
		int num=stockVO.getStock()-orderItemVO.getNum();
		if(num>=0) {
			//得到一些库存，CAS操作，并发量不多
			int currentVersion = stockVO.getVersion();
			int updateRetCount = stockService.freezeStoreCountByVersion(currentVersion,
					orderItemVO.getGoodsId() + "", new Date(), num,orderItemVO.getNum());

			if (updateRetCount == 1) {  //数据库有一条数据被更新。
				return UpdateStockStatus.SUCCESS;
			} else if (updateRetCount == 0) {
				return UpdateStockStatus.CAS_FAIL;
			}
		}
		return UpdateStockStatus.FAIL;
	}



	public static final String PKG_TOPIC = "pkg_topic";
	
	public static final String PKG_TAGS = "pkg";

	@Override
	/**
	 * 给物流系统发顺序消息
	 */
	public void sendOrderlyMessage4Pkg(String userId, String orderId) {
		List<Message> messageList = new ArrayList<>();
		
		Map<String, Object> param1 = new HashMap<>();
		param1.put("userId", userId);
		param1.put("orderId", orderId);
		param1.put("text", "创建包裹操作---1");
		
		String key1 = UUID.randomUUID().toString() + "$" +System.currentTimeMillis();
		Message message1 = new Message(PKG_TOPIC, PKG_TAGS, key1, FastJsonConvertUtil.convertObjectToJSON(param1).getBytes());
		
		messageList.add(message1);
		
		
		Map<String, Object> param2 = new HashMap<>();
		param2.put("userId", userId);
		param2.put("orderId", orderId);
		param2.put("text", "发送物流通知操作---2");
		
		String key2 = UUID.randomUUID().toString() + "$" +System.currentTimeMillis();
		Message message2 = new Message(PKG_TOPIC, PKG_TAGS, key2, FastJsonConvertUtil.convertObjectToJSON(param2).getBytes());
		
		messageList.add(message2);
		
		//	顺序消息投递 是应该按照 供应商ID 与topic 和 messagequeueId 进行绑定对应的
		//  supplier_id

		TbOrder tbOrder = tbOrderMapper.selectByPrimaryKey(orderId);
		int messageQueueNumber = Integer.parseInt(tbOrder.getSellerId());
		
		//对应的顺序消息的生产者 把messageList 发出去
		orderlyProducer.sendOrderlyMessages(messageList, messageQueueNumber);
	}

	
}
