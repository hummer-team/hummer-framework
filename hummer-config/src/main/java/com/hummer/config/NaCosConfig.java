package com.hummer.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.hummer.common.utils.DateUtil;
import com.hummer.common.utils.IpUtil;
import com.hummer.config.agent.ClientConfigAgent;
import com.hummer.config.bo.NacosConfigParams;
import com.hummer.config.dto.ClientConfigUploadReqDto;
import com.hummer.core.PropertiesContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class NaCosConfig implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(NaCosConfig.class);

    public void initNaCosConfig() {

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
    @Override
    public void afterPropertiesSet() throws Exception {
        final long start = System.currentTimeMillis();
        LOGGER.info("begin append nacos config to PropertiesContainer");

        putConfigToContainer(true);
        
        LOGGER.info("append nacos config to PropertiesContainer done,cos {} ms ",
                System.currentTimeMillis() - start);
    }

    public void putConfigToContainer(boolean addListener) throws Exception {
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
                        if (!Strings.isNullOrEmpty(configInfo)) {
                            try {
                                putConfigToContainer(groupId, dataId, configInfo);
                                LOGGER.info("receive for nacos config change notice,chance config is [{}]"
                                        , configInfo);
                            } catch (IOException e) {
                                //ignore
                            }
                        }
                        // 客户端配置上传至服务端
                        uploadConfig();
                    }
                });
            }
            String value = configService.getConfig(dataId, groupId, 3000);
            if (!Strings.isNullOrEmpty(value)) {
                putConfigToContainer(groupId, dataId, value);
            }
        }
        // 客户端配置上传至服务端
        uploadConfig();
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
        NacosConfigParams configParams = new NacosConfigParams();
        String service = PropertiesContainer.valueOfStringWithAssertNotNull("nacos.config.server-addr");
        //nacos properties
        Properties properties = new Properties();
        properties.put("serverAddr", service);

        List<String> groupIdList = Splitter.on(",").splitToList(groupIds);
        List<String> dataIdList = Splitter.on(",").splitToList(dataIds);
        configParams.setDataIdList(dataIdList);
        configParams.setGroupIdList(groupIdList);
        configParams.setProperties(properties);
        return configParams;
    }

    private void putConfigToContainer(String groupId, String dataId, String value) throws IOException {
        Properties properties1 = new Properties();
        properties1.load(new StringReader(value));
        for (Map.Entry<Object, Object> map : properties1.entrySet()) {
            PropertiesContainer.put(map.getKey().toString(), map.getValue());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("nacos config data id {},group id {},config {} put to PropertiesContainer"
                        , dataId, groupId, map);
            }
        }
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
}
