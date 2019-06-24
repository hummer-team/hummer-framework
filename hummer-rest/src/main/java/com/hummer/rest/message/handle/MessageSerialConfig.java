package com.hummer.rest.message.handle;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.support.config.FastJsonConfig;

/**
 * this interface defined fast json config,business can customer settings property
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/24 17:05
 **/
public interface MessageSerialConfig {
    /**
     * serial config
     *
     * @param serializeConfig
     * @return void
     * @author liguo
     * @date 2019/6/24 17:07
     * @version 1.0.0
     **/
    void register(SerializeConfig serializeConfig);

    /**
     * fast json all serial feature
     *
     * @param fastJsonConfig
     * @return void
     * @author liguo
     * @date 2019/6/24 17:08
     * @version 1.0.0
     **/
    void register(FastJsonConfig fastJsonConfig);
}
