package com.hummer.data.sync.plugin.enums;

/**
 * OrderSyncEnums
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/13 19:07
 */
public class OrderSyncEnums {

    public enum BusinessType {
        SHOPPING_ORDER("shopping-order", "代购订单"),
        SELF_ORDER("transport-order", "自助购订单"),
        SHIP_ORDER("ship-order", "运单"),
        ;
        private String value;
        private String remark;

        BusinessType(String value, String remark) {
            this.value = value;
            this.remark = remark;
        }

        public String getValue() {
            return value;
        }

        public String getRemark() {
            return remark;
        }
    }


    public enum ActionType {
        MEMBER_CREATED("member-created", "创建"),
        MEMBER_CANCELED("member-canceled", "取消"),
        MEMBER_DELETED("member-deleted", "删除"),
        MEMBER_FILL_DELIVERY("member-fill-delivery", "转运填充物流"),
        MEMBER_RECEIPTED("member-receipted", "确认收货"),
        MANAGER_PROPERTIES_MODIFIED("manager-properties-modified", "属性修改"),
        MANAGER_STATUS_MODIFIED("manager-status-modified", "状态修改"),
        MANAGER_CHECK_IN("manager-checkIn", "商品登记"),
        ;
        private String value;
        private String remark;

        ActionType(String value, String remark) {
            this.value = value;
            this.remark = remark;
        }

        public String getValue() {
            return value;
        }

        public String getRemark() {
            return remark;
        }
    }

    public enum OperatorType {
        USER(1, "会员"), CUSTOMER_SERVICE(2, "客服"), PAN_LI(3, "Panli"), PURCHASER(4, "采购"), QUALITY_INSPECTION(5, "质检"), WAREHOUSE_MANAGEMENT(6, "仓管"),
        ;

        private Integer value;
        private String remark;

        OperatorType(Integer value, String remark) {
            this.value = value;
            this.remark = remark;
        }

        public Integer getValue() {
            return value;
        }

        public String getRemark() {
            return remark;
        }
    }

}
