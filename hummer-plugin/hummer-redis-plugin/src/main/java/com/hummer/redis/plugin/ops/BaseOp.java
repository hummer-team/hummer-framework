package com.hummer.redis.plugin.ops;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.hummer.redis.plugin.pool.InternalRedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bingy
 */
public abstract class BaseOp {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    protected String REDIS_DB_GROUP_NAME = "simple";

    private final Map<String, InternalRedisPool> REDIS_CLIENT_INSTANCE_MAP
            = new ConcurrentHashMap<>();

    public BaseOp() {

    }

    public BaseOp(final String redisDbGroupName) {
        REDIS_DB_GROUP_NAME = redisDbGroupName;
    }

    protected InternalRedisPool redis(final String redisName) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(redisName), "redis name can't null");
        InternalRedisPool redisInstance = REDIS_CLIENT_INSTANCE_MAP
                .get(redisName);
        if (redisInstance != null) {
            return redisInstance;
        }
        InternalRedisPool redisInstance2 = new InternalRedisPool(redisName);
        //REFERENCE.compareAndSet(null, new InternalRedisPool(redisName));
        REDIS_CLIENT_INSTANCE_MAP.put(redisName, redisInstance2);
        LOGGER.debug("{} get jedis instance for cache is missing,create new instance done", redisName);
        return Objects.requireNonNull(redisInstance2);
    }

    public Long ttlByKey(final String key) {
        return redis(REDIS_DB_GROUP_NAME).doExecute(jedis -> jedis.ttl(key));
    }

    public Long ttlByKey(final byte[] key){
        return redis(REDIS_DB_GROUP_NAME).doExecute(jedis -> jedis.ttl(key));
    }

    public Long delByKey(final String key) {
        return redis(REDIS_DB_GROUP_NAME).doExecute(jedis -> jedis.del(key));
    }

    public Long delByKey(final byte[] key){
        return redis(REDIS_DB_GROUP_NAME).doExecute(jedis -> jedis.del(key));
    }

    public void closeAll() {
        for (Map.Entry<String, InternalRedisPool> poolEntry : REDIS_CLIENT_INSTANCE_MAP.entrySet()) {
            poolEntry.getValue().closeAll();
            LOGGER.info("class redis client pool done , key is {}", poolEntry.getKey());
        }
    }
}
