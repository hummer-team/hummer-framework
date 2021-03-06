package com.hummer.user.auth.plugin.holder;

import com.hummer.common.holder.HummerContextMapHolder;

public class RequestContextHolder {
    private static final HummerContextMapHolder MAP_HOLDER = new HummerContextMapHolder();

    private RequestContextHolder() {

    }

    public static void clearHolder() {
        MAP_HOLDER.clearHolder();
    }

    public static String get(String key) {
        key = key == null ? null : key.toLowerCase();
        return MAP_HOLDER.get(key);
    }

    public static void set(String key, String value) {
        key = key == null ? null : key.toLowerCase();
        MAP_HOLDER.set(key, value);
    }

    public static int size() {
        return MAP_HOLDER.size();
    }
}
