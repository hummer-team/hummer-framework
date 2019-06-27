package com.hummer.dao.interceptor;

import com.hummer.spring.plugin.context.PropertiesContainer;
import com.hummer.support.SysConsts;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * this class impl mybatis execute sql slow log interceptor
 *
 * @author bingy
 * @since 1.0.0
 */
public class LogInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogInterceptor.class);
    private Boolean isShowSql;
    private Integer sqlTimout;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        //invoke
        Object obj = invocation.proceed();
        long costTime = System.currentTimeMillis() - startTime;
        //record slow sql
        if (Boolean.TRUE.equals(isShowSql) && costTime >= sqlTimout) {
            final Object[] args = invocation.getArgs();
            MappedStatement mappedStatement = (MappedStatement) args[0];
            String sqlId = mappedStatement.getId();
            Object parameterObject = args[1];
            BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
            String sqlStr = boundSql.getSql();
            //out put log
            LOGGER.warn("slow sql,sql id : {},sql info :{}", sqlId, sqlStr);
        }
        return obj;
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
        isShowSql = PropertiesContainer.valueOf(SysConsts.DaoConsts.SHOW_SQL, Boolean.class, Boolean.TRUE);
        sqlTimout = PropertiesContainer.valueOf(SysConsts.DaoConsts.SHOW_TIMEOUT, Integer.class, 3000);
    }
}
