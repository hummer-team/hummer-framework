package com.hummer.redis.plugin.pool;

import redis.clients.jedis.Jedis;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/10/25 15:47
 **/
@FunctionalInterface
public interface SimpleFunction<T> {
    /**
     * execute redis command
     *
     * @param jedis jedis instance
     * @return
     */
    T execute(final Jedis jedis);
}
