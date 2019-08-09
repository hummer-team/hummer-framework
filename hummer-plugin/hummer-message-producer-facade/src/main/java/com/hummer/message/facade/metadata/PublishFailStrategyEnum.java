package com.hummer.message.facade.metadata;

import com.google.common.base.Strings;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/6 17:34
 **/
public enum PublishFailStrategyEnum {
    /**
     * retry
     **/
    RETRY(0, "retry"),
    /**
     * discard
     **/
    DISCARD(1, "discard"),
    /**
     * none
     **/
    NONE(2, "none");

    private int code;
    private String describe;

    PublishFailStrategyEnum(int code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public int getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }

    /**
     * parse strategy enum by name
     *
     * @param name name
     * @return {@link com.hummer.message.facade.metadata.PublishFailStrategyEnum}
     * @author liguo
     * @date 2019/8/6 17:44
     * @since 1.0.0
     **/
    public static PublishFailStrategyEnum parseByName(final String name) {
        if (Strings.isNullOrEmpty(name)) {
            return NONE;
        }
        for (PublishFailStrategyEnum strategyEnum : PublishFailStrategyEnum.values()) {
            if (strategyEnum.describe.equalsIgnoreCase(name)) {
                return strategyEnum;
            }
        }

        return NONE;
    }

    @Override
    public String toString() {
        return "PublishFailStrategyEnum{" +
                "code=" + code +
                ", describe='" + describe + '\'' +
                '}';
    }}
