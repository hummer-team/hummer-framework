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


    public static HikariDataSource builderHikariDataSource(Map<String, Object> map) {
        long start = System.currentTimeMillis();
        HikariDataSource ds = new HikariDataSource(builderConfig(map));
        LOGGER.info("builder Hikari DataSource done,cost {} millis ", System.currentTimeMillis() - start);
        return ds;
    }


    private static HikariConfig builderConfig(Map<String, Object> map) {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl((String) map.get("url"));
        config.setDriverClassName((String) map.get("driverClassName"));
        config.setUsername((String) map.get("username"));
        config.setPassword((String) map.get("password"));
        config.setMaximumPoolSize(Integer.parseInt(Optional.ofNullable((String) map.get("maxActive"))
                .orElse("3")));
        config.setConnectionTestQuery("select 1");


        config.setConnectionTimeout(10000L);
        String connectionProperties = (String) map.get("connectionProperties");
        if (!Strings.isNullOrEmpty(connectionProperties)) {
            Map<String, String> connMap = Splitter.on(";").withKeyValueSeparator("=").split(connectionProperties);
            config.setConnectionTimeout(Long.parseLong(connMap.getOrDefault("connectTimeout", "10000")));
        }

        config.setIdleTimeout(Integer.parseInt(Optional.ofNullable((String) map.get("minIdle"))
                .orElse("60000")));
        config.setMaxLifetime(config.getMinimumIdle() * 3);

        config.setValidationTimeout(Long.parseLong(
                (String) map.getOrDefault("validationQueryTimeout", "1000")));

        config.setPoolName((String) map.getOrDefault("poolName", "hikari.ds"));

        //Properties healthCheckProperties = new  Properties();

        String healthCheckProperties = (String) map.getOrDefault("healthCheckProperties"
                , "connectivityCheckTimeoutMs=1000;expected99thPercentileMs=10");
        config.setHealthCheckProperties(toProperties(healthCheckProperties));
        config.setConnectionInitSql("select 1");
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
}
