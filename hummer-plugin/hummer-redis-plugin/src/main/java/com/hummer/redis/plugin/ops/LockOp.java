package com.hummer.redis.plugin.ops;

import com.hummer.common.utils.IpUtil;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

/**
 * use redis impeachment lock
 *
 * @author bingy
 */
public class LockOp extends BaseOp<LockOp> {
    private String redisDbGroupName;
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "EX";

    public LockOp() {
        super();
        this.redisDbGroupName = REDIS_DB_GROUP_NAME;
        redis(this.redisDbGroupName);
    }

    public LockOp(final String redisDbGroupName) {
        super(redisDbGroupName);
        this.redisDbGroupName = redisDbGroupName;
        redis(this.redisDbGroupName);
    }


    public boolean lock(final String key
            , final int lockExpiredSecond) {
        return lock(key, lockExpiredSecond, -1);
    }

    public boolean lock(final String key
            , final int lockExpiredSecond
            , final int waitTimeOutSecond) {
        if (waitTimeOutSecond < 0) {
            return acquire(key, getLockValue(), lockExpiredSecond);
        }

        final long end = System.currentTimeMillis() + waitTimeOutSecond * 1000;
        while (System.currentTimeMillis() < end) {
            if (acquire(key, getLockValue(), lockExpiredSecond)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkReentrantLock(final String key, final String lockValue) {
        String result = redis(redisDbGroupName).doExecute(jeds -> jeds.get(key));
        return lockValue.equals(result);
    }

    private boolean acquire(final String key, final String lockObjKey, final int lockExpiredSecond) {
        if (checkReentrantLock(key, lockObjKey)) {
            return true;
        }

        final String result = redis(redisDbGroupName).doExecute(jedis ->
                jedis.set(key
                        , lockObjKey
                        , SetParams.setParams().nx().ex(lockExpiredSecond)));

        final String ok = "OK";
        LOGGER.debug("key {} acquire lock result {}", key, result);
        return ok.equalsIgnoreCase(result);
    }

    public boolean freeLock(final String lockObjKey) {
        final String freeLockStr = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                "return redis.call('del', KEYS[1]) " +
                "else " +
                "return 0 " +
                "end";
        final String value = getLockValue();
        final Object result = redis(redisDbGroupName).doExecute(jedis -> jedis.eval(freeLockStr
                , Collections.singletonList(lockObjKey)
                , Collections.singletonList(value)));
        final Long ok = 1L;

        return ok.equals(result);
    }

    private String getLockValue() {
        return String.format("%s-%d", IpUtil.getLocalIp(), Thread.currentThread().getId());
    }
}
