package com.hummer.doorgod.service.domain.listener;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hummer.core.PropertiesContainer;
import com.hummer.doorgod.service.domain.configuration.DoorGoodConfig;
import com.hummer.doorgod.service.domain.configuration.LoadBalancerConfig;
import com.hummer.doorgod.service.domain.route.InMemoryRouteRepository;
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author edz
 */
@Component
@Slf4j
public class ListenerConfig implements CommandLineRunner, DisposableBean {
    private final ConcurrentHashMap<String, ListenerEvent> listenerMap = new ConcurrentHashMap<>();

    @Autowired
    private InMemoryRouteRepository routeRepository;

    @Override
    public void run(String... args) throws Exception {
        listenerGateway();
    }

    private void listenerGateway() throws NacosException {
        String groupId = PropertiesContainer.valueOfString("config.center.gateway.group.id"
                , "DEFAULT_GROUP");
        long timeoutMillis = PropertiesContainer.valueOf("config.center.gateway.timeout.millis"
                , Long.class, 5000L);

        //load all gateway group config
        ConfigService configService = NacosFactory.createConfigService(getConfigServerProperties());
        String content = getGatewayIdGroupString(configService, groupId, timeoutMillis);
        List<String> gatewayGroup = JSON.parseArray(content, String.class);
        if (CollectionUtils.isEmpty(gatewayGroup)) {
            log.warn("no gateway config.");
            return;
        }
        log.debug("gateway group id is \n{}", gatewayGroup);
        addListenerForFirstLoading(gatewayGroup
                , groupId
                , timeoutMillis
                , configService);
    }

    private String getGatewayIdGroupString(ConfigService configService
            , String groupId
            , long timeoutMillis)
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

