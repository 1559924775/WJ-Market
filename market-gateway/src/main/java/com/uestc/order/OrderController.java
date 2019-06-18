package com.uestc.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.uestc.order.vo.OrderResultVO;
import com.uestc.order.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@RestController
public class OrderController {

   /* @Reference
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
    public void createOrder(@RequestBody OrderVO orderVO, String token) throws Exception {
        orderService.createOrder(orderVO,token);
    }

    public List<OrderResultVO> createOrderFallbackMethod4Timeout(@RequestBody OrderVO orderVO, String token) throws Exception {
        System.err.println("-------超时降级策略执行------------");
        //返回一个降级页面
        return null;
    }*/


   @Autowired
   TestService testService;
    @RequestMapping("test")
    public void test(String id){
        testService.test(id);
    }


}
