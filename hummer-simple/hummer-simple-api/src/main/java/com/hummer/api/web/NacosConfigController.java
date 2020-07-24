package com.hummer.api.web;

import com.hummer.core.PropertiesContainer;
import com.hummer.rest.model.ResourceResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目配置nacos管理
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/7/24 10:45
 */
@RestController
@RequestMapping("nacos/config")
@Api(value = "项目配置nacos管理", tags = "项目配置nacos管理")
public class NacosConfigController {

    @ApiOperation("获取当前所有配置")
    @GetMapping("/all")
    public ResourceResponse<Map<String, String>> getAllConfig() {
        Map<String, String> map = new HashMap<>(16);
        PropertiesContainer.allKey().forEach(key -> map.put(key, PropertiesContainer.get(key, String.class)));
        return ResourceResponse.ok(map);
    }
}
