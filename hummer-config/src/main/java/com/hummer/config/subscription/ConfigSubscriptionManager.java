package com.hummer.config.subscription;

import com.hummer.config.bo.ConfigDataInfoBo;
import com.hummer.config.bo.ConfigListenerKey;
import com.hummer.config.bo.ConfigPropertiesChangeInfoBo;
import com.hummer.config.listener.AbstractConfigListener;

import java.util.List;

/**
 * ConfigSubscriptionManagerImpl
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/28 14:52
 */
public interface ConfigSubscriptionManager {


    int addListener(ConfigListenerKey key, AbstractConfigListener listener);

    void removeListener(ConfigListenerKey key);

    void removeListener(ConfigListenerKey key, AbstractConfigListener listener);

    void doDispatch(ConfigDataInfoBo dataInfoBo, final List<ConfigPropertiesChangeInfoBo> changeInfoBos);
}
