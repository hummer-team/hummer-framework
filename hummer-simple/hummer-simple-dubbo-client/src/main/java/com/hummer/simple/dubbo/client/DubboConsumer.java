package com.hummer.simple.dubbo.client;


import com.google.common.collect.Maps;
import comm.hummer.simple.common.facade.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DubboConsumer {
    @Autowired
    private HelloService helloService;
    public String hello() {
        Map<String,Object> map= Maps.newConcurrentMap();
        map.put("AA",System.currentTimeMillis());
        return helloService.save(map);
    }
}
