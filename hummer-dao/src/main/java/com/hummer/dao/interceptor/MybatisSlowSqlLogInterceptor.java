package com.hummer.dao.interceptor;

import com.hummer.common.SysConstant;
import com.hummer.core.PropertiesContainer;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * this class impl mybatis execute sql slow log interceptor
 *
 * @author bingy
 * @since 1.0.0
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {
                MappedStatement.class
                , Object.class
        }),
        @Signature(type = Executor.class, method = "query", args = {
                MappedStatement.class
                , Object.class
                , RowBounds.class
                , ResultHandler.class})
})
public class MybatisSlowSqlLogInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisSlowSqlLogInterceptor.class);
    private Boolean isShowSql;
    private Integer sqlTimout;

    /**
     * new log interceptor instance
     *
     * @return @seen {@link MybatisSlowSqlLogInterceptor}
     */
    public static MybatisSlowSqlLogInterceptor instance() {
        return new MybatisSlowSqlLogInterceptor();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        //invoke
        Object obj = invocation.proceed();
        long costTime = System.currentTimeMillis() - startTime;
        //record slow sql
        if (costTime >= sqlTimout) {
            final Object[] args = invocation.getArgs();
            MappedStatement mappedStatement = (MappedStatement) args[0];
            String sqlId = mappedStatement.getId();
            Object parameterObject = args[1];
            BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
            String sqlStr = boundSql.getSql();
            //out put log
            LOGGER.warn("slow sql,sql id : {},sql cost {} millis,sql info :{}"
                    , sqlId
                    , costTime
                    , isShowSql ? sqlStr : "");
        }
        return obj;
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
        isShowSql = PropertiesContainer.valueOf(SysConstant.DaoConstant.SHOW_SQL, Boolean.class, Boolean.TRUE);
        sqlTimout = PropertiesContainer.valueOf(SysConstant.DaoConstant.SHOW_TIMEOUT, Integer.class, 3000);
    }
}
