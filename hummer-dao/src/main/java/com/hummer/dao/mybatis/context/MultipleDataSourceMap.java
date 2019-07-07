package com.hummer.dao.mybatis.context;

import com.hummer.common.exceptions.SysException;

/**
 * cache multiple data source
 *
 * @author bingy
 */
public class MultipleDataSourceMap {
    private MultipleDataSourceMap() {


    }

    private static final ThreadLocal<DataSourceMetadata> HOLDER = new ThreadLocal<>();

    public static void setDataSource(String name, String dbType) {
        DataSourceMetadata metadata = getMeta(name, dbType);
        HOLDER.set(metadata);
    }

    /**
     * return thread context cache data source,call method need handle data source null
     *
     * @param checkNull if true then verified metadata , if null then throw exception
     * @return
     */
    public static DataSourceMetadata getDataSource(boolean checkNull) {
        DataSourceMetadata metadata = HOLDER.get();
        if (metadata == null && checkNull) {
            throw new SysException(50000, "current thread context no cache DataSourceMetadata.");
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
    private static DataSourceMetadata getMeta(String dbName, String dbType) {
        DataSourceMetadata meta = HOLDER.get();
        if (meta == null) {
            meta = new DataSourceMetadata(dbName, dbType);
            HOLDER.set(meta);
        }
        return meta;
    }

    /**
     * remove current thread cache data source metadata
     */
    public static void clean() {
        HOLDER.remove();
    }
}
