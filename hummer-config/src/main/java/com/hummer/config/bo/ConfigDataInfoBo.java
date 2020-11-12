package com.hummer.config.bo;

import com.hummer.config.enums.ConfigEnums;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * ConfigDataInfoBo
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/31 14:25
 */
@Data
@Builder
public class ConfigDataInfoBo {

    private String dataId;

    private String groupId;

    private Map<String, Object> originValue;

    private Map<String, Object> currentValue;

    private ConfigEnums.ConfigActions action;

    private String dataType;

    private ConfigEnums.ConfigChangeScene scene;
}
