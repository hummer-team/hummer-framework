package com.hummer.config.subscription;

import com.hummer.config.bo.ConfigListenerKey;
import com.hummer.config.enums.ConfigEnums;
import com.hummer.config.listener.ConfigListener;

import java.util.Map;

/**
 * ConfigSubscriptionManagerImpl
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/28 14:52
 */
public interface ConfigSubscriptionManager {


    int addListener(ConfigListenerKey key, ConfigListener listener);

    void removeListener(ConfigListenerKey key);

    void setListener(ConfigListenerKey key, ConfigListener listener);

    void doDispatch(ConfigListenerKey key, Map<String, String> changedPropertiesInfo);

    void doDispatch(String dataId, String groupId, ConfigEnums.ConfigActions option, Map<String, String> configInfo);
}
