package com.hummer.user.plugin.user.member;

import lombok.Data;

import java.math.BigDecimal;

/**
 * MemberUserContext
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/9/2 14:19
 */
@Data
public class MemberUserContext {

    private String userId;

    private String userName;

    public String email;

    public String realName;

    public String avatarUrl;

    public Integer gender;

    public String country;

    public String province;

    public String city;

    public int inviteCode;

    /**
     * 余额
     */
    public BigDecimal balance;

    /**
     * 积分
     */
    public BigDecimal Score;
}
