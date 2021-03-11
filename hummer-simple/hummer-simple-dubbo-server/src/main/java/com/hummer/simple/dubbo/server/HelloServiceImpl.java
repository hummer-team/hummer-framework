package com.hummer.simple.dubbo.server;

import comm.hummer.simple.common.facade.HelloService;
import comm.hummer.simple.common.module.SimpleDubboDto;

import javax.validation.constraints.NotNull;
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

    @Override
    public Integer add2(@NotNull(message = "dto not null") SimpleDubboDto dto) {
        return dto.getA() + dto.getB();
    }
}
