package com.hummer.redis.plugin.pool;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.hummer.core.PropertiesContainer;
import com.hummer.redis.plugin.config.RedisConfig;
import com.hummer.redis.plugin.config.RedisPropertiesBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * simple redis client pool
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/10/25 14:02
 **/
public class InternalRedisPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternalRedisPool.class);
    private final Map<String, JedisPool> POOL = new ConcurrentHashMap<>();
    private final Map<String, JedisSentinelPool> SENTINEL_POOL = new ConcurrentHashMap<>();
    private final String db;

    public InternalRedisPool(final String dbName) {
        this.db = dbName;
        LOGGER.debug("create a new InternalRedisPool.dbName=={}", dbName);
    }

    private JedisPool getJedisPool() {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(db)
                , "get jedis pool db can not null.");
        JedisPool jedisPool = POOL.get(db);
        if (jedisPool != null) {
            LOGGER.debug("db {} get jedis pool for cache", db);
            return jedisPool;
        }

        synchronized (POOL) {
            jedisPool = POOL.get(db);
            if (jedisPool == null) {
                JedisPoolConfig poolConfig = RedisPropertiesBuilder.builderPoolConfig(db);
                RedisConfig.SimpleConfig redisProperties = RedisPropertiesBuilder.builderConfig(db);

                jedisPool = new JedisPool(poolConfig, redisProperties.getHost(), redisProperties.getPort()
                        , redisProperties.getTimeOut()
                        , redisProperties.getPassword()
                        , redisProperties.getDbNumber()
                        , redisProperties.getClientName());
                LOGGER.debug("db {} jedis pool instance is null,create new redis pool success, config is {}"
                        , db
                        , redisProperties);

                //add to cache
                POOL.put(db, jedisPool);
            }
        }
        return jedisPool;
    }

    private JedisSentinelPool getSentinelPool() {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(db)
                , "get sentinel pool db can not null");
        JedisSentinelPool jedisPool = SENTINEL_POOL.get(db);
        if (jedisPool != null) {
            LOGGER.debug("db {} get jedis sentinel pool for cache", db);
            return jedisPool;
        }
        synchronized (POOL) {
            jedisPool = SENTINEL_POOL.get(db);
            if (jedisPool == null) {
                RedisConfig.SentinelConfig sentinelConfig = RedisPropertiesBuilder.builderSentinelConfig(db);
                jedisPool = new JedisSentinelPool(sentinelConfig.getMasterName()
                        , sentinelConfig.getSentinelNode()
                        , RedisPropertiesBuilder.builderObjectPoolConfig(db)
                        , sentinelConfig.getTimeOut()
                        , sentinelConfig.getPassword()
                        , sentinelConfig.getDbNumber()
                        , sentinelConfig.getClientName());
                LOGGER.debug("db {} jedis sentinel pool instance is null,create new redis pool success."
                        , db);
                SENTINEL_POOL.put(db, jedisPool);
            }
        }
        return jedisPool;
    }


    private Jedis getJedisInstance() {
        String type = PropertiesContainer.valueOfString(
                String.format("%s%s%s"
                        , RedisPropertiesBuilder.perfix()
                        , db
                        , ".pool.type")
                , "simple");
        final String sentinelType = "sentinel";
        if (sentinelType.equalsIgnoreCase(type)) {
            return getSentinelPool().getResource();
        }
        return getJedisPool().getResource();
    }

    public <T> T doExecute(final SimpleFunction<T> function) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(db), "db can not null");
        Preconditions.checkArgument(function != null, "function can not null");

        Jedis jedis = null;
        T value;
        try {
            jedis = getJedisInstance();
            value = function.execute(jedis);
        } catch (Throwable throwable) {
            LOGGER.error("`{}` execute redis command failed -> \n"
                    , db
                    , throwable);
            throw throwable;
        } finally {
            //free this jedis instance
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    public <T> T doExecuteWithRetry(final SimpleFunction<T> function, final int retryCount) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        while (atomicInteger.incrementAndGet() < retryCount) {
            try {
                return doExecute(function);
            } catch (Throwable throwable) {
                if (throwable instanceof JedisConnectionException
                        || throwable.getCause() instanceof JedisConnectionException) {
                    //ignore
                } else {
                    //throw last throwable
                    if (atomicInteger.get() == retryCount - 1) {
                        throw throwable;
                    }
                }
            }
        }
        return null;
    }

    public void closeAll() {
        for (Map.Entry<String, JedisPool> entry : POOL.entrySet()) {
            entry.getValue().close();
        }

        for (Map.Entry<String, JedisSentinelPool> entry : SENTINEL_POOL.entrySet()) {
            entry.getValue().close();
        }
    }
}
