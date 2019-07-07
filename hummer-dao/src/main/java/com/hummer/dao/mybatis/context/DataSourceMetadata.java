package com.hummer.dao.mybatis.context;

public class DataSourceMetadata {
    private String dbName;
    private String dbType;

    public DataSourceMetadata(){

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

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
}
