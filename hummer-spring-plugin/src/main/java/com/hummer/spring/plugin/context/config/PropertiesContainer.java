package com.hummer.spring.plugin.context.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * wrapper property .
 *
 * @author bingy
 */
public final class PropertiesContainer extends PropertyPlaceholderConfigurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesContainer.class);
    private static final Map<String, Object> PROPERTY_MAP = new ConcurrentHashMap<>(16);
    private static final AtomicBoolean LOAD_FLAG = new AtomicBoolean(true);
    private static ConfigurableConversionService conversionService = new DefaultConversionService();
    private static final String APP_PROPERTIES = "applicationConfigurationProperties";
    private static final String APOLLO_PROPERTY_SOURCE_NAME = "ApolloPropertySources";
    private static final String ENV = "spring.profiles.active";

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) {
        super.processProperties(beanFactoryToProcess, props);
        if (LOAD_FLAG.get()) {
            loadData(props);
            LOGGER.info("load properties done by processProperties method,item count {}"
                    , PROPERTY_MAP.size());
        }
    }

    public static Collection<String> allKey() {
        return PROPERTY_MAP.keySet();
    }

    public static Collection<Object> allValues() {
        return PROPERTY_MAP.values();
    }

    public static boolean hasKey(String key) {
        return PROPERTY_MAP.containsKey(key);
    }

    public static <T> T get(String key, Class<T> classType) {
        Object val = PROPERTY_MAP.get(key);
        if (val == null) {
            return null;
        }
        return conversionService.convert(val, classType);
    }

    public static <T> T get(String key, Class<T> classType, T defVal) {
        Object val = PROPERTY_MAP.get(key);
        if (val == null) {
            return defVal;
        }
        return conversionService.convert(val, classType);
    }

    private static void loadData(final Properties props) {
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            PROPERTY_MAP.put(keyStr, value);
        }
    }

    private static void setPropertySource(PropertySource<?> ps) {
        EnumerablePropertySource<?> eps = (EnumerablePropertySource<?>) ps;
        for (String key : eps.getPropertyNames()) {
            PROPERTY_MAP.put(key, eps.getProperty(key));
        }
    }

    public static void loadData(Environment event) {
        if (LOAD_FLAG.get()) {
            ConfigurableEnvironment environment = (ConfigurableEnvironment) event;

            PropertySource<?> appPS = environment.getPropertySources().get(APP_PROPERTIES);
            if (appPS != null) {
                PropertiesContainer.setPropertySource(appPS);
            }

            PropertySource<?> apolloPS = environment.getPropertySources().get(APOLLO_PROPERTY_SOURCE_NAME);
            if (apolloPS != null) {
                PropertiesContainer.setPropertySource(apolloPS);
            }

            String env = event.getProperty(ENV);
            if (env != null) {
                PROPERTY_MAP.put(ENV, env);
            }
            LOAD_FLAG.set(false);
            LOGGER.info("load properties done by loadData method,item count {}"
                    , PROPERTY_MAP.size());
        }
    }
}
