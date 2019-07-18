package com.hummer.dao.mybatis.context;

import java.util.Objects;

/**
 * @author liguo
 * @date 2019/7/8 15:41
 * @since 1.0.0
 **/
public class DataSourceMetadata {
    private String dbName;
    private String dbType;

    public DataSourceMetadata() {

    }

    public DataSourceMetadata(String dbName) {
        this.dbName = dbName;
    }

    public DataSourceMetadata(String dbName, String dbType) {
        this.dbName = dbName;
        this.dbType = dbType;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbType() {
        return dbType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataSourceMetadata that = (DataSourceMetadata) o;
        return Objects.equals(dbName, that.dbName) &&
                Objects.equals(dbType, that.dbType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbName, dbType);
    }
}
