package com.uestc.order;

import com.uestc.order.vo.OrderResultVO;
import com.uestc.order.vo.OrderVO;

import java.util.List;

public interface OrderService {

	List<OrderResultVO> createOrder(OrderVO orderVO, String token);

	void sendOrderlyMessage4Pkg(String userId, String orderId);

	
}
