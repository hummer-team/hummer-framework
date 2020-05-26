package com.hummer.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;

public class ConfigTest {
    public static void main(String[] args) throws NacosException, IOException {
        String serverAddr = "http://192.168.38.148:8848";
        String dataId = "mytest_01";
        String group = "test_01";
        Properties properties = new Properties();

        properties.put("serverAddr", serverAddr);
        ConfigService configService = NacosFactory.createConfigService(properties);

        String content = configService.getConfig(dataId, group, 5000);
        System.out.println(content);
        configService.addListener("data_01", "G_1", new Listener() {
            @Override
            public Executor getExecutor() {
               return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println("recieve22:" + configInfo);
            }
        });
        configService.addListener(dataId, group, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println("recieve:" + configInfo);
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });


        System.in.read();
    }
}
