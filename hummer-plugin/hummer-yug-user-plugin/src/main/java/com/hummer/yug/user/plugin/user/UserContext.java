package com.hummer.yug.user.plugin.user;

import lombok.Data;

/**
 * @author chenwei
 */
@Data
public class UserContext {
    //用户ID
    private Long ygfUserId;

    //用户等级id
    private Long ygfUserLevelId;

    //用户分组表id
    private Long ygfUserGroupId;

    //用户名称
    private String userName;

    //用户昵称
    private String nickName;

    //用户头像url
    private String picUrl;

    //用户手机号
    private String phoneNo;

    //用户邮箱
    private String email;

    //性别 1-男 2-女 3-保密
    private Integer sex;

    //用户状态
    private Integer status;

    //是否黑名单
    private Integer isBlack;

    //黑名单更改备注信息
    private String blackDes;

    //系统默认分组   1、PC ，2、安卓 ，3、IOS，4、H5，5、微信
    private Integer defaultGroup;
}
