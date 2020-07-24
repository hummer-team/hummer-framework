package com.hummer.redis.plugin.ops;

import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author bingy
 */
public class SetSimpleOp extends BaseOp<SetSimpleOp> {
    private String redisDbGroupName;

    public SetSimpleOp() {
        super();
        this.redisDbGroupName = REDIS_DB_GROUP_NAME;
        redis(this.redisDbGroupName);
    }

    public SetSimpleOp(final String redisDbGroupName) {
        super(redisDbGroupName);
        this.redisDbGroupName = redisDbGroupName;
        redis(this.redisDbGroupName);
    }


    public String getKey(final String key) {
        return redis(redisDbGroupName).doExecute(jedis -> jedis.get(key));
    }

    public byte[] getKey(final byte[] key) {
        return redis(redisDbGroupName).doExecute(jedis -> jedis.get(key));
    }

    public String set(final String key, final String value) {
        return redis(REDIS_DB_GROUP_NAME).doExecute(jedis -> jedis.set(key, value));
    }

    public String set(final byte[] key, final byte[] value) {
        return redis(REDIS_DB_GROUP_NAME).doExecute(jedis -> jedis.set(key, value));
    }

    public String set(final String key, final String value, final int seconds) {
        return redis(REDIS_DB_GROUP_NAME).doExecute(jedis -> jedis.setex(key, seconds, value));
    }

    public String set(final byte[] key, final byte[] value, final int seconds) {
        return redis(REDIS_DB_GROUP_NAME).doExecute(jedis -> jedis.setex(key, seconds, value));
    }

    public void setMultipleBinaryByPipeline(
            final Map<byte[], byte[]> map
            , final int expireSecond
            , Boolean transaction) {
        redis(redisDbGroupName).doExecute(jedis -> {
            Pipeline pipeline = jedis.pipelined();
            if (Boolean.TRUE.equals(transaction)) {
                pipeline.multi();
            }

            for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
                if (expireSecond > 0) {
                    pipeline.setex(entry.getKey(), expireSecond, entry.getValue());
                } else {
                    pipeline.set(entry.getKey(), entry.getValue());
                }
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

            LOGGER.debug("`{}` [set pipeline tran - {}] map item is binary," +
                            " server resp->{}-{},item size->{},expireSecond->{}"
                    , redisDbGroupName
                    , transaction
                    , result
                    , result2
                    , map.size()
                    , expireSecond);
            return null;
        });
    }

    public void setMultipleStringByPipeline(
            final Map<String, String> map
            , final int expireSecond
            , Boolean transaction) {
        redis(redisDbGroupName).doExecute(jedis -> {
            Pipeline pipeline = jedis.pipelined();
            if (Boolean.TRUE.equals(transaction)) {
                pipeline.multi();
            }

            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (expireSecond > 0) {
                    pipeline.setex(entry.getKey(), expireSecond, entry.getValue());
                } else {
                    pipeline.set(entry.getKey(), entry.getValue());
                }
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

            LOGGER.debug("`{}` [set pipeline tran - {}] map item is string," +
                            " server resp->{}-{},item size->{},expireSecond->{}"
                    , redisDbGroupName
                    , transaction
                    , result
                    , result2
                    , map.size()
                    , expireSecond);
            return null;
        });
    }
}
