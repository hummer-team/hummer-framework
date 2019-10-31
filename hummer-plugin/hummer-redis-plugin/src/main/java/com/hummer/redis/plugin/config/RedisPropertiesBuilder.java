package com.hummer.redis.plugin.config;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.hummer.core.PropertiesContainer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * redis config
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/10/25 14:28
 **/
public class RedisPropertiesBuilder {

    private static final String REDIS_PREFIX="redis.";
    
    private RedisPropertiesBuilder() {

    }

    public static String perfix(){
        return REDIS_PREFIX;
    }

    public static RedisConfig.SimpleConfig builderConfig(final String dbGroup) {
        return RedisConfig.SimpleConfig
                .builder()
                .dbNumber(PropertiesContainer.valueOfInteger(String.format("%s%s%s"
                        , REDIS_PREFIX
                        , dbGroup
                        , ".ds")
                        , Protocol.DEFAULT_DATABASE))
                .host(PropertiesContainer.valueOfStringWithAssertNotNull(String.format("%s%s%s"
                        , REDIS_PREFIX, dbGroup, ".host")))
                .port(PropertiesContainer.valueOfInteger(String.format("%s%s%s", REDIS_PREFIX, dbGroup, ".port")
                        , Protocol.DEFAULT_PORT))
                .timeOut(PropertiesContainer.valueOfInteger(String.format("%s%s%s", REDIS_PREFIX, dbGroup, ".timeout")
                        , Protocol.DEFAULT_TIMEOUT))
                .password(PropertiesContainer.valueOfString(String.format("%s%s%s"
                        , REDIS_PREFIX
                        , dbGroup
                        , ".password")
                        , null))
                .clientName(PropertiesContainer.valueOfString(String.format("%s%s%s"
                        , REDIS_PREFIX
                        , dbGroup
                        , ".clientname")
                        , "hummer-plugin"))
                .build();
    }

    public static JedisPoolConfig builderPoolConfig(final String dbGroup) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(PropertiesContainer.valueOfInteger(String.format("%s%s%s"
                , REDIS_PREFIX
                , dbGroup
                , ".pool.max-idle")
                , 10));
        config.setMaxTotal(PropertiesContainer.valueOfInteger(String.format("%s%s%s"
                , REDIS_PREFIX
                , dbGroup
                , ".pool.max-total")
                , 50));
        config.setMaxWaitMillis(PropertiesContainer.valueOf(String.format("%s%s%s"
                , REDIS_PREFIX
                , dbGroup
                , ".pool.max-wait")
                , Long.class
                , 30000L));
        return config;
    }

    public static RedisConfig.SentinelConfig builderSentinelConfig(final String dbGroup) {
        return RedisConfig.SentinelConfig
                .builder()
                .masterName(PropertiesContainer.valueOfString(String.format("%s%s%s", REDIS_PREFIX, dbGroup, ".master")))
                .password(PropertiesContainer.valueOfString(String.format("%s%s%s"
                        , REDIS_PREFIX
                        , dbGroup
                        , ".password")
                        , null))
                .sentinelNode(Sets.newHashSet(
                        Splitter
                                .on(",")
                                .split(PropertiesContainer.valueOfStringWithAssertNotNull(String.format("%s%s%s"
                                        , REDIS_PREFIX
                                        , dbGroup
                                        , ".host")))))
                .timeOut(PropertiesContainer.valueOf(String.format("%s%s%s", REDIS_PREFIX, dbGroup, ".timeout")
                        , Integer.class
                        , Protocol.DEFAULT_TIMEOUT))
                .clientName(PropertiesContainer.valueOfString(String.format("%s%s%s"
                        , REDIS_PREFIX
                        , dbGroup
                        , ".clientname")
                        , "hummer-redis-plugin"))
                .dbNumber(PropertiesContainer.valueOfInteger(String.format("%s%s%s"
                        , REDIS_PREFIX
                        , dbGroup
                        , ".key.prefix")
                        , Protocol.DEFAULT_DATABASE))

                .build();
    }

    public static GenericObjectPoolConfig builderObjectPoolConfig(final String dbGroup) {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        JedisPoolConfig config = builderPoolConfig(dbGroup);
        poolConfig.setMaxIdle(config.getMaxIdle());
        poolConfig.setMaxTotal(config.getMaxTotal());
        poolConfig.setMaxWaitMillis(config.getMaxWaitMillis());
        return poolConfig;
    }
}
