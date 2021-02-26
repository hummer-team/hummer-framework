package com.hummer.yug.tools.plugin.util;

import com.hummer.common.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;

/**
 * GeneratorUtil
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/2/26 14:31
 */
public class GeneratorUtil {

    public static class GeneratorCodePre {
        // 订单类型:秒杀MS 限时购 XS 拼团 PT 砍价 KJ 发货单号SD 退款TK 退货 TH 换货 HH
        /**
         * 拼团订单前缀
         */
        public static final String YGF_ORDER_CODE_PT = "PT";
        /**
         * 秒杀订单前缀
         */
        public static final String YGF_ORDER_CODE_MS = "MS";
        /**
         * 限时购订单前缀
         */
        public static final String YGF_ORDER_CODE_XS = "XS";
        /**
         * 发货单号前缀
         */
        public static final String YGF_ORDER_CODE_SD = "SD";
        /**
         * 退款单前缀
         */
        public static final String YGF_ORDER_CODE_TK = "TK";
        /**
         * 退货单前缀
         */
        public static final String YGF_ORDER_CODE_TH = "TH";
        /**
         * 换货单前缀
         */
        public static final String YGF_ORDER_CODE_HH = "HH";
        /**
         * 普通砍价单前缀
         */
        public static final String YGF_ORDER_CODE_KJ = "KJ";
        /**
         * 红包砍价单前缀
         */
        public static final String YGF_ORDER_CODE_KH = "KH";
        /**
         * 红包砍价单前缀
         */
        public static final String YGF_ORDER_CODE_PREFIX_SHOP = "MD";

        public static final String YGF_TH_CODE_PREFIX_SHOP = "MDTH";
    }

    /**
     * Description: 编号生成规则
     *
     * @param code 普通订单:null 秒杀MS 限时购 XS 拼团 PT 砍价 KJ 发货单号SD 退款TK 退货 TH 换货 HH
     * @return author GZ date 2018-2-2
     */
    public static String generateCode(String code) {
        String result = DateUtil.formatNowDate("yyyyMMddHHmmss" + getRandomStringByLength(4, "0"));
        return StringUtils.isEmpty(code) ? result : code + result;
    }

    /**
     * @Description: @param length @return @author chenwei @date 2017-12-24 @throws
     */
    public static String getRandomStringByLength(int length, String type) {
        String str1 = "abcdefghijklmnopqrstuvwxyz";
        String str2 = "0123456789";
        String base = str1 + str2;
        // 全小写字母
        if ("az".equals(type)) {
            base = str1;

        } else if ("AZ".equals(type)) {
            // 全大写字母
            base = str1.toUpperCase();
        } else if ("0".equals(type)) {
            // 全数字
            base = str2;
        } else if ("aA".equals(type)) {
            // 大小写字母
            base = str1 + str2;
        } else if ("total".equals(type)) {
            // 大小写字母
            base = str1 + str1.toUpperCase() + str2;
        }

        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
