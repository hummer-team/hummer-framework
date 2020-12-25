package com.hummer.redis.plugin.test;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hummer.common.coder.ProtostuffCoder;
import com.hummer.common.http.HttpSyncClient;
import com.hummer.common.http.context.MessageTypeContext;
import com.hummer.common.utils.DateUtil;
import com.hummer.core.config.PropertiesConfig;
import com.hummer.core.init.HummerApplicationContextInit;
import com.hummer.core.starter.BootStarterBean;
import com.hummer.rest.model.ResourceResponse;
import com.hummer.rest.model.request.ResourcePageReqDto;
import comm.hummer.simple.common.module.QueryStringDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Collections;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConfigFileApplicationContextInitializer.class
        , HummerApplicationContextInit.class})
@Import(value = {PropertiesConfig.class, BootStarterBean.class})
@PropertySource(value = {"classpath:application.properties"})
public class MessageCoderTest {
    @Test
    public void postMessageByCoder() {
        ResourcePageReqDto<QueryStringDto> req = new ResourcePageReqDto<>();
        req.setPageNumber(1);
        req.setPageSize(10);
        QueryStringDto dto = new QueryStringDto();
        dto.setAtTime(DateUtil.now());
        dto.setClassId(124);
        dto.setUuId("sd");
        dto.setUsers(Collections.singletonList("sdsddss"));
        req.setQueryObject(dto);
        ResourceResponse<ResourcePageReqDto<QueryStringDto>> resp =
                HttpSyncClient.sendByRetry("http://localhost:8089/v1/message-coder3"
                        , req
                        , new MessageTypeContext<>(new TypeReference<ResourceResponse<ResourcePageReqDto<QueryStringDto>>>() {
                        }, new ResourceResponse<ResourcePageReqDto<QueryStringDto>>().getClass())
                        , HttpMethod.POST
                        , 3000
                        , 0);

        System.out.println(JSON.toJSON(resp));
    }

    @Test
    public void protostuffCoder() throws IOException {
        ResourcePageReqDto<QueryStringDto> req2 = new ResourcePageReqDto<>();
        req2.setPageNumber(1);
        req2.setPageSize(10);
        QueryStringDto dto = new QueryStringDto();
        dto.setAtTime(DateUtil.now());
        dto.setClassId(124);
        dto.setUuId("sd");
        dto.setUsers(Collections.singletonList("sdsddss"));
        req2.setQueryObject(dto);

        byte[] bytes = ProtostuffCoder.encodeWithJson(req2);
        ResourcePageReqDto<QueryStringDto> re3 = ProtostuffCoder.decodeWithJson(bytes, req2.getClass());
        System.out.println(JSON.toJSONString(re3));


        byte[] bytes2 = ProtostuffCoder.encode(req2);
        ResourcePageReqDto<QueryStringDto> re4 = ProtostuffCoder.decode(bytes2, req2.getClass());
        System.out.println(JSON.toJSONString(re4));

    }
}
