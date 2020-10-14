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
        CREATED("created", "创建"),
        CANCELED("canceled", "取消"),
        DELETED("deleted", "删除"),
        FILL_DELIVERY("fill-delivery", "转运填充物流"),
        RECEIPTED("receipted", "确认收货"),
        PROPERTIES_MODIFIED("properties-modified", "属性修改"),
        STATUS_MODIFIED("status-modified", "状态修改"),
        CHECK_IN("checkIn", "商品登记"),
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
