package com.hummer.request.idempotent.plugin.aspect;

import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import com.hummer.request.idempotent.plugin.KeyUtil;
import com.hummer.request.idempotent.plugin.annotation.RequestIdempotentAnnotation;
import com.hummer.request.idempotent.plugin.pipeline.SimpleRedisPipeLine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

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
@Slf4j
public class RequestIdempotentAspect {

    @Around(" @annotation(requestIdempotent)")
    public Object orderDataSync(ProceedingJoinPoint point, RequestIdempotentAnnotation requestIdempotent) throws Throwable {
        log.debug("request idempotent aspect : >>>> {}", point.getSignature());
        if (!requestIdempotent.enable()) {
            return point.proceed(point.getArgs());
        }
        String keyName = requestIdempotent.key();
        // 判断是否重复请求
        String keyValue = PropertiesContainer.valueOfString(keyName, MDC.get(keyName));
        if (StringUtils.isEmpty(keyValue)) {
            log.warn("request idempotent key not exist,signature=={} ", point.getSignature());
            return point.proceed(point.getArgs());
        }
        SimpleRedisPipeLine pipeLine = SpringApplicationContext.getBean(SimpleRedisPipeLine.class);
        String key = pipeLine.formatKey(requestIdempotent.applicationName()
                , requestIdempotent.businessCode(), KeyUtil.getFieldNameValueMap(point, keyName, keyValue));
        if (pipeLine.keyExist(key)) {
            log.warn("requestId =={} is repeat,key=={} ", keyName, key);
            return null;
        }
        // 站位
        pipeLine.keyStation(key);
        try {
            return point.proceed(point.getArgs());
        } catch (Exception e) {
            log.debug("method=={}, proceed fail,remove key", point.getSignature());
            pipeLine.removeKey(key);
            throw e;
        }
    }

    private void assertMethodApi(ProceedingJoinPoint point) {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
    }

}
