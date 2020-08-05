package com.hummer.doorgod.service.domain.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.base.Strings;
import com.hummer.core.PropertiesContainer;
import com.hummer.doorgod.service.domain.route.DynamicRouteRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * @author edz
 */
@Component
@Slf4j
public class ListenerRouteEvent implements CommandLineRunner, DisposableBean {
    private final ConcurrentHashMap<String, ListenerEventInfo> listenerMap = new ConcurrentHashMap<>();

    @Autowired
    private DynamicRouteRepository routeRepository;

    @Override
    public void run(String... args) throws Exception {
        listener();
    }

    private void listener() throws NacosException {
        Properties properties = getConfigServerProperties();
        ConfigService configService = NacosFactory.createConfigService(properties);

        String groupId = PropertiesContainer.valueOfString("config.center.gateway.group.id"
                , "DEFAULT_GROUP");
        long timeoutMillis = PropertiesContainer.valueOf("config.center.gateway.timeout.millis"
                , Long.class, 5000L);

        String content = getGatewayIdGroupString(configService, groupId, timeoutMillis);
        List<String> gatewayGroup = JSON.parseArray(content, String.class);
        if (CollectionUtils.isEmpty(gatewayGroup)) {
            log.warn("no gateway config.");
            return;
        }

        addListener(gatewayGroup
                , groupId
                , timeoutMillis
                , configService);
    }

    private String getGatewayIdGroupString(ConfigService configService, String groupId, long timeoutMillis)
            throws NacosException {
        return configService.getConfigAndSignListener(
                PropertiesContainer.valueOfString("config.center.gateway.dataids"
                        , "gateway-configid-group")
                , groupId
                , timeoutMillis
                , new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @SneakyThrows
                    @Override
                    public void receiveConfigInfo(String s) {
                        log.debug(">> receive config value {}", s);
                        updateListener(s, groupId, timeoutMillis, configService);
                    }
                });
    }

    private void updateListener(String configVal, String groupId, long timeoutMillis, ConfigService configService)
            throws NacosException {
        if (Strings.isNullOrEmpty(configVal)) {
            return;
        }

        List<String> gatewayGroups = JSON.parseArray(configVal, String.class);
        if (CollectionUtils.isEmpty(gatewayGroups)) {
            log.warn("no gateway config.");
            return;
        }

        //add listener if not settings
        for (String dataId : gatewayGroups) {
            if (!listenerMap.containsKey(dataId)) {
                addListener(Collections.singletonList(dataId), groupId, timeoutMillis, configService);
                log.debug("add new listener,data id {} group id {}", dataId, groupId);
            }
        }

        //remove listener
        for (Map.Entry<String, ListenerEventInfo> entry : listenerMap.entrySet()) {
            if (!gatewayGroups.contains(entry.getKey())) {
                configService.removeListener(entry.getKey()
                        , entry.getValue().getGroupId()
                        , entry.getValue().getListener());
                log.debug("remove nacos listener ok,data id {} group id {}"
                        , entry.getKey()
                        , entry.getValue().getGroupId());
            }
        }
    }

    private void addListener(List<String> gatewayGroup, String groupId, long timeoutMillis, ConfigService configService)
            throws NacosException {
        for (String dataId : gatewayGroup) {
            Listener listener = new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    if (Strings.isNullOrEmpty(configInfo)) {
                        return;
                    }

                    setRoute(configInfo);
                    log.debug(">>>>>>receive config data id {} info: \n {}"
                            , dataId, configInfo);
                }
            };

            String configVal = configService.getConfigAndSignListener(dataId, groupId, timeoutMillis, listener);
            if (!Strings.isNullOrEmpty(configVal)) {
                setRoute(configVal);
                listenerMap.put(dataId
                        , ListenerEventInfo
                                .builder()
                                .dataId(dataId)
                                .groupId(groupId)
                                .listener(listener)
                                .build());
            }
        }
    }

    private void setRoute(String configInfo) {
        List<RouteDefinition> gatewayConfig = JSON.parseObject(configInfo, new TypeReference<List<RouteDefinition>>() {
        });
        for (RouteDefinition route : gatewayConfig) {
            routeRepository.update(route);
        }
    }

    private Properties getConfigServerProperties() {
        Properties properties = new Properties();
        properties.put("namespace"
                , PropertiesContainer.valueOfString("config.center.gateway.namespace", "gateway"));
        properties.put("serverAddr"
                , PropertiesContainer.valueOfStringWithAssertNotNull("config.center.server-addr"));
        return properties;
    }

    @Override
    public void destroy() throws Exception {
        listenerMap.clear();
        log.debug("clea all listener");
    }
}
