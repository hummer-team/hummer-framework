package com.hummer.config.listener;

import com.hummer.config.bo.ConfigListenerKey;
import com.hummer.config.bo.ConfigPropertiesChangeInfoBo;

import java.util.List;

/**
 * ConfigListener
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/28 14:34
 */
public interface ConfigListener {

    String getId();

    void handleChange(ConfigListenerKey key, List<ConfigPropertiesChangeInfoBo> changeInfoBos);
}
