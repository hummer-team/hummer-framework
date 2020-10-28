package com.hummer.request.idempotent.plugin.pipeline;

import com.hummer.common.utils.AppBusinessAssert;
import com.hummer.core.PropertiesContainer;
import com.hummer.redis.plugin.RedisOp;
import com.hummer.request.idempotent.plugin.KeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * SimpleRedisPipeLine
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/28 10:55
 */
@Component
public class SimpleRedisPipeLine {

    @Autowired
    @Lazy
    private RedisOp redisOp;

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleRedisPipeLine.class);


    public String formatKey(String application, String businessCode, Map<String, Object> params) {
        if (Strings.isEmpty(application)) {
            application = PropertiesContainer.valueOfString("spring.application.name");
        }
        AppBusinessAssert.isTrue(StringUtils.isNotEmpty(application), 51001
                , "project application name not exist");
        return KeyUtil.formatKey(application, businessCode, params);
    }

    public boolean keyExist(String key) {

        return redisOp.set().exist(key);
    }

    public void keyStation(String key, int expireSeconds) {

        redisOp.set().set(key, "REQUEST_IDEMPOTENT_STATION", expireSeconds);
    }

    public void removeKey(String key) {

        redisOp.set().del(key);
    }
}
