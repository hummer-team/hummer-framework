package com.hummer.core;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.hummer.core.exceptions.KeyNotExistsException;
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
import java.util.Iterator;
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
    private static final String APOLLO_PROPERTY_SOURCE_NAME = "ApolloPropertySources";
    private static final String ENV = "spring.profiles.active";
    private static final String CLASS_RESOURCE = "applicationConfig";


    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess
            , Properties props) {
        super.processProperties(beanFactoryToProcess, props);
        if (LOAD_FLAG.get()) {
            loadPropertyData(props);
            LOGGER.info("load properties done by processProperties method,item count {}"
                    , PROPERTY_MAP.size());
        }
    }

    /**
     * return property all keys.
     *
     * @param []
     * @return <code>java.util.Collection<java.lang.String></code>
     * @author liguo
     * @date 2019/6/19 16:53
     * @version 1.0.0
     **/
    public static Collection<String> allKey() {
        return PROPERTY_MAP.keySet();
    }

    /**
     * return all property values.
     *
     * @param []
     * @return <code>java.util.Collection<java.lang.Object></code>
     * @author liguo
     * @date 2019/6/19 16:53
     * @version 1.0.0
     **/
    public static Collection<Object> allValues() {
        return PROPERTY_MAP.values();
    }

    /**
     * check this keys if exists.
     *
     * @param key key
     * @return boolean
     * @author liguo
     * @date 2019/6/19 16:54
     * @version 1.0.0
     **/
    public static boolean hasKey(final String key) {
        return PROPERTY_MAP.containsKey(key);
    }

    /**
     * scan property all key return match key prefix
     *
     * @param keyPrefix key prefix
     * @return {@link java.util.Map<java.lang.String,java.lang.Object>}
     * @throws IllegalArgumentException key prefix is null.
     * @author liguo
     * @date 2019/6/26 16:12
     * @version 1.0.0
     **/
    public static Map<String, Object> scanKeys(final String keyPrefix) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(keyPrefix), "key prefix can not null.");

        Map<String, Object> map = Maps.newHashMapWithExpectedSize(PROPERTY_MAP.size());

        PROPERTY_MAP.entrySet()
                .stream()
                .filter(k -> k.getKey().startsWith(keyPrefix))
                .forEach(entry -> map.put(entry.getKey(), entry.getValue()));

        return map;
    }

    /**
     * get this key property value.
     *
     * @param key       key
     * @param classType target type
     * @return T
     * @author liguo
     * @date 2019/6/19 16:55
     * @version 1.0.0
     **/
    public static <T> T get(final String key, final Class<T> classType) {
        Object val = PROPERTY_MAP.get(key);
        if (val == null) {
            return null;
        }
        return conversionService.convert(val, classType);
    }

    /**
     * get properties as  target class type.
     *
     * @param key       key
     * @param classType target class type
     * @return T
     * @author liguo
     * @date 2019/6/25 17:27
     * @version 1.0.0
     **/
    public static <T> T valueOf(final String key, final Class<T> classType) {
        return get(key, classType);
    }

    /**
     * get properties as string
     *
     * @param key key
     * @return
     */
    public static String valueOfString(final String key) {
        return get(key, String.class);
    }

    /**
     * get properties as string and assert value not null.
     *
     * @param key
     * @return
     * @throws KeyNotExistsException
     */
    public static String valueOfStringWithAssertNotNull(final String key) {
        String result = get(key, String.class);
        if (Strings.isNullOrEmpty(result)) {
            throw new KeyNotExistsException(40000, String.format("key `%s` not settings properties values", key));
        }
        return result;
    }

    /**
     * get properties as string and assert value not null.
     *
     * @param key key
     * @return
     * @throws KeyNotExistsException
     */
    public static <T> T valueOfWithAssertNotNull(final String key, Class<T> classType) {
        T result = valueOf(key, classType);
        if (result == null) {
            throw new KeyNotExistsException(40000, String.format("key `%s` not settings properties values", key));
        }
        return result;
    }


    /**
     * get value convert to int,if exists then return 0.
     *
     * @param key
     * @return
     */
    public static int valueOfInteger(final String key) {
        return valueOf(key, Integer.class, 0);
    }

    /**
     * get value convert to int,if exists then return defaultVal.
     *
     * @param key
     * @return
     */
    public static int valueOfInteger(final String key,final int defaultVal) {
        return valueOf(key, Integer.class, defaultVal);
    }

    /**
     * get properties as string
     *
     * @param key        key
     * @param defaultVal if key not exists then return default value
     * @return
     */
    public static String valueOfString(final String key, final String defaultVal) {
        return get(key, String.class, defaultVal);
    }

    /**
     * get properties as  target class type.
     *
     * @param key       key
     * @param classType target class type
     * @param defVal    if key not exists then return default value
     * @return T target class type
     * @author liguo
     * @date 2019/6/25 17:25
     * @version 1.0.0
     **/
    public static <T> T valueOf(final String key, final Class<T> classType, final T defVal) {
        return get(key, classType, defVal);
    }

    /**
     * get this key property value.
     *
     * @param key       key
     * @param classType target type
     * @param defVal    if not exists key,return default value.
     * @return T
     * @author liguo
     * @date 2019/6/19 16:56
     * @version 1.0.0
     **/
    public static <T> T get(final String key, final Class<T> classType, final T defVal) {
        Object val = PROPERTY_MAP.get(key);
        if (val == null) {
            return defVal;
        }
        return conversionService.convert(val, classType);
    }

    private static void loadPropertyData(final Properties props) {
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

    /**
     * load property configuration data to map.
     *
     * @param event environment
     * @return void
     * @author liguo
     * @date 2019/6/19 16:51
     * @version 1.0.0
     **/
    public static synchronized void loadPropertyData(Environment event) {
        if (LOAD_FLAG.get()) {
            ConfigurableEnvironment environment = (ConfigurableEnvironment) event;
            Iterator<PropertySource<?>> iter = environment.getPropertySources().iterator();
            while (iter.hasNext()) {
                String name = parseApplicationPropertiesName(iter.next());
                setPropertySource(environment, name);
            }

            PropertySource<?> apolloPS = environment
                    .getPropertySources()
                    .get(APOLLO_PROPERTY_SOURCE_NAME);
            if (apolloPS != null) {
                PropertiesContainer.setPropertySource(apolloPS);
            }

            String env = event.getProperty(ENV);
            if (env != null) {
                PROPERTY_MAP.put(ENV, env);
            }
            LOAD_FLAG.set(false);
            LOGGER.info("load properties done by loadPropertyData method,item count {}"
                    , PROPERTY_MAP.size());
        }
    }

    private static void setPropertySource(ConfigurableEnvironment environment, String name) {
        if (!Strings.isNullOrEmpty(name)) {
            PropertySource<?> appPS = environment.getPropertySources().get(name);
            if (appPS != null) {
                PropertiesContainer.setPropertySource(appPS);
            }
        }
    }

    /**
     * parse user defined configuration name.
     *
     * @param ps property source
     * @return java.lang.String
     * @author liguo
     * @date 2019/6/19 16:45
     * @version 1.0.0
     **/
    private static String parseApplicationPropertiesName(PropertySource<?> ps) {
        String name = ps.getName();
        if (name != null && name.startsWith(CLASS_RESOURCE)) {
            return name;
        }
        return null;
    }
}
