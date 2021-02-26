package com.hummer.yug.tools.plugin.util;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * MemberUtil
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/12/30 11:49
 */
public class MemberUtil {

    public static String composeCustomerId(@NotNull String sid) {

        return sortString(3, sid);
    }

    public static String parseCustomerId(@NotNull String customerId) {

        return sortString(3, customerId);
    }

    private static String sortString(String s) {
        return sortString(0, s);
    }

    private static String sortString(int startIndex, String s) {
        if (StringUtils.isEmpty(s)) {
            return s;
        }
        char[] chars = s.toCharArray();
        if (startIndex >= chars.length / 2) {
            startIndex = 0;
        }
        char temp;
        for (int i = startIndex; i <= chars.length / 2; i++) {
            temp = chars[i];
            chars[i] = chars[chars.length - i - 1];
            chars[chars.length - i - 1] = temp;
        }
        return String.valueOf(chars);
    }

    private static final String MEMBER_AVATAR_URL_DEFAULT
            = "https://pic.yugyg.com/uploadFiles/appImgs/header/defaultUImg.png";

    public static String getMemberAvatarUrlDefault() {
        return MEMBER_AVATAR_URL_DEFAULT;
    }
}
