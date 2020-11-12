package com.hummer.config.subscription;

import com.hummer.config.bo.ConfigDataInfoBo;
import com.hummer.config.bo.ConfigPropertiesChangeInfoBo;

import java.util.List;

/**
 * ConfigLoadSubscriptionManagerImpl
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/12 13:35
 */
public class ConfigLoadSubscriptionManagerImpl extends ConfigSubscriptionManagerImpl {

    @Override
    public void doDispatch(ConfigDataInfoBo dataInfoBo, List<ConfigPropertiesChangeInfoBo> changeInfoBos) {

        super.doDispatch(dataInfoBo, changeInfoBos);
    }
}
