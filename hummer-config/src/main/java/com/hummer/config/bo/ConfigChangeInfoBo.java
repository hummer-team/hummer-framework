package com.hummer.config.bo;

import lombok.Data;

import java.util.List;

/**
 * ConfigChangeBo
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/31 13:17
 */
@Data
public class ConfigChangeInfoBo {

    private ConfigListenerKey key;

    private List<ConfigPropertiesChangeInfoBo> propertiesChangeInfoBos;
}
