package com.hummer.common.security;

import com.google.common.hash.Funnel;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

/**
 * wrapper md5 encrypt
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/7/2 11:23
 **/
public class Md5 {
    private Md5() {

    }

    /**
     * input param val be new md5 string
     *
     * @param val val
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/2 11:42
     * @since 1.0.0
     **/
    public static String encryptMd5(final String val) {
        return Hashing.md5().newHasher().putString(val, Charset.forName("utf-8")).hash().toString();
    }

    /**
     * each input parameter args string values put to hash,return new md5 string value
     *
     * @param obj array args
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/2 11:57
     * @since 1.0.0
     **/
    public static String encryptMd5(final Object... obj) {
        Hasher function = Hashing.md5().newHasher();
        return getHashString(function, obj);
    }


    /**
     * input param val be new sh256 string
     *
     * @param val val
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/2 11:42
     * @since 1.0.0
     **/
    public static String encryptSha256(final String val) {
        return Hashing.sha256().newHasher().putString(val, Charset.forName("utf-8")).hash().toString();
    }

    /**
     * each input parameter args string values put to hash,return new md5 string value
     *
     * @param obj array args
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/2 11:57
     * @since 1.0.0
     **/
    public static String encryptSha256(final Object... obj) {
        Hasher function = Hashing.sha256().newHasher();
        return getHashString(function, obj);
    }

    private static String getHashString(final Hasher function, final Object[] obj) {
        for (Object o : obj) {
            function.putObject(o, (Funnel<Object>) (from, into) ->
                    into.putString(from.toString(), Charset.forName("utf-8")));
        }

        return function.hash().toString();
    }
}
