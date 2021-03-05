package com.hummer.simple.dubbo.server;

import comm.hummer.simple.common.facade.HelloService;
import org.springframework.stereotype.Component;

import java.util.Map;

//@Component
public class HelloServiceImpl implements HelloService {
    @Override
    public String save(Map<String, Object> demo) {
        return "hell " + demo.toString();
    }

    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }
}
