package com.uestc.service.impl;
import com.uestc.account.user.UserAccountService;
import com.uestc.account.user.vo.PayResultVO;
import com.uestc.dao.TbUserAccountMapper;
import com.uestc.domain.TbUserAccount;
import com.uestc.domain.TbUserAccountExample;
import com.uestc.service.producer.*;
import com.uestc.utils.FastJsonConvertUtil;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class UserAccountServiceImpl implements UserAccountService{
    public static final String TX_PAY_TOPIC = "tx_pay_topic";

    public static final String TX_PAY_TAGS = "pay";
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    TbUserAccountMapper tbUserAccountMapper;

    @Autowired
    private TransactionProducer transactionProducer;

    @Autowired
    private CallbackService callbackService;

    PayResultVO payResultVO=null;

    @Override
    public PayResultVO payment(String userId, String orderId, String accountId, BigDecimal money,String token) {
        payResultVO=new PayResultVO();
        String paymentRet = "";
        try {
            //	最开始有一步 token验证操作（重复提单问题）
            //接口调用去重 token在浏览页面时生成。
            if(redisTemplate.boundSetOps("payment").isMember(token)){
                return payResultVO;
            }
            redisTemplate.boundSetOps("payment").add(token);

            //如果两个人同时登陆同一个用户，去支付。
            //加锁开始（获取） 用户级别，不同用户不会竞争
            //如果获取不到锁（宕机），所以在插入数据时还应该用cas重。
            //如果要用zookeepr的话要确保能用足够的节点。因为每个用户都需要一个节点，建议用redis

            TbUserAccountExample example=new TbUserAccountExample();
            TbUserAccountExample.Criteria criteria=example.createCriteria();
            criteria.andIdEqualTo(accountId);
            List<TbUserAccount> tbUserAccounts = tbUserAccountMapper.selectByExample(example);
            if(tbUserAccounts==null){
                payResultVO.setMessage("找不到该账户");
            }
            TbUserAccount old=tbUserAccounts.get(0);
            BigDecimal currentBalance = old.getAccountBalance();
            int currentVersion = old.getVersion();
            //	要对大概率事件进行提前预判（小概率事件我们做放过,但是最后保障数据的一致性即可）
            //业务出发:
            //当前一个用户账户 只允许一个线程（一个应用端访问）
            //技术出发：
            //1 redis去重 分布式锁
            //2 数据库乐观锁去重
            //	做扣款操作的时候：获得分布式锁，看一下能否获得
            BigDecimal newBalance = currentBalance.subtract(money);

            //加锁结束（释放）

            if(newBalance.doubleValue() > 0 ) {	//	或者一种情况获取锁失败
                //	1.组装消息
                //  1.执行本地事务
                String keys = UUID.randomUUID().toString() + "$" + System.currentTimeMillis();
                Map<String, Object> params = new HashMap<>();
                //	可能需要用到的参数
                params.put("userId", userId);
                params.put("orderId", orderId);
                params.put("accountId", accountId);
                params.put("payMoney", money);
                params.put("newBalance", newBalance);
                params.put("currentVersion", currentVersion);
                Message message = new Message(TX_PAY_TOPIC, TX_PAY_TAGS, keys, FastJsonConvertUtil.convertObjectToJSON(params).getBytes());


                //	同步阻塞
                CountDownLatch countDownLatch = new CountDownLatch(1);
                params.put("currentCountDown", countDownLatch);
                //	消息发送并且 本地的事务执行
                TransactionSendResult sendResult = transactionProducer.sendMessage(message, params);

                countDownLatch.await();
                //消息发送成功才往下走
                if(sendResult.getSendStatus() == SendStatus.SEND_OK
                        && sendResult.getLocalTransactionState() == LocalTransactionState.COMMIT_MESSAGE) {
                    //	回调order通知支付成功消息
                    callbackService.sendOKMessage(orderId, userId);
                    paymentRet = "支付成功!";
                } else {
                    paymentRet = "支付失败!";
                }
            } else {
                paymentRet = "余额不足!";
            }
        } catch (Exception e) {
            e.printStackTrace();
            paymentRet = "支付失败!";
        }
        payResultVO.setMessage(paymentRet);
        return payResultVO;
    }
}
