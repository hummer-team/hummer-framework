package com.hummer.dao.interceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DbTypeInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbTypeInterceptor.class);

    private static final String DBTYPE_SQLSERVER = "Microsoft SQL Server";

    public static DbTypeInterceptor instance(){
        return new DbTypeInterceptor();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String dbType = ((Connection) invocation.getArgs()[0]).getMetaData().getDatabaseProductName();
        LOGGER.info("DbTypeInterceptor intercept db type {}", dbType);
        if (DBTYPE_SQLSERVER.equals(dbType)) {
            //MultipleDataSourceMap.setDataType(JdbcUtils.SQL_SERVER);
        } else {
            //MultipleDataSourceMap.setDataType(JdbcUtils.MYSQL);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object arg0) {
        if (arg0 instanceof StatementHandler) {
            return Plugin.wrap(arg0, this);
        } else {
            return arg0;
        }
    }

    @Override
    public void setProperties(Properties p) {

    }

}
