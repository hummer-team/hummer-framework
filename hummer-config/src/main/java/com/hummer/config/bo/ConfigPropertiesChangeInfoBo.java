package com.hummer.config.bo;

import com.hummer.config.enums.ConfigEnums;
import lombok.Builder;
import lombok.Data;

/**
 * ConfigPropertiesChangeInfoBo
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/31 14:04
 */
@Data
@Builder
public class ConfigPropertiesChangeInfoBo {

    /**
     * 不存在则表示整个配置变更
     */
    private String propertiesKey;

    /**
     * propertiesKey 存在则是属性对应值，不存在则表示配置内容，json字符串格式
     */
    private Object originValue;

    /**
     * propertiesKey 存在则是属性对应值，不存在则表示配置内容，json字符串格式
     */
    private Object currentValue;

    private ConfigEnums.ConfigActions action;

    @Override
    public String toString() {
        return String.format("ConfigPropertiesChangeInfoBo=[propertiesKey:%s,originValue:%s" +
                ",currentValue:%s,action:%s]",propertiesKey,originValue,currentValue,action);
    }
}
