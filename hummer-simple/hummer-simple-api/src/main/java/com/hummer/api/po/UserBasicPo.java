package com.hummer.api.po;

import lombok.Data;

@Data
public class UserBasicPo extends BasePo {
    private Integer id;
    private String userId;
    private String nickName;
    private String trueName;
    private String mobilePhone;
    private String loginPassword;
    private String mail;
    private Boolean isLocked;
}
