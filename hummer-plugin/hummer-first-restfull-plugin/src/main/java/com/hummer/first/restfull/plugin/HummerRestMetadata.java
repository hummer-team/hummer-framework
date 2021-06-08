package com.hummer.first.restfull.plugin;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author edz
 */
public class HummerRestMetadata {
    private static final ConcurrentHashMap<String, Class<?>> METADATA_MAP = new ConcurrentHashMap<>();

    private HummerRestMetadata() {

    }

    public static void put(String key, Class<?> aClass) {
        METADATA_MAP.put(key, aClass);
    }

    public static Class<?> get(String key) {
        return METADATA_MAP.get(key);
    }
}
