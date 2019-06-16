package com.uestc.order;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.uestc.order.vo.OrderResultVO;
import com.uestc.order.vo.OrderVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/order")
public class OrderController {


    OrderService orderService;

    //	限流策略：超时降级
    @HystrixCommand(
            commandKey = "createOrder",
            commandProperties = {
                    @HystrixProperty(name="execution.timeout.enabled", value="true"),
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="3000"),
            },
            fallbackMethod = "createOrderFallbackMethod4Timeout"
    )
    @RequestMapping("/createOrder")
    public List<OrderResultVO> createOrder(@RequestBody OrderVO orderVO, String token) throws Exception {
        return orderService.createOrder(orderVO,token);
    }

    public List<OrderResultVO> createOrderFallbackMethod4Timeout(@RequestBody OrderVO orderVO, String token) throws Exception {
        System.err.println("-------超时降级策略执行------------");
        //返回一个降级页面
        return null;
    }

}
