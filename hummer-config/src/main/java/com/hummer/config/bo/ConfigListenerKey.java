package com.hummer.config.bo;

import com.hummer.config.enums.ConfigEnums;
import lombok.Builder;
import lombok.Data;

/**
 * ConfigListenerKey
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/28 14:39
 */
@Builder
@Data
public class ConfigListenerKey {

    private String dataId;

    private String groupId;

    private String propertiesKey;

    private ConfigEnums.ConfigActions option;
}
