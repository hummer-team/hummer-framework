package com.hummer.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.hummer.core.PropertiesContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
        String dataIds = PropertiesContainer.valueOfString("nacos.config.data-ids");
        if (Strings.isNullOrEmpty(dataIds)) {
            LOGGER.warn("no setting nacos data id,PropertiesContainer nacos config will is empty.");
            return;
        }
        String groupIds = PropertiesContainer.valueOfString("nacos.config.group");
        if (Strings.isNullOrEmpty(groupIds)) {
            LOGGER.warn("no setting nacos group id,PropertiesContainer nacos config will is empty.");
            return;
        }
        final long start = System.currentTimeMillis();
        LOGGER.info("begin append nacos config to PropertiesContainer");
        String service = PropertiesContainer.valueOfStringWithAssertNotNull("nacos.config.server-addr");
        //nacos properties
        Properties properties = new Properties();
        properties.put("serverAddr", service);
        //nacos server instance
        ConfigService configService = NacosFactory.createConfigService(properties);

        List<String> groupIdList = Splitter.on(",").splitToList(groupIds);
        List<String> dataIdList = Splitter.on(",").splitToList(dataIds);

        for (int i = 0; i < groupIdList.size(); i++) {
            String groupId = groupIdList.get(i);
            String dataId = i <= dataIdList.size() ? dataIdList.get(i) : null;
            if (Strings.isNullOrEmpty(groupId) || Strings.isNullOrEmpty(dataId)) {
                continue;
            }
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
                }
            });
            String value = configService.getConfig(dataId, groupId, 3000);
            if (!Strings.isNullOrEmpty(value)) {
                putConfigToContainer(groupId, dataId, value);
            }
        }

        LOGGER.info("append nacos config to PropertiesContainer done,cos {} ms ",
                System.currentTimeMillis() - start);
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
}
