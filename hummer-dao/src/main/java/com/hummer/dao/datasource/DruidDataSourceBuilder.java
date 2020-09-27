package com.hummer.dao.datasource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hummer.common.ErrorCodeConsts;
import com.hummer.common.SysConstant;
import com.hummer.common.exceptions.SysException;
import com.hummer.core.PropertiesContainer;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.alibaba.druid.util.JdbcConstants.MYSQL_DRIVER;

/**
 * this class wrapper druid builder data source feature.
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/26 17:35
 **/
public class DruidDataSourceBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DruidDataSource.class);

    private DruidDataSourceBuilder() {

    }

    /**
     * builder druid data source instance .see also <a href='https://github.com/alibaba/druid'>https://github.com/alibaba/druid</a>
     *
     * @param ds druid data source configuration map
     * @return {@link com.alibaba.druid.pool.DruidDataSource}
     * @author liguo
     * @date 2019/6/26 17:36
     * @version 1.0.0
     **/
    public static DruidDataSource buildDataSource(Map<String, Object> ds) {
        long start = System.currentTimeMillis();
        try {
            DruidDataSource druidDataSource = new DruidDataSource();
            String driverClassName = (String) ds.get("driverClassName");
            druidDataSource.setDriverClassName(driverClassName);
            druidDataSource.setUrl((String) ds.get("url"));
            druidDataSource.setUsername((String) ds.get("username"));
            druidDataSource.setPassword((String) ds.get("password"));

            String initialSize = (String) ds.get("initialSize");
            if (initialSize != null) {
                druidDataSource.setInitialSize(Integer.parseInt(initialSize));
            }

            String connectionProperties = (String) ds.get("connectionProperties");
            if (connectionProperties != null) {
                druidDataSource.setConnectionProperties(connectionProperties);
            }

            String maxActive = (String) ds.get("maxActive");
            if (maxActive != null) {
                druidDataSource.setMaxActive(Integer.parseInt(maxActive));
            }

            String minIdle = (String) ds.get("minIdle");
            if (minIdle != null) {
                druidDataSource.setMinIdle(Integer.parseInt(minIdle));
            }

            String maxWait = (String) ds.get("maxWait");
            if (maxWait != null) {
                druidDataSource.setMaxWait(Long.parseLong(maxWait));
            }

            String timeBetweenEvictionRunsMillis = (String) ds.get("timeBetweenEvictionRunsMillis");
            if (timeBetweenEvictionRunsMillis != null) {
                druidDataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong(timeBetweenEvictionRunsMillis));
            }

            String connectionInitSqls = (String) ds.get("connectionInitSqls");
            if (connectionInitSqls != null) {
                List<String> initSqls = Splitter.on(";").trimResults().omitEmptyStrings().splitToList(connectionInitSqls);
                druidDataSource.setConnectionInitSqls(initSqls);
            }

            String minEvictableIdleTimeMillis = (String) ds.get("minEvictableIdleTimeMillis");
            if (minEvictableIdleTimeMillis != null) {
                druidDataSource.setMinEvictableIdleTimeMillis(Long.parseLong(minEvictableIdleTimeMillis));
            }

            String maxEvictableIdleTimeMillis = (String) ds.get("maxEvictableIdleTimeMillis");
            if (maxEvictableIdleTimeMillis != null) {
                druidDataSource.setMaxEvictableIdleTimeMillis(Long.parseLong(maxEvictableIdleTimeMillis));
            }
            //
            String testWhileIdleCfg = (String) ds.get("testWhileIdle");
            Boolean testWhileIdleVal = Strings.isNullOrEmpty(testWhileIdleCfg)
                    ? PropertiesContainer.valueOf(SysConstant.DaoConstant.JDBC_TESTWHILEIDLE
                    , Boolean.class, Boolean.TRUE)
                    : BooleanUtils.toBoolean(testWhileIdleCfg);
            druidDataSource.setTestWhileIdle(testWhileIdleVal);
            //
            String testOnBorrowCfg = (String) ds.get("testOnBorrow");
            Boolean testOnBorrowVal = Strings.isNullOrEmpty(testOnBorrowCfg)
                    ? PropertiesContainer.valueOf(SysConstant.DaoConstant.JDBC_TESTONBORROW
                    , Boolean.class, Boolean.TRUE)
                    : BooleanUtils.toBoolean(testOnBorrowCfg);
            druidDataSource.setTestOnBorrow(testOnBorrowVal);
            //
            String testOnReturnCfg = (String) ds.get("testOnReturn");
            Boolean testOnReturnVal = Strings.isNullOrEmpty(testOnReturnCfg)
                    ? PropertiesContainer.valueOf(SysConstant.DaoConstant.JDBC_TESTONRETURN
                    , Boolean.class, Boolean.FALSE)
                    : BooleanUtils.toBoolean(testOnReturnCfg);
            druidDataSource.setTestOnReturn(testOnReturnVal);

            if (!MYSQL_DRIVER.equals(driverClassName)) {
                druidDataSource.setPoolPreparedStatements(true);
                String maxPoolPreparedStatementPerConnectionSize =
                        (String) ds.get("maxPoolPreparedStatementPerConnectionSize");
                if (maxPoolPreparedStatementPerConnectionSize != null) {
                    druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(
                            Integer.parseInt(maxPoolPreparedStatementPerConnectionSize));
                }
            }
            String queryTimeOutVal = (String) ds.get("queryTimeout");
            if (!Strings.isNullOrEmpty(queryTimeOutVal)) {
                druidDataSource.setQueryTimeout(Integer.parseInt(queryTimeOutVal));
            }
            druidDataSource.setValidationQuery("select 1");

            //validationQueryTimeout
            String validationQueryTimeoutCfg = (String) ds.get("validationQueryTimeout");
            Integer validationQueryTimeoutVal = Strings.isNullOrEmpty(validationQueryTimeoutCfg)
                    ? PropertiesContainer.valueOf(SysConstant.DaoConstant.JDBC_VALIDATIONQUERYTIMEOUT, Integer.class
                    , 5)
                    : Integer.parseInt(validationQueryTimeoutCfg);
            druidDataSource.setValidationQueryTimeout(validationQueryTimeoutVal);

            //timeout remove connections
            String removeAbandonedCfg = (String) ds.get("removeAbandoned");
            Boolean removeAbandonedVal = Strings.isNullOrEmpty(removeAbandonedCfg)
                    ? PropertiesContainer.valueOf(SysConstant.DaoConstant.JDBC_REMOVEABANDONED, Boolean.class
                    , Boolean.TRUE)
                    : BooleanUtils.toBoolean(removeAbandonedCfg);
            druidDataSource.setRemoveAbandoned(removeAbandonedVal);

            //time out 30s
            String removeAbandonedTimeoutCfg = (String) ds.get("removeAbandonedTimeout");
            Integer removeAbandonedTimeoutVal = Strings.isNullOrEmpty(removeAbandonedTimeoutCfg)
                    ? PropertiesContainer.valueOf(SysConstant.DaoConstant.JDBC_REMOVEABANDONEDTIMEOUT
                    , Integer.class, 30)
                    : Integer.parseInt(removeAbandonedTimeoutCfg);
            druidDataSource.setRemoveAbandonedTimeout(removeAbandonedTimeoutVal);

            //close connection output error logs
            String logAbandonedCfg = (String) ds.get("logAbandoned");
            Boolean logAbandonedVal = Strings.isNullOrEmpty(logAbandonedCfg)
                    ? PropertiesContainer.valueOf(SysConstant.DaoConstant.JDBC_LOGABANDONED
                    , Boolean.class, Boolean.TRUE)
                    : BooleanUtils.toBoolean(logAbandonedCfg);
            druidDataSource.setLogAbandoned(logAbandonedVal);

            List<Filter> filters = Lists.newArrayList(statFilter(ds));

            String sqlCheckCfg = (String) ds.get("sqlCheck");
            Boolean sqlCheckVal = Strings.isNullOrEmpty(sqlCheckCfg)
                    ? PropertiesContainer.valueOf(SysConstant.DaoConstant.JDBC_CHECK_SQL
                    , Boolean.class, Boolean.TRUE)
                    : BooleanUtils.toBoolean(sqlCheckCfg);
            if (sqlCheckVal) {
                filters.add(wallFilter(ds));
            }
            druidDataSource.setProxyFilters(filters);
            LOGGER.info("builder data source done cost {} ms "
                    , System.currentTimeMillis() - start);
            return druidDataSource;
        } catch (Exception ex) {
            LOGGER.info("builder data source exception cost {} ms ", System.currentTimeMillis() - start
                    , ex);
            throw new SysException(ErrorCodeConsts.DAO_LOAD_FAIL, ex.getMessage(), ex);
        }
    }

    private static WallFilter wallFilter(Map<String, Object> ds) {
        WallFilter wallFilter = new WallFilter();
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);
        wallConfig.setSelectWhereAlwayTrueCheck(false);
        wallConfig.setSelectHavingAlwayTrueCheck(false);

        String selectUnionCheckCfg = (String) ds.get("selectUnionCheck");
        Boolean selectUnionCheckVal = Strings.isNullOrEmpty(selectUnionCheckCfg)
                ? PropertiesContainer.valueOf(SysConstant.DaoConstant.DRUID_SELECT_UNION_CHECK
                , Boolean.class, Boolean.TRUE)
                : BooleanUtils.toBoolean(selectUnionCheckCfg);
        wallConfig.setSelectUnionCheck(selectUnionCheckVal);

        wallFilter.setConfig(wallConfig);
        return wallFilter;
    }

    private static StatFilter statFilter(Map<String, Object> ds) {
        StatFilter statFilter = new StatFilter();

        String slowSqlCfg = (String) ds.get("slowSqlMillis");
        Long slowSqlVal = Strings.isNullOrEmpty(slowSqlCfg)
                ? PropertiesContainer.valueOf(SysConstant.DaoConstant.DRUID_SHOW_TIMEOUT
                , Long.class, 3000L)
                : Long.parseLong(slowSqlCfg);
        statFilter.setSlowSqlMillis(slowSqlVal);
        //
        String showSlowSqlCfg = (String) ds.get("showSlowSql");
        Boolean showSlowSqlVal = Strings.isNullOrEmpty(showSlowSqlCfg)
                ? PropertiesContainer.valueOf(SysConstant.DaoConstant.DRUID_SHOW_SQL
                , Boolean.class, Boolean.TRUE)
                : BooleanUtils.toBoolean(showSlowSqlCfg);
        statFilter.setLogSlowSql(showSlowSqlVal);
        //
        String mergeSqlCfg = (String) ds.get("mergeSql");
        Boolean mergeSqlVal = Strings.isNullOrEmpty(mergeSqlCfg)
                ? PropertiesContainer.valueOf(SysConstant.DaoConstant.DRUID_MERGE_SQL
                , Boolean.class, Boolean.FALSE)
                : BooleanUtils.toBoolean(mergeSqlCfg);
        statFilter.setMergeSql(mergeSqlVal);
        //
        return statFilter;
    }
}
