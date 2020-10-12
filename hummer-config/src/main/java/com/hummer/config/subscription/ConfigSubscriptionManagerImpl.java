package com.hummer.config.subscription;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hummer.common.exceptions.AppException;
import com.hummer.config.bo.ConfigDataInfoBo;
import com.hummer.config.bo.ConfigListenerKey;
import com.hummer.config.bo.ConfigPropertiesChangeInfoBo;
import com.hummer.config.enums.ConfigEnums;
import com.hummer.config.listener.AbstractConfigListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    private final static Map<ConfigListenerKey, List<AbstractConfigListener>> SUBSCRIPTIONS = new ConcurrentHashMap<>();

    @Override
    public int addListener(ConfigListenerKey key, AbstractConfigListener listener) {

        assertConfigListener(key, listener);
        List<AbstractConfigListener> list = SUBSCRIPTIONS.getOrDefault(key, new LinkedList<>());
        // 避免重复添加
        if (confirmListenerRepeat(listener, list)) {
            return 0;
        }
        list.add(listener);
        SUBSCRIPTIONS.putIfAbsent(key, list);
        return 1;
    }

    private boolean confirmListenerRepeat(AbstractConfigListener listener, List<AbstractConfigListener> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        return list.stream().anyMatch(item ->
                item.equals(listener));
    }

    @Override
    public void removeListener(ConfigListenerKey key) {
        assertConfigListenerKey(key);
        SUBSCRIPTIONS.remove(key);
    }

    @Override
    public void removeListener(ConfigListenerKey key, AbstractConfigListener listener) {
        assertConfigListener(listener);
        List<AbstractConfigListener> list = SUBSCRIPTIONS.get(key);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (listener.equals(list.get(i))) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            list.remove(index);
        }
    }

    @Override
    public void doDispatch(ConfigDataInfoBo dataInfoBo, final List<ConfigPropertiesChangeInfoBo> changeInfoBos) {
        if (dataInfoBo == null || CollectionUtils.isEmpty(changeInfoBos) || CollectionUtils.isEmpty(SUBSCRIPTIONS)) {
            return;
        }
        // 判断dataId全配置订阅
        // 判断需要执行的listener
        Map<ConfigListenerKey, List<ConfigPropertiesChangeInfoBo>> map = new HashMap<>(16);

        if (ConfigEnums.ConfigType.JSON.getValue().equalsIgnoreCase(dataInfoBo.getDataType())) {
            for (ConfigPropertiesChangeInfoBo changeInfoBo : changeInfoBos) {
                for (Map.Entry<ConfigListenerKey, List<AbstractConfigListener>> entry : SUBSCRIPTIONS.entrySet()) {
                    if (matchConfigListenerKey(dataInfoBo, entry.getKey())) {
                        map.put(entry.getKey(),
                                Collections.singletonList(ConfigPropertiesChangeInfoBo.builder()
                                        .originValue(JSONObject.toJSONString(dataInfoBo.getOriginValue()))
                                        .currentValue(JSONObject.toJSONString(dataInfoBo.getCurrentValue()))
                                        .action(dataInfoBo.getAction()).build())
                        );
                        continue;
                    }
                    if (matchConfigPropertiesListenerKey(dataInfoBo, changeInfoBo.getPropertiesKey(), entry.getKey())) {

                        map.put(entry.getKey(), composeConfigPropertiesChanges(map.get(entry.getKey()), changeInfoBo));
                    }
                }
            }
        } else {

            for (Map.Entry<ConfigListenerKey, List<AbstractConfigListener>> entry : SUBSCRIPTIONS.entrySet()) {
                if (matchConfigListenerKey(dataInfoBo, entry.getKey())) {
                    map.put(entry.getKey(), changeInfoBos);
                    continue;
                }
                for (ConfigPropertiesChangeInfoBo changeInfoBo : changeInfoBos) {

                    if (matchConfigPropertiesListenerKey(dataInfoBo, changeInfoBo.getPropertiesKey(), entry.getKey())) {

                        map.put(entry.getKey(), composeConfigPropertiesChanges(map.get(entry.getKey()), changeInfoBo));
                    }
                }
            }
        }


        if (map.isEmpty()) {
            return;
        }
        disPatch(map);
    }

    private void disPatch(Map<ConfigListenerKey, List<ConfigPropertiesChangeInfoBo>> changedSubscriptions) {
        for (Map.Entry<ConfigListenerKey, List<ConfigPropertiesChangeInfoBo>> entry : changedSubscriptions.entrySet()) {
            List<AbstractConfigListener> listeners = SUBSCRIPTIONS.get(entry.getKey());
            if (CollectionUtils.isEmpty(listeners)) {
                continue;
            }

            for (AbstractConfigListener listener : listeners) {
                try {
                    listener.handleChange(entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    LOGGER.error("config change subscription listener do fail ,key=={},changes=={}"
                            , entry.getKey(), JSON.toJSONString(entry.getValue()), e);
                }
            }
        }
    }

    private List<ConfigPropertiesChangeInfoBo> composeConfigPropertiesChanges(List<ConfigPropertiesChangeInfoBo> origin
            , ConfigPropertiesChangeInfoBo changeInfoBo) {
        if (CollectionUtils.isEmpty(origin)) {
            origin = new ArrayList<>();
        }
        if (origin.contains(changeInfoBo)) {
            return origin;
        }
        origin.add(changeInfoBo);
        return origin;
    }

    private boolean matchConfigPropertiesListenerKey(ConfigDataInfoBo dataInfoBo, String propertiesKey
            , ConfigListenerKey matchedKey) {

        return matchedKey.getDataId().equals(dataInfoBo.getDataId())
                && matchedKey.getGroupId().equals(dataInfoBo.getGroupId())
                && !CollectionUtils.isEmpty(matchedKey.getPropertiesKey())
                && matchedKey.getPropertiesKey().contains(propertiesKey);
    }

    private boolean matchConfigListenerKey(ConfigDataInfoBo dataInfoBo, ConfigListenerKey matchedKey) {
        return matchedKey.getDataId().equals(dataInfoBo.getDataId())
                && matchedKey.getGroupId().equals(dataInfoBo.getGroupId())
                && CollectionUtils.isEmpty(matchedKey.getPropertiesKey());
    }


    private void assertConfigListener(ConfigListenerKey key, AbstractConfigListener listener) {
        assertConfigListenerKey(key);
        assertConfigListener(listener);
    }

    private void assertConfigListenerKey(ConfigListenerKey key) {
        if (key == null) {
            throw new AppException(40004, "key is null");
        }
        if (StringUtils.isEmpty(key.getDataId())) {
            throw new AppException(40004, "key.dataId is null");
        }
        if (StringUtils.isEmpty(key.getGroupId())) {
            throw new AppException(40004, "key.groupId is null");
        }
    }

    private void assertConfigListener(AbstractConfigListener listener) {
        if (listener == null) {
            throw new AppException(40004, "listener is null");
        }
    }
}
