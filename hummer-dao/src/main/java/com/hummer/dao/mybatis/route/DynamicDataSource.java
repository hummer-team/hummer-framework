package com.hummer.dao.mybatis.route;

import com.hummer.dao.mybatis.context.MultipleDataSourceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * dynamic data source
 *
 * @author bingy
 * @see {@link <a href='https://www.cnblogs.com/softidea/p/7127874.html'>https://www.cnblogs.com/softidea/p/7127874.html</a>}
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSource.class);

    public DynamicDataSource(final @NotNull Map<Object, Object> targetDataSources
            , final @NotNull DataSource defaultTargetDataSource) {
        super.setTargetDataSources(targetDataSources);
        super.setDefaultTargetDataSource(defaultTargetDataSource);
    }

    /**
     * designation target data source key.
     *
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        Object key = MultipleDataSourceMap.getDataSource();
        LOGGER.info("dynamic data source is {}", key);
        return key;
    }
}
