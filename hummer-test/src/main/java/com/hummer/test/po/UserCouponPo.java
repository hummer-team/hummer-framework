package com.hummer.test.po;

import lombok.Data;

import java.util.Date;

@Data
public class UserCouponPo {
    private Integer id;
    private String userId;
    private String couponCode;
    private Double usedMoney;
    private Date usedTime;
    private Date beginTime;
    private Date endTime;
    private Boolean isEnable;
    private Boolean isUsed;
    private Boolean deleteFlag;
    private String userCouponCode;
    private Date dAddtime;
}
