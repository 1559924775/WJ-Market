package com.uestc.order;

import com.uestc.cart.vo.Cart;
import com.uestc.order.constants.UpdateStockStatus;
import com.uestc.order.vo.OrderItemVO;
import com.uestc.order.vo.OrderResultVO;
import com.uestc.order.vo.OrderVO;
import org.mengyun.tcctransaction.api.Compensable;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
@Repository
public interface OrderService {

	void  createOrder(OrderVO orderVO, String token);

	void sendOrderlyMessage4Pkg(String userId, String orderId);

	@Compensable
	public void   doCreateOrder(OrderVO orderVO,Cart cart);
	public void udpateStockBeforeOrder(List<OrderItemVO> orderItemVOs);
	public UpdateStockStatus updateStock(OrderItemVO orderItemVO);

	
}
