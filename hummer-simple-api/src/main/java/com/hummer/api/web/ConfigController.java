package com.hummer.api.web;

import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.hummer.core.PropertiesContainer;
import com.hummer.rest.model.ResourceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/9/30 14:53
 **/
@RestController
@RequestMapping(value = "/v1")
@Slf4j
public class ConfigController {
    @NacosValue(value = "${test.config}", autoRefreshed = true)
    //warning ：use @Value annotation config value no flush
    private String testConfig;

    @NacosConfigListener(dataId = "mytest_01", groupId = "test_01")
    public void notice(Properties config) {
        log.info("config changed notice {}", config);
    }

    @GetMapping(value = "/nacos/config")
    public ResourceResponse showConfig() {
        return ResourceResponse.ok(String.format("%s---------->%s"
            , testConfig
            , PropertiesContainer.valueOfString("test.config")));
    }
}
