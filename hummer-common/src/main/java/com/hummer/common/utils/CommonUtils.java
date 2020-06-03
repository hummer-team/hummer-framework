package com.hummer.common.utils;

/**
 * CommonUtils
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/6/2 13:32
 */
public class CommonUtils {

    /**
     * ifNull return deft , else return resource
     *
     * @param resource
     * @param deft
     * @return T
     * @author chen wei
     * @date 2020/6/2
     */
    public static <T> T ifNullDefault(T resource, T deft) {
        if (resource == null) {
            return deft;
        }
        return resource;
    }
}
