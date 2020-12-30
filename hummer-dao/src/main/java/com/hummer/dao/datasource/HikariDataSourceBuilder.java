package com.hummer.dao.datasource;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * wrapper HikariDataSource.
 *
 * @author lee
 * @link {https://github.com/brettwooldridge/HikariCP}
 */
public class HikariDataSourceBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(HikariDataSourceBuilder.class);

    private HikariDataSourceBuilder() {

    }

    public static HikariDataSourceV2 builderHikariDataSource(final Map<String, Object> map) {
        long start = System.currentTimeMillis();
        HikariDataSourceV2 ds = new HikariDataSourceV2(builderConfig(map));
        setSqlExecuteTimeout(map, ds);
        LOGGER.info("builder Hikari DataSource done,cost {} millis ", System.currentTimeMillis() - start);
        return ds;
    }

    private static void setSqlExecuteTimeout(Map<String, Object> map, HikariDataSourceV2 ds) {
        String queryTimeoutSeconds = (String) map.get("queryTimeout");
        if (!Strings.isNullOrEmpty(queryTimeoutSeconds)) {
            ds.setSqlExecuteTimeoutSecond(Integer.parseInt(queryTimeoutSeconds));
        } else {
            String connectionProperties = (String) map.getOrDefault("connectionProperties"
                    , "socketTimeout=3000");
            if (!Strings.isNullOrEmpty(connectionProperties)) {
                Map<String, String> connMap = Splitter.on(";").withKeyValueSeparator("=").split(connectionProperties);
                ds.setSqlExecuteTimeoutSecond(
                        Integer.parseInt(connMap.getOrDefault("socketTimeout", "3000")) / 1000);
            }
        }
    }

    private static HikariConfig builderConfig(Map<String, Object> map) {

        HikariConfig config = new HikariConfig();
        if (map.containsKey("driverClassName")) {
            config.setJdbcUrl((String) map.get("url"));
            config.setDriverClassName((String) map.get("driverClassName"));
            config.setUsername((String) map.get("username"));
            config.setPassword((String) map.get("password"));
        }
        if (map.containsKey("dataSourceClassName")) {
            config.setDataSourceClassName((String) map.get("dataSourceClassName"));
            //config.addDataSourceProperty("url", map.get("url"));
            config.addDataSourceProperty("user", map.get("username"));
            config.addDataSourceProperty("password", map.get("password"));
            config.addDataSourceProperty("databaseName", map.get("databaseName"));
            config.addDataSourceProperty("serverName",map.get("serverName"));
            config.addDataSourceProperty("portNumber",map.get("portNumber"));
        }
        config.setMinimumIdle(Integer.parseInt((String) map.get("initialSize")));
        config.setMaximumPoolSize(Integer.parseInt(Optional.ofNullable((String) map.get("maxActive"))
                .orElse("3")));
        String initTimeOutMs = (String) map.get("hikariInitTimeOutMs");
        if (!Strings.isNullOrEmpty(initTimeOutMs)) {
            config.setInitializationFailTimeout(Long.parseLong(initTimeOutMs));
        }
        String connectionTestEnable = (String) map.getOrDefault("connectionTestEnable", "true");
        if ("true".equalsIgnoreCase(connectionTestEnable)) {
            config.setConnectionTestQuery("select 1");
        }

        config.setConnectionTimeout(Long.parseLong((String) map.getOrDefault("hikariConnTimeoutMs"
                , "10000")));
        config.setIdleTimeout(Integer.parseInt((String) map.getOrDefault("hikariConnIdleTimeoutMs"
                , "60000")));
        config.setMaxLifetime(Long.parseLong((String) map.getOrDefault("hikariConnMaxLifetimeMs"
                , "180000")));

        config.setValidationTimeout(Long.parseLong(
                (String) map.getOrDefault("validationQueryTimeout", "1000")));

        config.setPoolName((String) map.getOrDefault("poolName", "hikari.ds"));
        String healthCheckProperties = (String) map.getOrDefault("healthCheckProperties"
                , "connectivityCheckTimeoutMs=1000;expected99thPercentileMs=10");
        config.setHealthCheckProperties(toProperties(healthCheckProperties));

        String connectionBeforeVerify = (String) map.get("hikariConnectionBeforeVerify");
        if ("yes".equalsIgnoreCase(connectionBeforeVerify)) {
            config.setConnectionInitSql("select 1");
        }

        config.setMetricsTrackerFactory(new PrometheusMetricsTrackerFactory());
        return config;
    }

    private static Properties toProperties(String stringMap) {
        Map<String, String> connMap = Splitter.on(";").withKeyValueSeparator("=").split(stringMap);
        Properties properties = new Properties();
        for (Map.Entry<String, String> entry : connMap.entrySet()) {
            properties.put(entry.getKey(), entry.getValue());
        }

        return properties;
    }

    public static class HikariDataSourceV2 extends HikariDataSource {

        private int sqlExecuteTimeoutSecond;

        /**
         * Default constructor.  Setters are used to configure the pool.  Using
         * this constructor vs. {@link #HikariDataSource(HikariConfig)} will
         * result in {@link #getConnection()} performance that is slightly lower
         * due to lazy initialization checks.
         * <p>
         * The first call to {@link #getConnection()} starts the pool.  Once the pool
         * is started, the configuration is "sealed" and no further configuration
         * changes are possible -- except via {@link HikariConfigMXBean} methods.
         */
        public HikariDataSourceV2() {
            super();
        }

        /**
         * Construct a HikariDataSource with the specified configuration.  The
         * {@link HikariConfig} is copied and the pool is started by invoking this
         * constructor.
         * <p>
         * The {@link HikariConfig} can be modified without affecting the HikariDataSource
         * and used to initialize another HikariDataSource instance.
         *
         * @param configuration a HikariConfig instance
         */
        public HikariDataSourceV2(HikariConfig configuration) {
            super(configuration);
        }

        public int getSqlExecuteTimeoutSecond() {
            return sqlExecuteTimeoutSecond;
        }

        public void setSqlExecuteTimeoutSecond(int sqlExecuteTimeoutSecond) {
            this.sqlExecuteTimeoutSecond = sqlExecuteTimeoutSecond;
        }
    }
}
