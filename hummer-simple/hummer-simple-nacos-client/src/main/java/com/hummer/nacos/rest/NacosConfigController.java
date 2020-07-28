package com.hummer.nacos.rest;

import com.hummer.core.PropertiesContainer;
import com.hummer.rest.model.ResourceResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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

    @ApiOperation("获取所有配置")
    public ResourceResponse<Map<String, String>> getAll() {
        Map<String, String> map = new HashMap<>(16);
        PropertiesContainer.allKey().forEach(key -> map.put(key, PropertiesContainer.get(key, String.class)));
        return ResourceResponse.ok(map);
    }
}
