package com.hummer.kafka.consumer.plugin.consumer;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/26 17:45
 **/
public enum OffsetSeekEnum {
    BEGIN(0),
    END(1),
    SPECIFIC_POINT(2)
    ;
    private int code;

    private OffsetSeekEnum(final int code) {
        this.code = code;
    }

    public static OffsetSeekEnum getByCode(final int code){
        for(OffsetSeekEnum offsetSeekEnum : OffsetSeekEnum.values()){
            if(offsetSeekEnum.code==code){
                return offsetSeekEnum;
            }
        }

        return BEGIN;
    }
}
