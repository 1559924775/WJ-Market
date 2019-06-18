package com.uestc.order;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class TestService {
    @HystrixCollapser(batchMethod = "testAll",scope = com.netflix.hystrix.HystrixCollapser.Scope.GLOBAL,
            collapserProperties = {
                    @HystrixProperty(name="timerDelayInMilliseconds",value="200"),
            @HystrixProperty(name="maxRequestsInBatch",value="50"),
            @HystrixProperty(name="requestCache.enabled",value="false")
            })

    public Future<String> test(String id){
        System.out.println("id:"+id+"----"+cnt++);
        return null;
    }
    int cnt=0;
    @HystrixCommand
    public List<String> testAll(List<String> ids){
        for(int i=0;i<ids.size();i++){
            System.out.println("id:"+ids.get(i)+"batcher-----------------");
        }
        List<String> list=new ArrayList<>();
        for(String s:ids){
            list.add(s);
        }
        return list;
    }
}
