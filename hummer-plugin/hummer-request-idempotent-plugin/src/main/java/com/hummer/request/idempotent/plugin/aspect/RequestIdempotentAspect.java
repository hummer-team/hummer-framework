package com.hummer.request.idempotent.plugin.aspect;

import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import com.hummer.request.idempotent.plugin.KeyUtil;
import com.hummer.request.idempotent.plugin.annotation.RequestIdempotentAnnotation;
import com.hummer.request.idempotent.plugin.pipeline.SimpleRedisPipeLine;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * RequestIdempotentAspect
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/28 10:28
 */
@Aspect
@Component
public class RequestIdempotentAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestIdempotentAspect.class);

    @Around(" @annotation(requestIdempotent)")
    public Object requestIdempotent(ProceedingJoinPoint point, RequestIdempotentAnnotation requestIdempotent) throws Throwable {
        LOGGER.debug("request idempotent aspect : >>>> {}", point.getSignature());
        if (!requestIdempotent.enable()) {
            return point.proceed(point.getArgs());
        }
        String keyName = requestIdempotent.key();
        // 判断是否重复请求
        String keyValue = PropertiesContainer.valueOfString(keyName, MDC.get(keyName));
        if (StringUtils.isEmpty(keyValue)) {
            LOGGER.warn("request idempotent key not exist,signature=={} ", point.getSignature());
            return point.proceed(point.getArgs());
        }
        SimpleRedisPipeLine pipeLine = SpringApplicationContext.getBean(SimpleRedisPipeLine.class);
        String key = pipeLine.formatKey(requestIdempotent.applicationName()
                , requestIdempotent.businessCode(), KeyUtil.getFieldNameValueMap(point, keyName, keyValue));
        if (pipeLine.keyExist(key)) {
            LOGGER.warn("requestId =={} is repeat,key=={} ", keyName, key);
            return null;
        }
        // 站位
        pipeLine.keyStation(key, requestIdempotent.expireSeconds());
        try {
            return point.proceed(point.getArgs());
        } catch (Exception e) {
            LOGGER.debug("method=={}, proceed fail,remove key", point.getSignature());
            pipeLine.removeKey(key);
            throw e;
        }
    }

}
