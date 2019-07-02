package com.hummer.common.http;

import org.springframework.web.bind.annotation.RequestMethod;
import org.testng.annotations.Test;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/20 16:33
 **/
public class HttpAsyncClientTest {
    @Test
    public void sendGet(){
        RequestCustomConfig config = RequestCustomConfig
                .builder()
                .setSocketTimeOutMillisecond(300)
                .setMethod(RequestMethod.GET)
                .setUrl("http://localhost:8000/v1/japanese/switch")
                .build();

        String result = HttpAsyncClient.instance().sendGet(config);
        System.out.println(result);
    }
}
