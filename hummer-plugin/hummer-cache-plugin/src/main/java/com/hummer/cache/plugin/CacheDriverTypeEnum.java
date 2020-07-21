package com.hummer.cache.plugin;

public enum CacheDriverTypeEnum {
    GUAVA,
    REDIS,
    GUAVA_AND_REDIS,
    UNKNOWN;

    public static CacheDriverTypeEnum getBy(String v) {
        if (GUAVA.name().equals(v)) {
            return GUAVA;
        }
        if (REDIS.name().equals(v)) {
            return REDIS;
        }

        if (GUAVA_AND_REDIS.name().equals(v)) {
            return GUAVA_AND_REDIS;
        }

        return UNKNOWN;
    }
}
