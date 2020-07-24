package com.hummer.redis.plugin.ops;

import com.google.common.collect.Maps;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * provide redis hash simple operation
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/10/25 14:00
 **/
public class HashSimpleOp extends BaseOp<HashSimpleOp> {
    private String redisDbGroupName;

    public HashSimpleOp() {
        super();
        this.redisDbGroupName = REDIS_DB_GROUP_NAME;
        redis(this.redisDbGroupName);
    }

    public HashSimpleOp(final String redisDbGroupName) {
        super(redisDbGroupName);
        this.redisDbGroupName = redisDbGroupName;
        redis(this.redisDbGroupName);
    }

    public Map<String, String> getAll(final String hashName) {
        return redis(redisDbGroupName).doExecute(jedis -> jedis.hgetAll(hashName));
    }

    public Map<byte[], byte[]> getAllValueOfBytes(final byte[] hashName) {
        return redis(redisDbGroupName).doExecute(jedis -> jedis.hgetAll(hashName));
    }

    public byte[] getValueOfBytesByFieldKey(final byte[] hashName, final byte[] fieldKey) {
        return redis(redisDbGroupName).doExecute(jedis -> jedis.hget(hashName, fieldKey));
    }

    public boolean checkHashExistsKey(final String hashName, final String fieldKey) {
        return redis(redisDbGroupName).doExecute(jedis -> jedis.hexists(hashName, fieldKey));
    }

    public boolean checkHashExistsKey(final byte[] hashName, final byte[] fieldKey) {
        return redis(redisDbGroupName).doExecute(jedis -> jedis.hexists(hashName, fieldKey));
    }

    public String getByFieldKey(final String hashName, final String fieldKey) {
        LOGGER.debug("`{}` hashName->{},fieldKey->{}", redisDbGroupName, hashName, fieldKey);
        return redis(redisDbGroupName).doExecute(jedis -> jedis.hget(hashName, fieldKey));
    }

    public long hset(final byte[] hashName, final byte[] fieldKey, final byte[] value) {
        return redis(redisDbGroupName).doExecute(jedis -> jedis.hset(hashName, fieldKey, value));
    }

    public void hset(final byte[] hashName
            , final byte[] fieldKey
            , final byte[] value
            , final Integer expireSecond
            , final Boolean transaction) {
        final Map<byte[], byte[]> map = Maps.newHashMapWithExpectedSize(1);
        map.put(fieldKey, value);
        hsetMultipleByPipeline(hashName, map, expireSecond, transaction);
    }

    public void hsetWithExpireByTran(final byte[] hashName, final byte[] fieldKey, final byte[] value
            , final int second) {
        redis(redisDbGroupName).doExecute(jedis -> {
            Transaction transaction = jedis.multi();
            transaction.hset(hashName, fieldKey, value);
            transaction.expire(hashName, second);
            List<Object> result = transaction.exec();
            LOGGER.debug("`{}` mutlti server resp->{}, hashName->{},fieldKey->{},value->{}"
                    , redisDbGroupName
                    , result
                    , hashName
                    , fieldKey
                    , value);
            return null;
        });
    }

    public long hset(final String hashName, final String fieldKey, final String value) {
        return redis(redisDbGroupName).doExecute(jedis -> {
            final long result = jedis.hset(hashName, fieldKey, value);
            LOGGER.debug("`{}` hset server resp->{}, hashName->{},fieldKey->{},value->{}"
                    , redisDbGroupName
                    , result
                    , hashName
                    , fieldKey
                    , value);
            return result;
        });
    }

    public void hsetMultipleByTran(final String hashName
            , final Map<String, String> map) {
        hsetMultipleByTran(hashName, map, null);
    }

    public void hsetMultipleByTran(final byte[] hashName
            , final Map<byte[], byte[]> map) {
        hsetMultipleByTran(hashName, map, null);
    }

