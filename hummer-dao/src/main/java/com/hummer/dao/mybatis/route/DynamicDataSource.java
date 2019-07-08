package com.hummer.dao.mybatis.route;

import com.hummer.dao.mybatis.context.MultipleDataSourceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * dynamic data source
 *
 * @author bingy
 * @see {@link <a href='https://www.cnblogs.com/softidea/p/7127874.html'>https://www.cnblogs.com/softidea/p/7127874.html</a>}
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSource.class);

    /**
     * designation target data source key.
     *
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        Object key = MultipleDataSourceMap.getDataSource(false);
        LOGGER.info("dynamic data source {}", key);
        return key;
    }
}
