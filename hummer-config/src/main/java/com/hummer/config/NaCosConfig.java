package com.hummer.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.hummer.common.utils.DateUtil;
import com.hummer.common.utils.IpUtil;
import com.hummer.config.agent.ClientConfigAgent;
import com.hummer.config.bo.ConfigDataInfoBo;
import com.hummer.config.bo.ConfigListenerKey;
import com.hummer.config.bo.ConfigPropertiesChangeInfoBo;
import com.hummer.config.bo.NacosConfigParams;
import com.hummer.config.dto.ClientConfigUploadReqDto;
import com.hummer.config.listener.AbstractConfigListener;
import com.hummer.config.subscription.ConfigCacheManager;
import com.hummer.config.subscription.ConfigSubscriptionManager;
import com.hummer.core.PropertiesContainer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class NaCosConfig implements DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(NaCosConfig.class);

    private ConfigSubscriptionManager configSubscriptionManager;

    private final ConfigCacheManager configCacheManager;

    public NaCosConfig(ConfigSubscriptionManager configSubscriptionManager) {
        this.configSubscriptionManager = configSubscriptionManager;
    }

    {
        configCacheManager = new ConfigCacheManager();
    }

    /**
     * Invoked by the containing {@code BeanFactory} after it has set all bean properties
     * and satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
     * <p>This method allows the bean instance to perform validation of its overall
     * configuration and final initialization when all bean properties have been set.
     *
     * @throws Exception in the event of misconfiguration (such as failure to set an
     *                   essential property) or if initialization fails for any other reason
     */
    public void afterPropertiesSet() throws Exception {
        final long start = System.currentTimeMillis();
        LOGGER.info("begin append nacos config to PropertiesContainer");

        registerConfigListener(true);

        LOGGER.info("append nacos config to PropertiesContainer done,cos {} ms ",
                System.currentTimeMillis() - start);
    }

    public void refreshConfig() {
        try {
            registerConfigListener(false);
        } catch (NacosException e) {
            LOGGER.warn("refresh config failed ", e);
        }
    }

    public void refreshConfig(boolean addListener) {
        try {
            registerConfigListener(addListener);
        } catch (NacosException e) {
            LOGGER.warn("refresh config failed ", e);
        }
    }

    public void registerConfigListener(boolean addListener) throws NacosException {
        NacosConfigParams params = createNacosConfigParams();
        if (params == null) {
            return;
        }
        //nacos server instance
        ConfigService configService = NacosFactory.createConfigService(params.getProperties());
        for (int i = 0; i < params.getGroupIdList().size(); i++) {
            String groupId = params.getGroupIdList().get(i);
            String dataId = i <= params.getDataIdList().size() ? params.getDataIdList().get(i) : null;
            if (Strings.isNullOrEmpty(groupId) || Strings.isNullOrEmpty(dataId)) {
                continue;
            }
            if (addListener) {

                configService.addListener(dataId, groupId, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {

                        handleConfigChange(groupId, dataId, configInfo, params);
                    }
                });

            }
            String value = configService.getConfig(dataId, groupId, 3000);
            if (!Strings.isNullOrEmpty(value)) {
                ConfigDataInfoBo dataInfoBo = configCacheManager.putConfigToContainer(groupId
                        , dataId
                        , value
                        , params.getProperties().getProperty("dataType"));
                //
                handleConfigChange(groupId, dataId, value, params);
            }
        }
        // 客户端配置上传至服务端
        uploadConfig();
    }

    private void handleConfigChange(String groupId, String dataId, String configInfo, NacosConfigParams params) {
        // 更新项目配置本地缓存
        ConfigDataInfoBo dataInfoBo = configCacheManager.putConfigToContainer(groupId, dataId, configInfo
                , params.getProperties().getProperty("dataType"));
        // 客户端配置上传至服务端
        uploadConfig();
        // 配置变更订阅处理
        configChangeDispatch(dataInfoBo);
    }


    private void configChangeDispatch(ConfigDataInfoBo dataInfoBo) {
        if (configSubscriptionManager == null) {
            return;
        }
        // 比对新旧配置，获得变化的属性信息
        List<ConfigPropertiesChangeInfoBo> changeInfoBos
                = configCacheManager.parsingConfigPropertiesChanges(dataInfoBo);
        configSubscriptionManager.doDispatch(dataInfoBo, changeInfoBos);
    }

    private NacosConfigParams createNacosConfigParams() {

        String dataIds = PropertiesContainer.valueOfString("nacos.config.data-ids");
        if (Strings.isNullOrEmpty(dataIds)) {
            LOGGER.warn("no setting nacos data id,PropertiesContainer nacos config will is empty.");
            return null;
        }
        String groupIds = PropertiesContainer.valueOfString("nacos.config.group");
        if (Strings.isNullOrEmpty(groupIds)) {
            LOGGER.warn("no setting nacos group id,PropertiesContainer nacos config will is empty.");
            return null;
        }
        String namespace = PropertiesContainer.valueOfString("nacos.config.namespace");
        NacosConfigParams configParams = new NacosConfigParams();
        String service = PropertiesContainer.valueOfStringWithAssertNotNull("nacos.config.server-addr");
        //nacos properties
        Properties properties = new Properties();
        properties.put("serverAddr", service);
        if (StringUtils.isNotBlank(namespace)) {
            properties.put("namespace", namespace);
        }
        properties.put("dataType", PropertiesContainer.valueOfString("nacos.config.type", "properties"));
        List<String> groupIdList = Splitter.on(",").splitToList(groupIds);
        List<String> dataIdList = Splitter.on(",").splitToList(dataIds);
        configParams.setDataIdList(dataIdList);
        configParams.setGroupIdList(groupIdList);
        configParams.setProperties(properties);
        return configParams;
    }


    private void uploadConfig() {
        Boolean enable = PropertiesContainer.get("nacos.config.upload.enable", Boolean.class, true);
        if (!enable) {
            return;
        }
        uploadConfigAsync();
    }

    private void uploadConfigAsync() {
        CompletableFuture.supplyAsync(() -> {
            ClientConfigAgent.uploadConfig(composeClientConfigUploadReqDto());
            return Void.TYPE;
        });
    }

    private ClientConfigUploadReqDto composeClientConfigUploadReqDto() {
        Map<String, String> map = new HashMap<>(16);
        PropertiesContainer.allKey().forEach(key -> map.put(key, PropertiesContainer.get(key, String.class)));
        ClientConfigUploadReqDto reqDto = new ClientConfigUploadReqDto();
        reqDto.setBusinessCode("panli.application.config");
        reqDto.setOperationType("config");
        Map<String, String> extentMap = new HashMap<>(16);
        extentMap.put("appIp", IpUtil.getLocalIp());
        extentMap.put("appName", PropertiesContainer.get("spring.application.name", String.class));
        extentMap.put("appPort", PropertiesContainer.get("server.port", String.class));
        extentMap.put("configInfo", JSON.toJSONString(map));
        reqDto.setExtendData(extentMap);
        reqDto.setNewValue(JSON.toJSONString(map));
        reqDto.setOperatorName(extentMap.get("appName"));
        reqDto.setOperatorId(extentMap.get("appIp"));
        reqDto.setRemark(extentMap.get("appPort"));
        reqDto.setOperatorTime(DateUtil.now());
        return reqDto;
    }

    /**
     * Invoked by the containing {@code BeanFactory} on destruction of a bean.
     *
     * @throws Exception in case of shutdown errors. Exceptions will get logged
     *                   but not rethrown to allow other beans to release their resources as well.
     */
    @Override
    public void destroy() throws Exception {

    }

    public int addListener(ConfigListenerKey key, AbstractConfigListener listener) {

        return this.configSubscriptionManager.addListener(key, listener);
    }

    public void removeListener(ConfigListenerKey key) {

        this.configSubscriptionManager.removeListener(key);
    }

    public void removeListener(ConfigListenerKey key, AbstractConfigListener listener) {

        this.configSubscriptionManager.removeListener(key, listener);
    }
}
