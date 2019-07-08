package com.hummer.dao.mybatis.context;

import com.google.common.collect.Sets;
import com.hummer.common.exceptions.SysException;

import java.util.Set;

/**
 * cache multiple data source
 *
 * @author bingy
 */
public class MultipleDataSourceMap {
    private MultipleDataSourceMap() {


    }

    private static final Set<String> dataSourceSets = Sets.newHashSetWithExpectedSize(6);
    private static final ThreadLocal<DataSourceMetadata> HOLDER = new ThreadLocal<>();

    public static void setDataSource(final String name, final String dbType) {
        DataSourceMetadata metadata = getMeta(name, dbType);
        HOLDER.set(metadata);
    }

    /**
     * return thread context cache data source,call method need handle data source null
     *
     * @param checkNull if true then verified metadata , if null then throw exception
     * @return
     */
    public static DataSourceMetadata getDataSource(final boolean checkNull) {
        DataSourceMetadata metadata = HOLDER.get();
        if (metadata == null && checkNull) {
            throw new SysException(50000, "current thread context no cache DataSourceMetadata.");
        }
        if (metadata == null) {
            return new DataSourceMetadata();
        }
        return metadata;
    }

    /**
     * current thread context if exists data source metadata instance then return else new metadata instance
     *
     * @param dbName data source name
     * @param dbType data source type
     * @return
     */
    private static DataSourceMetadata getMeta(final String dbName, final String dbType) {
        DataSourceMetadata meta = HOLDER.get();
        if (meta == null) {
            meta = new DataSourceMetadata(dbName, dbType);
            HOLDER.set(meta);
        }
        return meta;
    }

    /**
     * cache data source
     *
     * @param dataSourceName name
     * @return void
     * @author liguo
     * @date 2019/7/8 15:44
     * @since 1.0.0
     **/
    public static void cacheDataSource(final String dataSourceName) {
        dataSourceSets.add(dataSourceName);
    }

    /**
     * check input data source if exists
     *
     * @param dataSourceName name
     * @return boolean
     * @author liguo
     * @date 2019/7/8 15:45
     * @since 1.0.0
     **/
    public static boolean exists(final String dataSourceName) {
        return dataSourceSets.contains(dataSourceName);
    }

    /**
     * remove current thread cache data source metadata
     */
    public static void clean() {
        HOLDER.remove();
    }
}
