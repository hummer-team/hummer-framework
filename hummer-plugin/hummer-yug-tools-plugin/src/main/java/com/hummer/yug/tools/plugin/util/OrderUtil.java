package com.hummer.yug.tools.plugin.util;

import com.hummer.common.utils.AppBusinessAssert;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * OrderUtil
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/12/29 11:33
 */
public class OrderUtil {


    public static int parsingOrderStatus(Integer isPay, Integer isCancel, Integer isSend, Integer isReceive
            , Boolean pendingPickup, Boolean verificationOn) {
        if (isCancel != null && isCancel != 0) {
            return 6;
        }
        if (isPay == null || isPay < 1) {
            return 1;
        }
        if (isPay == 1 && (isSend == 0 || isSend == 2)) {
            return 2;
        }
        if ((isSend == 1 || isSend == 3) && (isReceive == null || isReceive == 0)) {
            return 3;
        }
        if (Boolean.TRUE.equals(pendingPickup) && Boolean.FALSE.equals(verificationOn)) {
            return 4;
        }
        if (isReceive != null && isReceive > 0) {
            return 5;
        }
        return 1;
    }

    /**
     * description 正则匹配字符串
     *
     * @author chen wei
     * @date 2019/8/2
     */
    public static class ParamValidPatter {

        public static final String MOBILE_STR = "\\d{11}";

        private static final String MOBILE_LIKE_STR = "\\d+";

        public static final Pattern MOBILE_LIKE = Pattern.compile(MOBILE_LIKE_STR);

        private static final String REGEX_MONEY_DECIMAL_PLACES_STR = "^(([1-9]\\d*)|0)(\\.(\\d){0,2})?$";

        public static final Pattern REGEX_MONEY_DECIMAL_PLACES = Pattern.compile(REGEX_MONEY_DECIMAL_PLACES_STR);

    }

    public static void assertMoneyFormat(Double amount) {
        if (amount == null) {
            return;
        }
        AppBusinessAssert.isTrue(ParamValidPatter.REGEX_MONEY_DECIMAL_PLACES.matcher(amount.toString()).matches(),
                400, "金额格式不符，仅限输入正数，支持小数点后两位");
    }


    private static String getFormByNumber(double number) {

        DecimalFormat format = new DecimalFormat("####.#");
        return format.format(number);
    }
}
