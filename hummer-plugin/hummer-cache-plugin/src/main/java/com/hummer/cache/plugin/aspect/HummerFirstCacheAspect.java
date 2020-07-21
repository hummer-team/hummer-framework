package com.hummer.cache.plugin.aspect;

import com.hummer.cache.plugin.CacheDriverTypeEnum;
import com.hummer.cache.plugin.HummerSimpleObjectCache;
import com.hummer.cache.plugin.KeyUtil;
import com.hummer.cache.plugin.driver.SimpleGuavaCache;
import com.hummer.cache.plugin.driver.SimpleRedisCache;
import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @author edz
 */
@Aspect
@Component
@Slf4j
public class HummerFirstCacheAspect {


    @Around("@annotation(cache)")
    public Object loadCacheData(ProceedingJoinPoint point, HummerSimpleObjectCache cache)
            throws Throwable {
        if (!cache.enable()) {
            return point.proceed(point.getArgs());
        }

        CacheDriverTypeEnum driverTypeEnum = getCacheDriverType(cache);
        if (driverTypeEnum == CacheDriverTypeEnum.GUAVA) {
            SimpleGuavaCache guavaCache =
                    SpringApplicationContext.getBean(SimpleGuavaCache.class);
            return guavaCache.getOrSet(guavaCache.formatKey(
                    cache.applicationName()
                    , cache.businessCode()
                    , KeyUtil.getFieldNameValueMap(point))
                    , () -> point.proceed(point.getArgs()));
        }

        if (driverTypeEnum == CacheDriverTypeEnum.REDIS) {
            SimpleRedisCache redisCache =
                    SpringApplicationContext.getBean(SimpleRedisCache.class);
            return redisCache.getOrSet(redisCache.formatKey(
                    cache.applicationName()
                    , cache.businessCode()
                    , KeyUtil.getFieldNameValueMap(point))
                    , cache.timeoutSeconds()
                    , () -> point.proceed(point.getArgs())
                    , ((MethodSignature)point.getSignature()).getReturnType());
        }


        return point.proceed(point.getArgs());
    }


    private CacheDriverTypeEnum getCacheDriverType(HummerSimpleObjectCache cache) {
        String driver = PropertiesContainer.valueOfStringWithAssertNotNull(String.format("%s.%s.cache.store.type"
                , cache.applicationName()
                , cache.businessCode()));
        return CacheDriverTypeEnum.getBy(driver);
    }
}