    public void hsetMultipleByPipeline(final byte[] hashName
            , final Map<byte[], byte[]> map
            , final Integer expireSecond
            , Boolean transaction) {
        redis(redisDbGroupName).doExecute(jedis -> {
            Pipeline pipeline = jedis.pipelined();
            if (transaction) {
                pipeline.multi();
            }
            for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
                pipeline.hset(hashName, entry.getKey(), entry.getValue());
            }
            if (expireSecond != null && expireSecond > 0) {
                pipeline.expire(hashName, expireSecond);
            }
            Response<List<Object>> result = null;
            if (transaction) {
                result = pipeline.exec();
            }
            pipeline.sync();
            LOGGER.debug("`{}` [multi] map is binary server resp->{}, hashName->{},item size->{},expireSecond->{}"
                    , redisDbGroupName
                    , result
                    , hashName
                    , map.size()
                    , expireSecond);
            return null;
        });
    }

    public void hsetMultipleByTran(final byte[] hashName
            , final Map<byte[], byte[]> map
            , final Integer expireSecond) {
        redis(redisDbGroupName).doExecute(jedis -> {
            Transaction transaction = jedis.multi();
            for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
                transaction.hset(hashName, entry.getKey(), entry.getValue());
            }
            if (expireSecond != null && expireSecond > 0) {
                transaction.expire(hashName, expireSecond);
            }
            List<Object> result = transaction.exec();
            LOGGER.debug("`{}` [multi] map is binary server resp->{}, hashName->{},item size->{},expireSecond->{}"
                    , redisDbGroupName
                    , result
                    , hashName
                    , map.size()
                    , expireSecond);
            return null;
        });
    }

    public void hsetMultipleByPipeline(final String hashName
            , final Map<String, String> map
            , final Integer expireSecond
            , Boolean transaction) {
        redis(redisDbGroupName).doExecute(jedis -> {
            Pipeline pipeline = jedis.pipelined();
            if (Boolean.TRUE.equals(transaction)) {
                pipeline.multi();
            }
            for (Map.Entry<String, String> entry : map.entrySet()) {
                pipeline.hset(hashName, entry.getKey(), entry.getValue());
            }
            if (expireSecond != null && expireSecond > 0) {
                pipeline.expire(hashName, expireSecond);
            }
            Response<List<Object>> result = null;
            if (Boolean.TRUE.equals(transaction)) {
                result = pipeline.exec();
            }
            List<Object> result2 = Collections.emptyList();
            if (LOGGER.isDebugEnabled()) {
                result2 = pipeline.syncAndReturnAll();
            } else {
                pipeline.sync();
            }

            LOGGER.debug("`{}` [pipeline tran - {}] map item is string," +
                            " server resp->{}-{}, hashName->{},item size->{},expireSecond->{}"
                    , redisDbGroupName
                    , transaction
                    , result
                    , result2
                    , hashName
                    , map.size()
                    , expireSecond);
            return null;
        });
    }

    public void hsetMultipleByTran(final String hashName
            , final Map<String, String> map
            , final Integer expireSecond) {
        redis(redisDbGroupName).doExecute(jedis -> {
            Transaction transaction = jedis.multi();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                transaction.hset(hashName, entry.getKey(), entry.getValue());
            }
            if (expireSecond != null && expireSecond > 0) {
                transaction.expire(hashName, expireSecond);
            }
            List<Object> result = transaction.exec();
            LOGGER.debug("`{}` [multi] map item is string," +
                            " server resp->{}, hashName->{},item size->{},expireSecond->{}"
                    , redisDbGroupName
                    , result
                    , hashName
                    , map.size()
                    , expireSecond);
            return null;
        });
    }

    public long hsetWithExpireByTran(final String hashName
            , final String fieldKey
            , final String value
            , int second) {
        return redis(redisDbGroupName).doExecute(jedis -> {
            Transaction transaction = jedis.multi();
            transaction.hset(hashName, fieldKey, value);
            transaction.expire(hashName, second);
            transaction.exec();
            return 1L;
        });
    }
}
