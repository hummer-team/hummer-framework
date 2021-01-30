package com.hummer.request.idempotent.plugin.pipeline;

import com.hummer.common.utils.AppBusinessAssert;
import com.hummer.core.PropertiesContainer;
import com.hummer.redis.plugin.RedisOp;
import com.hummer.request.idempotent.plugin.KeyUtil;
import com.hummer.request.idempotent.plugin.constants.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

    public Long removeKey(String key) {
        return redisOp.set().del(key);
    }

    public void removeRedisKeyRetry(String key) {
        try {
            Long result = removeKey(key);
            if (result == null) {
                removeKey(key);
            }
        } catch (Exception e) {
            LOGGER.warn("redis key == {},remove fail", key);
            removeKey(key);
        }
    }

    public boolean getShipCodeCreatedLock(String key) {
        try {
            return redisOp.lock().lock(key, Constants.REDIS_ADD_LOCK_TIME_SECONDS);
        } catch (JedisConnectionException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("get redis lock fail key=={}", key, e);
            return false;
        }
    }

    public boolean releaseLockAsyncRetry(String key) {
        if (!releaseLock(key)) {
            CompletableFuture.runAsync(() -> {
                int times = PropertiesContainer.valueOfInteger("ship.order.code.lock.release.fail.retry.times", 20);
                while (times > 0 && !releaseLock(key)) {
                    times--;
                }
            });
        }
        LOGGER.debug("redis freeLock success key=={}", key);
        return true;
    }

    private boolean releaseLock(String key) {
        try {
            return redisOp.lock().freeLock(key);
        } catch (Exception e) {
            LOGGER.error("redis freeLock fail key=={}", key, e);
            return false;
        }
    }
}
