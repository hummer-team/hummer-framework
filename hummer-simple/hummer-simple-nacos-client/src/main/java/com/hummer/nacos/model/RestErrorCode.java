package com.hummer.nacos.model;

import com.hummer.common.ErrorCode;

/**
 * RestErrorCode
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/3/26 11:27
 */
public enum RestErrorCode implements ErrorCode {

    // 访问成功
    SUCCESS(200, 0, "请求成功"),
    // 请求参数错误模块
    PARAM_ERROR(400, 0, "参数错误"),

    PARAM_ERROR_SHOP_SHARE_CODE(400, 1, "未查询到店铺分享码信息"),

    PARAM_ERROR_SHOP_NOT_EXIST(400, 2, "店铺不存在"),

    PARAM_ERROR_MEMBER_NOT_EXIST(400, 101, "未查询到用户");

    private int code;
    private int subCode;
    private String msg;

    RestErrorCode(int code, int subCode, String msg) {
        this.code = code;
        this.subCode = subCode;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public int getSubCode() {
        return this.subCode;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
