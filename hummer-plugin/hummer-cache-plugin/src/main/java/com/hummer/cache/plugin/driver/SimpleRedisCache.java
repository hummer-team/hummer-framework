package com.hummer.cache.plugin.driver;

import com.alibaba.fastjson.JSON;
import com.hummer.cache.plugin.KeyUtil;
import com.hummer.cache.plugin.SupplierEx;
import com.hummer.common.SysConstant;
import com.hummer.redis.plugin.RedisOp;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class SimpleRedisCache {
    @Autowired
    @Lazy
    private RedisOp redisOp;

    public Object getOrSet(
            final String key
            , final int timeoutSeconds
            , final SupplierEx noExistsAction
            , final Class<?> returnCls
    ) throws Throwable {
        String result = null;
        long start = System.currentTimeMillis();
        try {
            result = redisOp.set().getKey(key);
            log.debug("get data for redis success,cost {} mills,key is {},data is{}"
                    , System.currentTimeMillis() - start
                    , key
                    , result);
        } catch (Throwable e) {
            log.warn("get data for redis failed,need execute noExistsAction");
        }
        if (Strings.isEmpty(result)) {
            Object o = noExistsAction.get();
            log.debug("redis cache missed,this key is {},need execute business method cost {} millis"
                    , key
                    , System.currentTimeMillis() - start);
            if (o != null) {
                set(key, timeoutSeconds, o);
            }
            return o;
        }
        return JSON.parseObject(result, returnCls);
    }

    public void set(final String key, final int timeoutSeconds, final Object o) {
        String requestId = MDC.get(SysConstant.REQUEST_ID);
        String serverIp = MDC.get(SysConstant.RestConstant.SERVER_IP);
        CompletableFuture.runAsync(() -> {
            MDC.put(SysConstant.REQUEST_ID, requestId);
            MDC.put(SysConstant.RestConstant.SERVER_IP, serverIp);
            long start = System.currentTimeMillis();
            String result = redisOp.set().set(key, JSON.toJSONString(o), timeoutSeconds);
            log.debug("async add to redis done,cost {} millis,redis result {},key is {}"
                    , System.currentTimeMillis() - start
                    , result, key);
        });
    }

    public void set(final String key, final String value, final int timeoutSeconds) {
        
        redisOp.set().set(key, value, timeoutSeconds);
    }

    public String get(final String key) {

        return redisOp.set().getKey(key);
    }

    public String formatKey(final String nameSpace
            , final String businessCode
            , final Map<String, Object> parameterMap) {
        if (Strings.isEmpty(businessCode)) {
            throw new IllegalArgumentException("hummer cache business code can not null");
        }
        return KeyUtil.formatKey(nameSpace, businessCode, parameterMap);
    }
}
