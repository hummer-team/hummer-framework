package com.hummer.request.idempotent.plugin.valid;

import com.alibaba.fastjson.JSON;
import com.hummer.core.SpringApplicationContext;
import com.hummer.request.idempotent.plugin.KeyUtil;
import com.hummer.request.idempotent.plugin.annotation.RequestIdempotentAnnotation;
import com.hummer.request.idempotent.plugin.pipeline.SimpleRedisPipeLine;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Map;

/**
 * ParamsIdempotentValidator
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/1/20 17:11
 */
@Component
public class ParamsIdempotentValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParamsIdempotentValidator.class);

    public Map<String, String> getValidKey(ProceedingJoinPoint joinPoint
            , RequestIdempotentAnnotation requestIdempotent) {
        ValidParamsAssembler assembler = SpringApplicationContext.getBean(requestIdempotent.validParamsAssembler());
        Map<String, String> validParams;
        if (assembler instanceof DefaultValidParamsAssembler) {
            validParams = assembler.assemble(requestIdempotent.key());
        } else {
            validParams = assembler.assemble(joinPoint.getArgs());
        }
        return validParams;
    }

    public boolean validParamsIdempotent(String key, int expireSeconds, Map<String, String> validParams) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        SimpleRedisPipeLine pipeLine = SpringApplicationContext.getBean(SimpleRedisPipeLine.class);
        String lockKey = KeyUtil.formatLockKey(key);
        if (!pipeLine.getShipCodeCreatedLock(lockKey)) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("request idempotent get lock fail key=={},validParams=={}", key
                        , JSON.toJSONString(validParams));
            }
            return true;
        }
        try {
            if (pipeLine.keyExist(key)) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("request idempotent valid fail ,key=={},validParams=={} ", key
                            , JSON.toJSONString(validParams));
                }
                return true;
            }
            // 站位
            pipeLine.keyStation(key, expireSeconds);
        } catch (JedisConnectionException e) {
            LOGGER.error("get redis connection fail key=={}", key, e);
            return false;
        } finally {
            pipeLine.releaseLockAsyncRetry(lockKey);
        }
        return false;
    }

    public void removeValidKey(String key) {
        if (StringUtils.isEmpty(key)) {
            return;
        }
        SimpleRedisPipeLine pipeLine = SpringApplicationContext.getBean(SimpleRedisPipeLine.class);
        String lockKey = KeyUtil.formatLockKey(key);
        if (!pipeLine.getShipCodeCreatedLock(lockKey)) {
            LOGGER.warn("request idempotent remove valid key get lock fail key=={},", key);
            return;
        }
        try {
            pipeLine.removeRedisKeyRetry(key);
        } finally {
            pipeLine.releaseLockAsyncRetry(lockKey);
        }
    }

}
