package com.hummer.common.test;

import com.hummer.common.exceptions.SysException;
import com.hummer.common.http.HttpAsyncClient;
import com.hummer.common.http.HttpSyncClient;
import com.hummer.common.http.RequestCustomConfig;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/20 16:33
 **/
public class HttpClientTest {
    @Test(expected = NullPointerException.class)
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

    @Test(expected = SysException.class)
    public void sendGetBySyncClient() {
        String httpGet = HttpSyncClient.sendHttpGet("http://www.baidu2.com");
        System.out.println(httpGet);
    }
}
