package com.hummer.test;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.hummer.notification.channel.plugin.client.dd.DingDingCustomRobotClient;
import com.hummer.notification.channel.plugin.config.DdCustomRobotConfig;
import com.hummer.notification.channel.plugin.context.DdCustomRobotLinkContext;
import com.hummer.notification.channel.plugin.context.DdCustomRobotTextContext;
import com.hummer.notification.channel.plugin.model.DdCustomRobotAtData;
import com.hummer.notification.channel.plugin.model.DdCustomRobotLinkData;
import com.hummer.notification.channel.plugin.model.DdCustomRobotTextData;
import com.hummer.notification.channel.plugin.result.DdCustomRobotNotifyResponse;
import com.hummer.notification.channel.plugin.result.NotificationResponse;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

public class CommonTest {
    @Test
    public void url() {
        Assert.assertEquals("2021-06-24+11%3A46%3A57", URLEncoder.encode("2021-06-24 11:46:57"));
        URI uri = URI.create("http://local.gjpqqd.com:5929/Service/ERPService.asmx/EMallApi?method=emall.token.get&timestamp=2021-06-24+11%3A46%3A57&format=json&app_key=000210611170422897&v=1.0&sign=&sign_method=md5");
        Map<String, String> map = Splitter.on("&").withKeyValueSeparator("=").split(uri.getQuery());
        Assert.assertEquals("emall.token.get"
                , map.get("method"));
    }

    @Test
    public void ddNotify() {
        NotificationResponse response = DingDingCustomRobotClient.doNotify(createLinkContext(), createConfig());
        System.out.println(JSONObject.toJSONString(response));
    }

    public static void doNotify() {
        DdCustomRobotNotifyResponse response = DingDingCustomRobotClient.doNotify(createContext(), createConfig());
        System.out.println(JSONObject.toJSONString(response));

    }

    public static DdCustomRobotTextContext createContext() {

        DdCustomRobotTextContext context = new DdCustomRobotTextContext();
        DdCustomRobotAtData atData = new DdCustomRobotAtData();
        atData.setAtMobiles(Collections.singletonList("+86-18262282991"));
        atData.setIsAtAll(false);
        context.setAt(atData);
        DdCustomRobotTextData textData = new DdCustomRobotTextData();
        textData.setContent("此处是测试信息，请忽略@+86-18262282991");
        context.setText(textData);
        return context;
    }

    public static DdCustomRobotLinkContext createLinkContext() {
        DdCustomRobotLinkContext context = new DdCustomRobotLinkContext();
        DdCustomRobotLinkData linkData = new DdCustomRobotLinkData();
        context.setLink(linkData);
        linkData.setMessageUrl("http://app.yugyg.com");
        linkData.setPicUrl("http://jc.yugyg.com/uploadFiles/uploadImgs/goodsPic/goodsPic/f9fb0a1bdb0a463aa00511f78e98eef0.png");
        linkData.setText("这个即将发布的新版本，创始人xx称它为红树林。而在此之前" +
                "，每当面临重大升级，产品经理们都会取一个应景的代号，这一次，为什么是红树林@+86-18262282991");
        linkData.setTitle("此处是测试信息");
        return context;
    }

    public static DdCustomRobotConfig createConfig() {
        String api = "https://oapi.dingtalk.com/robot/send";
        String accessToken = "e39a35744a69b19684575e95eb17380037179891022f8e81126356e3db4b0a28";
        String secret = "SEC6cf5dab390752b1533d2692095054740b98aa803f99fabd61c7c1c0d17376396";
        DdCustomRobotConfig config = new DdCustomRobotConfig();
        config.setAccessToken(accessToken);
        config.setNeedSign(true);
        config.setRetry(1);
        config.setSecret(secret);
        config.setSendApi(api);
        config.setTimeOutMillions(3000L);
        return config;
    }

}
