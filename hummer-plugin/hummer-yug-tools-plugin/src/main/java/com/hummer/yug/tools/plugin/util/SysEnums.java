package com.hummer.yug.tools.plugin.util;

import org.apache.commons.lang3.StringUtils;

/**
 * SysEnums
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/12/29 11:35
 */
public class SysEnums {

    /**
     * 客户端来源枚举
     *
     * @author chen wei
     * @date 2020/8/7
     */
    public enum ClientResourceEnum {
        H5("h5", "微信端"),
        ANDROID("android", "Android端"),
        IOS("ios", "ios端"),
        ;
        private String value;
        private String remark;

        ClientResourceEnum(String value, String remark) {
            this.value = value;
            this.remark = remark;
        }

        public String getValue() {
            return value;
        }

        public String getRemark() {
            return remark;
        }

        public static ClientResourceEnum getEnumByValue(String value) {
            if (StringUtils.isBlank(value)) {
                return H5;
            }
            for (ClientResourceEnum item : ClientResourceEnum.values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
            return H5;
        }

    }
}
