package com.hummer.config.api;

import com.hummer.config.NaCosConfig;
import com.hummer.core.PropertiesContainer;
import com.hummer.rest.model.ResourceResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置管理API集
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/7/28 15:41
 */
@Api(value = "配置管理API集", tags = "配置管理API集")
@RestController
@RequestMapping("/v1/config/manage")
public class ConfigController {
    @Autowired
    private NaCosConfig naCosConfig;

    @ApiOperation("查看当前所有配置信息")
    @GetMapping(value = "/all", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResourceResponse<Map<String, String>> getAllConf() {

        Map<String, String> map = new HashMap<>(16);
        PropertiesContainer.allKey().forEach(key -> map.put(key, PropertiesContainer.get(key, String.class)));
        return ResourceResponse.ok(map);
    }

    @ApiOperation("从配置中心获取最新配置")
    @GetMapping("/refresh")
    public ResourceResponse<Boolean> refreshConf() throws Exception {

        naCosConfig.putConfigToContainer(false);
        return ResourceResponse.ok();
    }


}
