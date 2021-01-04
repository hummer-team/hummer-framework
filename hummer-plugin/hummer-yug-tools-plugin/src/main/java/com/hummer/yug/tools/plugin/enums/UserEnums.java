package com.hummer.yug.tools.plugin.enums;

/**
 * UserEnums
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/12/31 11:25
 */
public class UserEnums {

    public enum UserType {
        MEMBER("member", "会员"), SHOP_MANAGER("shopManager", "店铺管理员")
        , YUG_MANAGER("manager", "管理后台管理员"), YUG_OPERATOR("operator", "运营后台管理员");
        private String key;
        private String remark;

        UserType(String key, String remark) {
            this.key = key;
            this.remark = remark;
        }

        public String getKey() {
            return key;
        }

        public String getRemark() {
            return remark;
        }
    }
}
