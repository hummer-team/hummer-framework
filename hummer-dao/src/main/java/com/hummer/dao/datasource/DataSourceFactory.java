package com.hummer.dao.datasource;

import javax.sql.DataSource;
import java.util.Map;

public class DataSourceFactory {
    private DataSourceFactory() {

    }

    public static DataSource factory(final Map<String, Object> dsMap) {
        String driverName = (String) dsMap.getOrDefault("poolDriver", "druid");
        if ("druid".equalsIgnoreCase(driverName)) {
            return DruidDataSourceBuilder.buildDataSource(dsMap);
        }
        if ("Hikari".equalsIgnoreCase(driverName)) {
            return HikariDataSourceBuilder.builderHikariDataSource(dsMap);
        }

        throw new IllegalArgumentException("pool driver name invalid");
    }
}
