package com.hummer.nacos.rest;

import com.hummer.config.NaCosConfig;
import com.hummer.config.bo.ConfigListenerKey;
import com.hummer.core.PropertiesContainer;
import com.hummer.nacos.model.CustomListener;
import com.hummer.rest.model.ResourceResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NacosConfigController
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/7/24 18:03
 */
@RestController
@RequestMapping(value = "/v1/nacos/config")
@Slf4j
@Api(value = "NacosConfigController", tags = "NacosConfigController")
public class NacosConfigController {

    @Autowired
    private NaCosConfig naCosConfig;

    @ApiOperation("获取所有配置")
    @GetMapping("/all")
    public ResourceResponse<Map<String, Object>> getAll(
            @RequestHeader(value = "token") String userToken
    ) {
        Map<String, Object> map = new HashMap<>(16);
        PropertiesContainer.allKey().forEach(key -> map.put(key, PropertiesContainer.get(key, Object.class)));
        return ResourceResponse.ok(map);
    }

    @ApiOperation("添加配置变化监听器")
    @GetMapping("/listener/add")
    public ResourceResponse<Integer> addListener(
            @RequestParam("dataId") String dataId
            , @RequestParam("groupId") String groupId
            , @RequestParam(value = "propertiesKeys", required = false) List<String> propertiesKeys
    ) {
        CustomListener listener = new CustomListener();
        listener.setId("123456");
        int result = naCosConfig.addListener(ConfigListenerKey.builder()
                .dataId(dataId)
                .groupId(groupId)
                .propertiesKey(propertiesKeys).build(), listener);
        return ResourceResponse.ok(result);
    }

    @ApiOperation("移除配置变化监听器")
    @GetMapping("/listener/remove/all")
    public ResourceResponse<Integer> removeListener(
            @RequestParam("dataId") String dataId
            , @RequestParam("groupId") String groupId
            , @RequestParam("propertiesKeys") List<String> propertiesKeys
    ) {
        naCosConfig.removeListener(ConfigListenerKey.builder()
                .dataId(dataId)
                .groupId(groupId)
                .propertiesKey(propertiesKeys).build());
        return ResourceResponse.ok();
    }

    @ApiOperation("移除指定配置变化监听器")
    @GetMapping("/listener/remove/target")
    public ResourceResponse<Integer> removeListener(
            @RequestParam("dataId") String dataId
            , @RequestParam("groupId") String groupId
            , @RequestParam("propertiesKeys") List<String> propertiesKeys
            , @RequestParam("id") String listenerId
    ) {
        CustomListener listener = new CustomListener();
        listener.setId(listenerId);
        naCosConfig.removeListener(ConfigListenerKey.builder()
                .dataId(dataId)
                .groupId(groupId)
                .propertiesKey(propertiesKeys).build(), listener);
        return ResourceResponse.ok();
    }
}
