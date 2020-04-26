package com.hummer.common.http;

import com.hummer.common.exceptions.SysException;
import org.springframework.web.bind.annotation.RequestMethod;
import org.testng.annotations.Test;

import java.net.UnknownHostException;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/20 16:33
 **/
public class HttpClientTest {
    @Test
    public void sendGet() {
        RequestCustomConfig config = RequestCustomConfig
                .builder()
                .setSocketTimeOutMillisecond(300)
                .setMethod(RequestMethod.GET)
                .setUrl("http://www")
                .build();

        String result = HttpAsyncClient.create().sendGet(config);
        System.out.println(result);
    }

    @Test(expectedExceptions = {SysException.class, UnknownHostException.class})
    public void sendGetBySyncClient() {
        String httpGet = HttpSyncClient.sendHttpGet("http://www.baidu2.com");
        System.out.println(httpGet);
    }
}
