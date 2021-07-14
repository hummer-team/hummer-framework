package com.hummer.core;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hummer.core.exceptions.KeyNotExistsException;
import org.apache.commons.lang3.StringUtils;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * wrapper property .
 *
 * @author bingy
 */
public final class PropertiesContainer extends PropertyPlaceholderConfigurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesContainer.class);
    private static final ConcurrentHashMap<String, Object> PROPERTY_MAP = new ConcurrentHashMap<>(16);
    private static final AtomicBoolean LOAD_FLAG = new AtomicBoolean(true);
    private static final String ENV = "spring.profiles.active";
    private static final String CLASS_RESOURCE = "applicationConfig";
    private static final String USER_PROPERTIES_FILE_PREFIX = "application";
    private static final List<String> CLASS_PATH_USER_RESOURCE_APPLICATION =
            Lists.newArrayList("class path resource [application"
                    , "Config resource 'class path resource [application"
                    , "Config resource 'file [resources\\application");
    private static ConfigurableConversionService conversionService = new DefaultConversionService();

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
     * get properties as  target class type.
     *
     * @param classs key class
     * @param <T>    target T
     * @return direct convert to target type.
     */
    public static <T> T valueOfInstanceDirect(Class<?> classs) {
        Object val = PROPERTY_MAP.get(classs.getName());
        if (val == null) {
            return null;
        }
        return (T) val;
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
    public static int valueOfInteger(final String key, final int defaultVal) {
        return valueOf(key, Integer.class, defaultVal);
    }

    /**
     * get value convert to int,if exists then return defaultVal.
     *
     * @param key
     * @return
     */
    public static int valueOfInteger(final String key, final Supplier<Integer> defaultVal) {
        Integer val = valueOf(key, Integer.class);
        if (val != null) {
            return val;
        }
        Integer[] def = new Integer[1];
        Optional.ofNullable(defaultVal).ifPresent(c -> def[0] = c.get());
        return def[0];
    }

    /**
     * get value convert to long,if exists then return defaultVal.
     *
     * @param key        key
     * @param defaultVal defaultVal
     * @return {@link java.lang.Long}
     */
    public static long valueOfLong(final String key, final Supplier<Long> defaultVal) {
        return valueOf(key, Long.class, defaultVal);
    }

    /**
     * get value convert to boolean,if exists then return defaultVal.
     *
     * @param key        key
     * @param defaultVal defaultVal
     * @return {@link java.lang.Boolean}
     */
    public static boolean valueBoolean(final String key, final Supplier<Boolean> defaultVal) {
        return valueOf(key, Boolean.class, defaultVal);
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
     * get properties as string
     *
     * @param key        key
     * @param defaultVal if key not exists then return default value
     * @return
     */
    public static String valueOfString(final String key, final Supplier<String> defaultVal) {
        String val = valueOf(key, String.class);
        if (val != null) {
            return val;
        }
        String[] def = new String[1];
        Optional.ofNullable(defaultVal).ifPresent(c -> def[0] = c.get());
        return def[0];
    }

    /**
     * get properties as  target class type.
     *
     * @param key       key
     * @param classType target class type
     * @param defVal    if key not exists then return default value
     * @return T target class type,must is basic type
     * @author liguo
     * @date 2019/6/25 17:25
     * @version 1.0.0
     **/
    public static <T> T valueOf(final String key, final Class<T> classType, final T defVal) {
        return get(key, classType, defVal);
    }

    /**
     * get properties as  target class type.
     *
     * @param key       key
     * @param classType target class type
     * @param defVal    if key not exists then return default value
     * @return T target class type,must is basic type
     * @author liguo
     * @date 2019/6/25 17:25
     * @version 1.0.0
     **/
    public static <T> T valueOf(final String key, final Class<T> classType, final Supplier<T> defVal) {
        T val = get(key, classType);
        if (val != null) {
            return val;
        }
        List<T> list = new ArrayList<>(1);
        Optional.ofNullable(defVal).ifPresent(c -> list.add(c.get()));
        return list.get(0);
    }

    /**
     * get this key property value.
     *
     * @param key       key
     * @param classType target type
     * @param defVal    if not exists key,return default value.
     * @return T  target class type,must is basic type
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

    /**
     * put this value
     *
     * @param key   key
     * @param value value
     * @return void
     * @author liguo
     * @date 2019/9/30 17:47
     * @since 1.0.0
     **/
    public static void put(final String key, final Object value) {
        PROPERTY_MAP.put(key, value);
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
     * true： already configuration , false: no load configuration
     *
     * @return
     */
    public static boolean isLoadPropertyData() {
        return LOAD_FLAG.get();
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
            StringBuilder propertyNames = new StringBuilder();
            while (iter.hasNext()) {
                String name = matchUserApplicationPropertiesName(iter.next());
                setPropertySource(environment, name);
                if (!Strings.isNullOrEmpty(name)) {
                    propertyNames.append(name).append(" ,");
                }
            }

            String env = event.getProperty(ENV);
            if (!Strings.isNullOrEmpty(env)) {
                PROPERTY_MAP.put(ENV, env);
            }
            LOAD_FLAG.set(false);
            LOGGER.info("load properties done by loadPropertyData method,item count {},property name {}"
                    , PROPERTY_MAP.size()
                    , propertyNames);
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
     * match user defined configuration name.if no match then return null else return name
     *
     * @param ps property source
     * @return java.lang.String
     * @author liguo
     * @date 2019/6/19 16:45
     * @version 1.0.0
     **/
    private static String matchUserApplicationPropertiesName(PropertySource<?> ps) {
        String name = ps.getName();
        if (StringUtils.isNoneEmpty(name) &&
                (name.startsWith(CLASS_RESOURCE)
                        || CLASS_PATH_USER_RESOURCE_APPLICATION.stream()
                        .anyMatch(p -> StringUtils.startsWithIgnoreCase(name, p))
                        || name.contains(USER_PROPERTIES_FILE_PREFIX))) {
            return name;
        }
        return null;
    }

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
}
