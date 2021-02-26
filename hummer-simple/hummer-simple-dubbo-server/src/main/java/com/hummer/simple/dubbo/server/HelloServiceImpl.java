package com.hummer.simple.dubbo.server;

import org.apache.dubbo.config.annotation.DubboService;

import java.util.Map;

@DubboService(version = "${demo.service.version}")
public class HelloServiceImpl implements HelloService {
    @Override
    public String save(Map<String, Object> demo) {
        return "hell " + demo.toString();
    }
}
