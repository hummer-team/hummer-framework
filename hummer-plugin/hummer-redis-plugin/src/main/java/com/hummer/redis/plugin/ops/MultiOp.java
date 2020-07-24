package com.hummer.redis.plugin.ops;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.Collections;
import java.util.List;

@Slf4j
public class MultiOp extends BaseOp<MultiOp> {
    private String redisDbGroupName;

    public MultiOp() {
        super();
        this.redisDbGroupName = REDIS_DB_GROUP_NAME;
        redis(this.redisDbGroupName);
    }

    public MultiOp(final String redisDbGroupName) {
        super(redisDbGroupName);
        this.redisDbGroupName = redisDbGroupName;
        redis(this.redisDbGroupName);
    }

    public void setAndHsetByPipeline(final String key
            , final String value
            , final String hashName
            , final int expireSecond) {
        setAndHsetByPipeline(key, value, hashName, expireSecond, true);
    }

    public void setAndHsetByPipeline(final String key
            , final String value
            , final String hashName
            , final int expireSecond
            , Boolean transaction) {
        redis(redisDbGroupName).doExecute(jedis -> {
            Pipeline pipeline = jedis.pipelined();
            if (Boolean.TRUE.equals(transaction)) {
                pipeline.multi();
            }
            Response<String> setResp = pipeline.setex(key, expireSecond, value);
            log.debug("set op key is :{},expire second {},redis resp {}"
                    , key, expireSecond, setResp);
            Response<Long> hashResp = pipeline.hset(hashName, key, "NL");
            Response<Long> hashExResp = pipeline.expire(hashName, expireSecond);
            log.debug("hash set op key is :{},expire second {},redis resp {},{}"
                    , key, expireSecond, hashResp, hashExResp);

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

            LOGGER.debug("`{}` [set and hash set pipeline tran - {}] map item is binary," +
                            " server resp->{}-{},expireSecond->{}"
                    , redisDbGroupName
                    , transaction
                    , result
                    , result2
                    , expireSecond);
            return null;
        });
    }
}
