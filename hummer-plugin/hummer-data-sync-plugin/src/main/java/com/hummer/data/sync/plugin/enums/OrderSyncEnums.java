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
    }

    public enum OperatorType {
        USER(1, "会员"), CUSTOMER_SERVICE(2, "客服"), PAN_LI(3, "Panli")
        , PURCHASER(4, "采购"),QUALITY_INSPECTION(5, "质检")
        , WAREHOUSE_MANAGEMENT(6, "仓管"),;

        private Integer value;
        private String remark;

        OperatorType(Integer value, String remark) {
            this.value = value;
            this.remark = remark;
        }
    }




}
