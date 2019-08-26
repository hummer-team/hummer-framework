package com.hummer.common.utils;

import java.nio.ByteBuffer;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/26 16:33
 **/
public class LongUtil {
    private LongUtil() {

    }

    /**
     * convert long to byte array
     *
     * @param value value
     * @return byte[]
     * @author liguo
     * @date 2019/8/26 16:37
     * @since 1.0.0
     **/
    public static byte[] convertToBytes(final long value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        byteBuffer.putLong(value);
        return byteBuffer.array();
    }

    /**
     * convert byte array to long
     *
     * @param value value
     * @return long
     * @author liguo
     * @date 2019/8/26 16:37
     * @since 1.0.0
     **/
    public static long convertToLong(final byte[] value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        byteBuffer.put(value);
        return byteBuffer.getLong();
    }

}
