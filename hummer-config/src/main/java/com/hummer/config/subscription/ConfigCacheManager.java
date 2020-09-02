package com.hummer.config.subscription;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.hummer.common.exceptions.AppException;
import com.hummer.config.bo.ConfigDataInfoBo;
import com.hummer.config.bo.ConfigListenerKey;
import com.hummer.config.bo.ConfigPropertiesChangeInfoBo;
import com.hummer.config.enums.ConfigEnums;
import com.hummer.core.PropertiesContainer;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * ConfigCacheManager
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/31 13:36
 */
public class ConfigCacheManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigCacheManager.class);

    final static Map<ConfigListenerKey, Map<String, Object>> configCache = new ConcurrentHashMap<>();

    final Map<String, Consumer<Config>> fillMap = new ConcurrentHashMap<>();

    {
        fillMap.put("properties", this::fillByProperties);
        fillMap.put("json", this::fillByJson);
    }

    @Data
    @Builder
    private static class Config {
        private String groupId;
        private String dataId;
        private String value;
    }

    public ConfigDataInfoBo putConfigToContainer(String groupId
            , String dataId
            , String value
            , String dataType
    ) {
        ConfigListenerKey listenerKey = ConfigListenerKey.builder().dataId(dataId).groupId(groupId).build();
        // 获取原配置
        Map<String, Object> originMap = configCache.get(listenerKey);
        // 更新配置全局管理缓存
        Map<String, Object> configMap = fillConfigInfo(dataId, groupId, value, dataType);
        // 更新dataId对应配置缓存
        configCache.put(listenerKey, configMap);
        return ConfigDataInfoBo.builder()
                .dataId(dataId)
                .groupId(groupId)
                .currentValue(configMap)
                .originValue(originMap).build();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fillByJson(Config config) {
        Map<String, Object> map = null;
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        try {
            Object o = JSON.parseObject(config.value);
            PropertiesContainer.put(config.getDataId(), o);

            map = JSONObject.parseObject(JSONObject.toJSONString(o), Map.class);
        } catch (Exception e) {
            map = JSON.parseObject(config.value, Map.class);
            PropertiesContainer.put(config.getDataId(), map);
        } finally {
            ParserConfig.getGlobalInstance().setAutoTypeSupport(false);
        }
        return map;
    }

    private Map<String, Object> fillByProperties(Config config) {
        Map<String, Object> configMap = new HashMap<>();
        try {
            Properties properties1 = new Properties();
            properties1.load(new StringReader(config.getValue()));
            for (Map.Entry<Object, Object> map : properties1.entrySet()) {
                configMap.put(map.getKey().toString(), map.getValue());
                PropertiesContainer.put(map.getKey().toString(), map.getValue());
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("nacos config data id {},group id {},config {} put to PropertiesContainer"
                            , config.getDataId(), config.getGroupId(), map);
                }
            }
            return configMap;
        } catch (IOException e) {
            LOGGER.error("read config failed data id:{},group:{},value:\n{}"
                    , config.getDataId()
                    , config.getGroupId()
                    , config.getValue());
        }
        return configMap;
    }


    private Map<String, Object> fillConfigInfo(String dataId, String groupId, String configInfo, String dataType) {

        Config config = Config.builder().groupId(groupId).dataId(dataId).value(configInfo).build();
        if ("json".equals(dataType)) {
            return fillByJson(config);
        } else if ("properties".equals(dataType)) {
            return fillByProperties(config);
        }
        throw new AppException(40004, String.format("%s no supported", dataType));
    }

    public List<ConfigPropertiesChangeInfoBo> parsingConfigPropertiesChanges(ConfigDataInfoBo dataInfoBo) {
        if (MapUtils.isEmpty(dataInfoBo.getCurrentValue()) && MapUtils.isEmpty(dataInfoBo.getOriginValue())) {
            return Collections.emptyList();
        }
        List<ConfigPropertiesChangeInfoBo> list = new ArrayList<>();
        ConfigEnums.ConfigActions action;
        if (MapUtils.isEmpty(dataInfoBo.getCurrentValue())) {
            action = ConfigEnums.ConfigActions.DELETE;
            for (Map.Entry<String, Object> entry : dataInfoBo.getOriginValue().entrySet()) {
                list.add(ConfigPropertiesChangeInfoBo.builder()
                        .action(action)
                        .currentValue(null)
                        .originValue(entry.getValue())
                        .propertiesKey(entry.getKey()).build());
            }
        }
        if (MapUtils.isEmpty(dataInfoBo.getOriginValue())) {
            action = ConfigEnums.ConfigActions.ADD;
            for (Map.Entry<String, Object> entry : dataInfoBo.getCurrentValue().entrySet()) {
                list.add(ConfigPropertiesChangeInfoBo.builder()
                        .action(action)
                        .currentValue(null)
                        .originValue(entry.getValue())
                        .propertiesKey(entry.getKey()).build());
            }
        }
        for (Map.Entry<String, Object> entry : dataInfoBo.getOriginValue().entrySet()) {
            Object currentValue = dataInfoBo.getCurrentValue().get(entry.getKey());
            if (entry.getValue() == null && currentValue == null) {
                continue;
            }

            if (entry.getValue() == null) {
                action = ConfigEnums.ConfigActions.ADD;
            } else if (currentValue == null) {
                action = ConfigEnums.ConfigActions.DELETE;
            } else if (entry.getValue().equals(currentValue)) {
                continue;
            } else {
                action = ConfigEnums.ConfigActions.UPDATE;
            }
            list.add(ConfigPropertiesChangeInfoBo.builder()
                    .action(action)
                    .currentValue(currentValue)
                    .originValue(entry.getValue())
                    .propertiesKey(entry.getKey()).build());

        }
        return list;
    }
}
