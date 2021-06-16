package com.hummer.test.model;

import com.hummer.config.bo.ConfigListenerKey;
import com.hummer.config.bo.ConfigPropertiesChangeInfoBo;
import com.hummer.config.listener.AbstractConfigListener;

import java.util.List;

/**
 * CustomListener
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/31 18:41
 */
public class CustomListener extends AbstractConfigListener {


    @Override
    public void handleChange(ConfigListenerKey key, List<ConfigPropertiesChangeInfoBo> changeInfoBos) {

    }
}
