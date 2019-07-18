package com.hummer.dao.mybatis.context;

import com.google.common.base.Strings;
import com.hummer.common.exceptions.SysException;
import org.apache.commons.lang3.Validate;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * cache multiple data source, is thread safe
 *
 * @author bingy
 */
public class MultipleDataSourceMap {
    private MultipleDataSourceMap() {


    }

    private static final CopyOnWriteArraySet<String> DATA_SOURCE_SETS = new CopyOnWriteArraySet<>();
    private static final InheritableThreadLocal<DataSourceMetadata> HOLDER = new InheritableThreadLocal<>();

    public static void setDataSource(final String name) {
        Validate.isTrue(!Strings.isNullOrEmpty(name), "metadata is null");
        DataSourceMetadata metadata = getMeta(name);
        HOLDER.set(metadata);
    }

    /**
     * return thread context cache data source,call method need handle data source null
     *
     * @param checkNull if true then verified metadata , if null then throw exception
     * @return
     */
    public static DataSourceMetadata getDataSource() {
        DataSourceMetadata metadata = HOLDER.get();
        Validate.isTrue(metadata != null, "current thread data source metadata is null");
        return metadata;
    }

    /**
     * current thread context if exists data source metadata instance then return else new metadata instance
     *
     * @param dbName data source name
     * @param dbType data source type
     * @return
     */
    private static DataSourceMetadata getMeta(final String dbName) {
        Validate.isTrue(exists(dbName), String.format("dbName %s is invalid", dbName));

        DataSourceMetadata meta = HOLDER.get();
        if (meta == null) {
            meta = new DataSourceMetadata(dbName);
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
        DATA_SOURCE_SETS.add(dataSourceName);
    }

    public static void cacheDataSourceAll(final Collection<String> dataSourceName) {
        DATA_SOURCE_SETS.addAll(dataSourceName);
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
        return DATA_SOURCE_SETS.contains(dataSourceName);
    }

    /**
     * remove current thread cache data source metadata
     */
    public static void clean() {
        HOLDER.remove();
    }
}
