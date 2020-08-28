package com.hummer.config.subscription;

import com.alibaba.fastjson.JSONObject;
import com.hummer.common.exceptions.AppException;
import com.hummer.config.bo.ConfigListenerKey;
import com.hummer.config.enums.ConfigEnums;
import com.hummer.config.listener.ConfigListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConfigSubscriptionManagerImpl
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/28 15:00
 */
public class ConfigSubscriptionManagerImpl implements ConfigSubscriptionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigSubscriptionManagerImpl.class);

    final Map<ConfigListenerKey, List<ConfigListener>> listeners = new ConcurrentHashMap<>();

    @Override
    public int addListener(ConfigListenerKey key, ConfigListener listener) {

        assertConfigListener(key, listener);

        listeners.getOrDefault(key, new LinkedList<>()).add(listener);
        return 0;
    }

    @Override
    public void removeListener(ConfigListenerKey key) {
        assertConfigListenerKey(key);
        listeners.remove(key);
    }

    @Override
    public void setListener(ConfigListenerKey key, ConfigListener listener) {
        assertConfigListener(key, listener);

        List<ConfigListener> list = new LinkedList<>();
        list.add(listener);
        listeners.put(key, list);
    }

    @Override
    public void doDispatch(ConfigListenerKey key, Map<String, String> changedConfigInfo) {
        assertConfigListenerKey(key);
        // TODO 暂支持整个配置变化触发,后续添加细颗粒度处理


    }

    @Override
    public void doDispatch(String dataId, String groupId, ConfigEnums.ConfigOptions option
            , Map<String, String> configInfo) {
        ConfigListenerKey key = ConfigListenerKey.builder()
                .dataId(dataId)
                .propertiesKey(null)
                .option(option).build();

        List<ConfigListener> list = listeners.get(key);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (ConfigListener listener : list) {
            try {
                listener.handleChange(configInfo);
            } catch (Exception e) {
                LOGGER.error("config change subscription listener do fail ,key=={}", JSONObject.toJSONString(key), e);
            }
        }
    }


    private void assertConfigListener(ConfigListenerKey key, ConfigListener listener) {
        assertConfigListenerKey(key);
        assertConfigListener(listener);
    }

    private void assertConfigListenerKey(ConfigListenerKey key) {
        if (key == null) {
            throw new AppException(40004, "key is null");
        }
    }

    private void assertConfigListener(ConfigListener listener) {
        if (listener == null) {
            throw new AppException(40004, "listener is null");
        }
    }
}