                    @Override
                    public void receiveConfigInfo(String s) {
                        try {
                            log.debug(">> receive config value \n{}", s);
                            updateListener(s, groupId, timeoutMillis, configService);
                        } catch (NacosException e) {
                            log.warn("reset gateway config failed", e);
                        }
                    }
                });
    }

    private void updateListener(String configVal, String groupId, long timeoutMillis, ConfigService configService)
            throws NacosException {
        if (Strings.isNullOrEmpty(configVal)) {
            for (Map.Entry<String, ListenerEvent> entry : listenerMap.entrySet()) {
                routeRepository.delete(entry.getKey());
                //remove nacos listener
                configService.removeListener(entry.getKey()
                        , entry.getValue().getGroupId()
                        , entry.getValue().getListener());
            }
            removeAllSentinelConfig();
            //remove all config
            log.warn("need remove all door god config.");
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
                addListenerForReLoading(Collections.singletonList(dataId), groupId, timeoutMillis, configService);
                log.debug("add new listener,data id {} group id {}", dataId, groupId);
            }
        }

        //remove listener
        for (Map.Entry<String, ListenerEvent> entry : listenerMap.entrySet()) {
            if (!gatewayGroups.contains(entry.getKey())) {
                //remove rule
                routeRepository.delete(entry.getKey());
                //remove nacos listener
                configService.removeListener(entry.getKey()
                        , entry.getValue().getGroupId()
                        , entry.getValue().getListener());
                removeSentinelConfig(entry);
                log.debug("remove nacos listener ok,data id {} group id {}"
                        , entry.getKey()
                        , entry.getValue().getGroupId());
            }
        }
    }

    private void removeAllSentinelConfig() {
        DegradeRuleManager.loadRules(Collections.emptyList());
        GatewayRuleManager.loadRules(Collections.emptySet());
        GatewayApiDefinitionManager.loadApiDefinitions(Collections.emptySet());
    }

    private void removeSentinelConfig(Map.Entry<String, ListenerEvent> entry) {
        if (entry.getValue() == null || entry.getValue().getSentinelConfig() == null) {
            return;
        }
        //remove sentinel
        if (CollectionUtils.isNotEmpty(entry.getValue().getSentinelConfig().getDegradeRule2())) {
            List<DegradeRule> rules = DegradeRuleManager.getRules()
                    .stream()
                    .filter(f -> entry
                            .getValue()
                            .getSentinelConfig()
                            .getDegradeRule2()
                            .stream()
                            .noneMatch(p -> p.getResource().equals(f.getResource())))
                    .collect(Collectors.toList());
            DegradeRuleManager.loadRules(rules);
        }

        if (CollectionUtils.isNotEmpty(entry.getValue().getSentinelConfig().getGatewayFlowRules())) {
            Set<GatewayFlowRule> flowRules = GatewayRuleManager.getRules()
                    .stream()
                    .filter(f -> entry
                            .getValue()
                            .getSentinelConfig()
                            .getGatewayFlowRules()
                            .stream()
                            .noneMatch(p -> p.getResource().equals(f.getResource())))
                    .collect(Collectors.toSet());
            GatewayRuleManager.loadRules(flowRules);
        }

        if (CollectionUtils.isNotEmpty(entry.getValue().getSentinelConfig().getApiDefinitions())) {
            Set<ApiDefinition> apiDefinitions = GatewayApiDefinitionManager.getApiDefinitions()
                    .stream()
                    .filter(f -> entry
                            .getValue()
                            .getSentinelConfig()
                            .getApiDefinitions()
                            .stream()
                            .noneMatch(p -> p.getApiName().equals(f.getApiName())))
                    .collect(Collectors.toSet());
            GatewayApiDefinitionManager.loadApiDefinitions(apiDefinitions);
        }
    }

    private void addListenerForReLoading(List<String> gatewayGroup
            , String groupId
            , long timeoutMillis
            , ConfigService configService) throws NacosException {
        addListener(gatewayGroup, groupId, timeoutMillis, configService, false);
    }

    private void addListenerForFirstLoading(List<String> gatewayGroup
            , String groupId
            , long timeoutMillis
            , ConfigService configService) throws NacosException {
        addListener(gatewayGroup, groupId, timeoutMillis, configService, true);
    }

    private void addListener(List<String> gatewayGroup
            , String groupId
            , long timeoutMillis
            , ConfigService configService
            , boolean isFirstLoading)
            throws NacosException {

        Set<DegradeRule> degradeRules = Sets.newConcurrentHashSet();
        Set<GatewayFlowRule> gatewayFlowRules = Sets.newConcurrentHashSet();
        Set<ApiDefinition> apiDefinitions = Sets.newConcurrentHashSet();

        for (String dataId : gatewayGroup) {
            Listener listener = getListener();
            String configVal = configService.getConfigAndSignListener(dataId, groupId, timeoutMillis, listener);
            if (!Strings.isNullOrEmpty(configVal)) {
                //refresh gateway config
                log.debug("refresh config done,config value is \n{}", configVal);
                DoorGoodConfig doorGoodConfig = JSON.parseObject(configVal, new TypeReference<DoorGoodConfig>() {
                });
                refreshGatewayRule(doorGoodConfig);
                listenerMap.put(dataId
                        , ListenerEvent
                                .builder()
                                .dataId(dataId)
                                .groupId(groupId)
                                .listener(listener)
                                .sentinelConfig(doorGoodConfig.getSentinelConfig())
                                .build());
                if (isFirstLoading) {
                    addSentinelConfigForInitLoad(degradeRules, gatewayFlowRules, apiDefinitions, doorGoodConfig);
                } else {
                    //refresh sentinel config
                    refreshSentinelConfig(doorGoodConfig);
                }
            }
        }

        firstLoadingSentinel(isFirstLoading, degradeRules, gatewayFlowRules, apiDefinitions);
    }

    private void firstLoadingSentinel(boolean isFirstLoading
            , Set<DegradeRule> degradeRules
            , Set<GatewayFlowRule> gatewayFlowRules
            , Set<ApiDefinition> apiDefinitions) {

        if (!PropertiesContainer.valueOf("csp.sentinel.enable", Boolean.class, false)) {
            return;
        }

        if (isFirstLoading) {
            DegradeRuleManager.loadRules(Lists.newArrayList(degradeRules));
            GatewayRuleManager.loadRules(gatewayFlowRules);
            GatewayApiDefinitionManager.loadApiDefinitions(apiDefinitions);
        }
    }

    private Listener getListener() {
        return new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                if (Strings.isNullOrEmpty(configInfo)) {
                    return;
                }
                log.debug("refresh config done,config value is \n{}", configInfo);
                //parse config instance
                DoorGoodConfig doorGoodConfig = JSON.parseObject(configInfo, new TypeReference<DoorGoodConfig>() {
                });
                //refresh gateway config
                refreshGatewayRule(doorGoodConfig);
                //refresh sentinel config
                refreshSentinelConfig(doorGoodConfig);
            }
        };
    }

    private void refreshSentinelConfig(DoorGoodConfig doorGoodConfig) {
        if (!PropertiesContainer.valueOf("csp.sentinel.enable", Boolean.class, false)) {
            return;
        }

        Set<DegradeRule> tempDegradeRules = Sets.newConcurrentHashSet();
        Set<GatewayFlowRule> tempGatewayFlowRules = Sets.newConcurrentHashSet();
        Set<ApiDefinition> tempApiDefinitions = Sets.newConcurrentHashSet();

        tempDegradeRules.addAll(DegradeRuleManager.getRules());
        tempGatewayFlowRules.addAll(GatewayRuleManager.getRules());
        tempApiDefinitions.addAll(GatewayApiDefinitionManager.getApiDefinitions());

        addSentinelConfigForUpdate(tempDegradeRules, tempGatewayFlowRules, tempApiDefinitions, doorGoodConfig);

        DegradeRuleManager.loadRules(Lists.newArrayList(tempDegradeRules));
        GatewayRuleManager.loadRules(tempGatewayFlowRules);
        GatewayApiDefinitionManager.loadApiDefinitions(tempApiDefinitions);
    }

    private void addSentinelConfigForInitLoad(Set<DegradeRule> degradeRules
            , Set<GatewayFlowRule> gatewayFlowRules
            , Set<ApiDefinition> apiDefinitions
            , DoorGoodConfig doorGoodConfig) {

        if (!PropertiesContainer.valueOf("csp.sentinel.enable", Boolean.class, false)) {
            return;
        }

        if (doorGoodConfig.getSentinelConfig() == null) {
            cleanSentinelConfig(degradeRules, gatewayFlowRules, apiDefinitions);
            return;
        }

        if (CollectionUtils.isNotEmpty(doorGoodConfig.getSentinelConfig().getDegradeRule2())) {
            degradeRules.addAll(doorGoodConfig.getSentinelConfig().getDegradeRule2());
        }
        if (CollectionUtils.isNotEmpty(doorGoodConfig.getSentinelConfig().getGatewayFlowRules())) {
            gatewayFlowRules.addAll(doorGoodConfig.getSentinelConfig().getGatewayFlowRules());
        }

        if (CollectionUtils.isNotEmpty(doorGoodConfig.getSentinelConfig().getApiDefinitions())) {
            apiDefinitions.addAll(doorGoodConfig.getSentinelConfig().getApiDefinitions());
        }
    }

    private void addSentinelConfigForUpdate(Set<DegradeRule> degradeRules
            , Set<GatewayFlowRule> gatewayFlowRules
            , Set<ApiDefinition> apiDefinitions
            , DoorGoodConfig doorGoodConfig) {

        if (!PropertiesContainer.valueOf("csp.sentinel.enable", Boolean.class, false)) {
            return;
        }

        if (doorGoodConfig.getSentinelConfig() == null) {
            cleanSentinelConfig(degradeRules, gatewayFlowRules, apiDefinitions);
            return;
        }

        if (CollectionUtils.isNotEmpty(doorGoodConfig.getSentinelConfig().getDegradeRule2())) {
            Map<String, DegradeRule> degradeRuleMap = new ConcurrentHashMap<>(16);
            for (DegradeRule rule : degradeRules) {
                degradeRuleMap.put(rule.getResource(), rule);
            }
            for (DegradeRule rule : doorGoodConfig.getSentinelConfig().getDegradeRule2()) {
                degradeRuleMap.put(rule.getResource(), rule);
            }
            degradeRules.clear();
            degradeRules.addAll(degradeRuleMap.values());
        }
        if (CollectionUtils.isNotEmpty(doorGoodConfig.getSentinelConfig().getGatewayFlowRules())) {
            Map<String, GatewayFlowRule> gatewayFlowRuleMap = new ConcurrentHashMap<>(16);
            for (GatewayFlowRule rule : gatewayFlowRules) {
                gatewayFlowRuleMap.put(rule.getResource(), rule);
            }
            for (GatewayFlowRule rule : doorGoodConfig.getSentinelConfig().getGatewayFlowRules()) {
                gatewayFlowRuleMap.put(rule.getResource(), rule);
            }
            gatewayFlowRules.clear();
            gatewayFlowRules.addAll(gatewayFlowRuleMap.values());
        }

        if (CollectionUtils.isNotEmpty(doorGoodConfig.getSentinelConfig().getApiDefinitions())) {
            Map<String, ApiDefinition> apiDefinitionMap = new ConcurrentHashMap<>(16);
            for (ApiDefinition api : apiDefinitions) {
                apiDefinitionMap.put(api.getApiName(), api);
            }
            for (ApiDefinition api : doorGoodConfig.getSentinelConfig().getApiDefinitions()) {
                apiDefinitionMap.put(api.getApiName(), api);
            }
            apiDefinitions.clear();
            apiDefinitions.addAll(apiDefinitionMap.values());
        }
    }

    private void cleanSentinelConfig(Set<DegradeRule> degradeRules
            , Set<GatewayFlowRule> gatewayFlowRules
            , Set<ApiDefinition> apiDefinitions) {
        degradeRules.clear();
        gatewayFlowRules.clear();
        apiDefinitions.clear();
    }

    private void refreshGatewayRule(DoorGoodConfig doorGoodConfig) {

        for (RouteDefinition route : doorGoodConfig.getRouteDefinition()) {
            routeRepository.update(route);
        }

        if (CollectionUtils.isNotEmpty(doorGoodConfig.getLoadBalancerConfig())) {
            for (LoadBalancerConfig lbConfig : doorGoodConfig.getLoadBalancerConfig()) {
                routeRepository.update(lbConfig);
            }
        }
    }

    private Properties getConfigServerProperties() {
        Properties properties = new Properties();
        properties.put("namespace"
                , PropertiesContainer.valueOfString("spring.cloud.nacos.discovery.namespace", "gateway"));
        properties.put("serverAddr"
                , PropertiesContainer.valueOfStringWithAssertNotNull("spring.cloud.nacos.discovery.server-addr"));
        return properties;
    }

    @Override
    public void destroy() {
        listenerMap.clear();
        log.debug("clea all listener");
    }
}
