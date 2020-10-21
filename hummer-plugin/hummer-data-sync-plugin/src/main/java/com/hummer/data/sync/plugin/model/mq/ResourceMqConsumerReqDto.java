package com.hummer.data.sync.plugin.model.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * ResourceMqConsumerReqDto
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/9/22 14:26
 */
@Setter
@Getter
public class ResourceMqConsumerReqDto<T> {

    @ApiModelProperty("businessId")
    private String businessId;

    @ApiModelProperty("业务数据body")
    private byte[] bodys;

    @ApiModelProperty("业务数据header")
    private Map<String, String> head;

    @ApiModelProperty("消息属性")
    private MqPropertiesReqDto properties;


    public T getBodys(TypeReference<T> reference) {

        return JSON.parseObject(new String(bodys, StandardCharsets.UTF_8), reference);
    }
}
