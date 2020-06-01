package com.hummer.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 常用方法
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/5/8 15:59
 */
public class SqlUtils {

    public static String createLeftLike(String keyword) {
        return StringUtils.isEmpty(keyword) ? keyword : keyword + "%";
    }

    public static String createRightLike(String keyword) {
        return StringUtils.isEmpty(keyword) ? keyword : "%" + keyword;
    }

    public static String createBlurLike(String keyword) {
        return StringUtils.isEmpty(keyword) ? keyword : "%" + keyword + "%";
    }
}
