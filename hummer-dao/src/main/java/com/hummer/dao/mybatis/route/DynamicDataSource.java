package com.hummer.dao.mybatis.route;

import com.hummer.dao.mybatis.context.MultipleDataSourceMap;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return MultipleDataSourceMap.getDataSource(false);
    }
}
