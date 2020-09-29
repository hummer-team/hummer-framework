package com.hummer.dao.datasource;

import javax.sql.DataSource;
import java.util.Map;

public class DataSourceFactory {
    private DataSourceFactory() {

    }

    public static DataSource factory(final Map<String, Object> dsMap) {
        String driverTypeName = (String) dsMap.getOrDefault("poolDriverType", "druid");
        if ("druid".equalsIgnoreCase(driverTypeName)) {
            return DruidDataSourceBuilder.buildDataSource(dsMap);
        }
        if ("hikari".equalsIgnoreCase(driverTypeName)) {
            return HikariDataSourceBuilder.builderHikariDataSource(dsMap);
        }

        throw new IllegalArgumentException("pool driver name " + driverTypeName + " invalid");
    }
}
