package com.hummer.doorgod.service.domain.init;

import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.base.Strings;
import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import com.hummer.core.spi.CustomizeContextInit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;


/**
 * @author lee
 */
@Slf4j
public class GatewayContextInit implements CustomizeContextInit {

    @SneakyThrows
    @Override
    public void init(ConfigurableApplicationContext context) {
        loadConfig();
    }

    private void loadConfig() throws NacosException, IOException {
        loadNacosConfig();
        registerSystemProperty();
        registerSentinelCspConfig();
        registerNettyConfig();
        registerFlowClusterClientConfig();
        registerSentinelSystemRuleConfig();
    }

    private void loadNacosConfig() throws NacosException, IOException {
        Properties properties = getConfigServerProperties();
        properties.setProperty("namespace", "bumblebee-service-panli-com");
        String groupId = PropertiesContainer.valueOfString("config.center.gateway.group.id"
                , "DEFAULT_GROUP");
        long timeoutMillis = PropertiesContainer.valueOf("config.center.gateway.timeout.millis"
                , Long.class, 5000L);
        //load all gateway group config
        ConfigService configService = NacosFactory.createConfigService(properties);
        String val = configService.getConfigAndSignListener(getDataIdByEvn()
                , groupId
                , timeoutMillis
                , new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        try {
                            fillByProperties(configInfo, getDataIdByEvn(), groupId);
                            registerSystemProperty();
                            registerFlowClusterClientConfig();
                            registerSentinelSystemRuleConfig();
                        } catch (IOException e) {
                            log.warn("reset properties to PropertiesContainer failed", e);
                            //ignore
                        }
                    }
                });
        fillByProperties(val, getDataIdByEvn(), groupId);
    }

    private void registerSystemProperty() {
        System.setProperty("csp.sentinel.enable"
                , PropertiesContainer.valueOfString("csp.sentinel.enable", "false"));
    }

    private void registerNettyConfig() {
        //netty config
        System.setProperty("reactor.netty.ioWorkerCount"
                , PropertiesContainer.valueOfString("system.netty.reactor.netty.ioWorkerCount"
                        , "" + Integer.max(Runtime.getRuntime().availableProcessors() * 8, 8)));
        System.setProperty("io.netty.eventLoopThreads"
                , PropertiesContainer.valueOfString("system.netty.io.netty.eventLoopThreads"
                        , "" + Integer.max(Runtime.getRuntime().availableProcessors() * 2, 8)));
        System.setProperty("io.netty.transport.noNative"
                , "" + PropertiesContainer.valueOf("system.netty.io.netty.transport.noNative"
                        , Boolean.class
                        , false));
        log.debug("init netty config done.");

    }

    private void registerSentinelCspConfig() {
        System.setProperty("csp.sentinel.log.dir", PropertiesContainer.valueOfString("csp.sentinel.log.dir"));
        System.setProperty("csp.sentinel.dashboard.server"
                , PropertiesContainer.valueOfString("csp.sentinel.dashboard.server"
                        , "localhost:8080"));
        System.setProperty("csp.sentinel.api.port"
                , "" + PropertiesContainer.valueOfInteger("csp.sentinel.api.port", 8720));
        System.setProperty("project.name", PropertiesContainer.valueOfString("spring.application.name"));
        log.debug("init sentinel dir config done.");
    }

    private void registerSentinelSystemRuleConfig() {
        //sentinel system config
        if (PropertiesContainer.valueOf("system.sentinel.sys.rule.enable", Boolean.class, true)) {
            SystemRule sysRule = new SystemRule();
            sysRule.setMaxThread(PropertiesContainer.valueOfInteger("system.sentinel.max.thread", 1000));
            sysRule.setHighestCpuUsage(PropertiesContainer.valueOf("system.sentinel.max.cpu.use"
                    , Double.class, 0.85));
            sysRule.setQps(PropertiesContainer.valueOfInteger("system.sentinel.max.qps", 10000));
            sysRule.setResource("system.sentinel");
            //register sys rule.
            SystemRuleManager.loadRules(Collections.singletonList(sysRule));
            log.debug("init sentinel rule config done.");
        }
    }

    private void registerFlowClusterClientConfig() {
        /**
         if (!PropertiesContainer.valueOf("doorgod-flow-controller-cluster.enable", Boolean.class, false)) {
         ClusterClientConfigManager.applyNewConfig(new ClusterClientConfig());
         ClusterClientConfigManager.applyNewAssignConfig(new ClusterClientAssignConfig());
         return;
         }

         ClusterClientAssignConfig clientAssignConfig = new ClusterClientAssignConfig();
         clientAssignConfig.setServerHost(PropertiesContainer.valueOfString("doorgod-flow-controller-cluster.host"));
         clientAssignConfig.setServerPort(PropertiesContainer.valueOfInteger("doorgod-flow-controller-cluster.port"));
         ClusterClientConfigManager.applyNewAssignConfig(clientAssignConfig);

         ClusterClientConfig clientConfig = new ClusterClientConfig();
         clientConfig.setRequestTimeout(PropertiesContainer.valueOfInteger(
         "doorgod-flow-controller-cluster.req.timeout", 200));
         ClusterClientConfigManager.applyNewConfig(clientConfig);

         log.debug("register flow cluster config done.");**/
    }

    private void fillByProperties(String configVal, String dataId, String groupId) throws IOException {
        if (Strings.isNullOrEmpty(configVal)) {
            return;
        }
        Properties properties = new Properties();
        properties.load(new StringReader(configVal));
        for (Map.Entry<Object, Object> map : properties.entrySet()) {
            PropertiesContainer.put(map.getKey().toString(), map.getValue());
            if (log.isDebugEnabled()) {
                log.debug("nacos config data id {},group id {},config {} put to PropertiesContainer"
                        , dataId, groupId, map);
            }
        }
    }

    private String getDataIdByEvn() {
        String env = SpringApplicationContext
                .getApplicationContext()
                .getEnvironment()
                .getProperty("spring.profiles.active");
        return String.format("application-%s.properties", env);
    }

    private Properties getConfigServerProperties() {
        Properties properties = new Properties();
        properties.put("namespace"
                , PropertiesContainer.valueOfString("spring.cloud.nacos.discovery.namespace", "gateway"));
        properties.put("serverAddr"
                , PropertiesContainer.valueOfStringWithAssertNotNull("spring.cloud.nacos.discovery.server-addr"));
        return properties;
    }
}
