package com.hummer.dao.mybatis;

import com.github.pagehelper.PageInterceptor;
import com.hummer.common.SysConstant;
import com.hummer.dao.interceptor.DbTypeInterceptor;
import com.hummer.dao.interceptor.MybatisSlowSqlLogInterceptor;
import com.hummer.spring.plugin.context.PropertiesContainer;
import com.hummer.spring.plugin.context.SpringApplicationContext;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.testng.collections.Lists;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * this class impl mybatis dynamic ben sql session,transaction,sql session template factory.
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/26 18:48
 **/
public class MybatisDynamicBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisDynamicBean.class);

    private MybatisDynamicBean() {

    }

    /**
     * register sql session factory.
     * {@see http://www.mybatis.org/mybatis-3/zh/configuration.html}
     *
     * @param dataSourceKey  properties data source key name
     * @param sqlSessionName mybatis sql session name
     * @param dataSource     data source
     * @throws {@link IOException} if load resource failed then throw this exception
     * @link
     * @since 1.0.0
     */
    public static void registerSqlSessionFactory(final String dataSourceKey
            , final String sqlSessionName
            , final DataSource dataSource) throws IOException {
        //get ben sql session
        BeanDefinitionBuilder sqlSessionBean =
                BeanDefinitionBuilder.rootBeanDefinition(SqlSessionFactoryBean.class);
        //settings target  data source
        sqlSessionBean.addPropertyValue("dataSource", dataSource);
        //settings initial method,method getObject will return default session factory instance
        sqlSessionBean.setInitMethodName("getObject");
        //settings alias po package path
        //notice： Not recommended use feature
        String poAliasPackage = PropertiesContainer.valueOfString("po.alias.package");
        if (StringUtils.isNoneEmpty(poAliasPackage)) {
            sqlSessionBean.addPropertyValue("typeAliasesPackage"
                    , poAliasPackage);
        }
        //settings session configuration
        //todo
        //settings type handle package
        sqlSessionBean.addPropertyValue("typeHandlersPackage",
                PropertiesContainer.valueOfString(
                        String.format(SysConstant.DaoConstant.MYBATIS_PO_MODEL_PACKAGE, dataSourceKey)));
        //settings resource path parse service
        VFS.addImplClass(CustomVFS.class);

        //slow sql interceptor
        MybatisSlowSqlLogInterceptor mybatisSlowSqlLogInterceptor = MybatisSlowSqlLogInterceptor.instance();
        Properties slowSqlLogProperties = builderSqlProperties();
        mybatisSlowSqlLogInterceptor.setProperties(slowSqlLogProperties);

        //page help interceptor
        //@see https://github.com/pagehelper/Mybatis-PageHelper
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties pageProperties = builderSqlProperties();
        pageProperties.setProperty("maxLimitEnable"
                , PropertiesContainer.valueOfString("maxLimitEnable", "true"));
        pageProperties.setProperty("max.limit", PropertiesContainer.valueOfString("max.limit", "500"));
        pageProperties.setProperty("offsetAsPageNum",
                PropertiesContainer.valueOfString("mybatis.page.offsetAsPageNum"
                        , "true"));
        pageProperties.setProperty("rowBoundsWithCount",
                PropertiesContainer.valueOfString("mybatis.page.rowBoundsWithCount"
                        , "true"));
        pageProperties.setProperty("pageSizeZero"
                , PropertiesContainer.valueOfString("mybatis.page.pageSizeZero", "true"));
        pageProperties.setProperty("reasonable"
                , PropertiesContainer.valueOfString("mybatis.page.rowBoundsWithCount", "false"));
        pageProperties.setProperty("params"
                , PropertiesContainer.valueOfString("mybatis.page.pageSizeZero",
                        "pageNum=pageHelperStart;pageSize=pageHelperRows;"));
        pageProperties.setProperty("supportMethodsArguments",
                PropertiesContainer.valueOfString("mybatis.page.reasonable"
                        , "false"));
        pageProperties.setProperty("returnPageInfo"
                , PropertiesContainer.valueOfString("mybatis.page.params", "none"));
        pageInterceptor.setProperties(pageProperties);
        //
        //loading business customer interceptor plugin
        Map<String, Interceptor> customerInterceptorMap = SpringApplicationContext.getBeans(Interceptor.class);
        List<Interceptor> interceptors = Lists.newArrayList(16);
        if (MapUtils.isNotEmpty(customerInterceptorMap)) {
            interceptors.addAll(customerInterceptorMap.values());
        }
        interceptors.add(mybatisSlowSqlLogInterceptor);
        interceptors.add(pageInterceptor);
        interceptors.add(DbTypeInterceptor.instance());
        //add all interceptor
        sqlSessionBean.addPropertyValue("plugins", interceptors);

        //mapper resource
        PathMatchingResourcePatternResolver resource = new PathMatchingResourcePatternResolver();
        sqlSessionBean.addPropertyValue("mapperLocations"
                , resource.getResources(PropertiesContainer
                        .valueOfString(String.format(SysConstant.DaoConstant.MYBATIS_RESOURCE_MAPPER_PATH
                                , dataSourceKey))));
        //register sql session bean
        SpringApplicationContext.registerDynamicBen(sqlSessionName, sqlSessionBean.getRawBeanDefinition());
        //scannerConfigurer.setSqlSessionFactoryBeanName(sqlSessionName);
        LOGGER.info("bean `{}` register done.", sqlSessionName);
    }

    /**
     * register data source transaction
     *
     * @param transactionName transaction name
     * @param source          data source
     */
    public static void registerTransaction(String transactionName, DataSource source) {
        //create new data source transaction bean
        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
        beanDefinitionBuilder.addPropertyValue("dataSource", source);
        SpringApplicationContext.registerDynamicBen(transactionName, beanDefinitionBuilder.getRawBeanDefinition());
        LOGGER.info("bean `{}` data transaction register done.", transactionName);
    }

    /**
     * register sql session template
     *
     * @param sqlSessionTemplateMap sql session
     */
    @Deprecated
    public static void registerSqlSessionTemplate(Map<String, SqlSessionFactory> sqlSessionTemplateMap) {
        //new custom sql session template ben
        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder.genericBeanDefinition(CustomSqlSessionTemplate.class);
        //call `CustomSqlSessionTemplate` constructor args,first session instance as default session instance.
        beanDefinitionBuilder.addConstructorArgValue(sqlSessionTemplateMap.entrySet().iterator().next().getValue());
        beanDefinitionBuilder.addPropertyValue("targetSqlSessionFactoryMap", sqlSessionTemplateMap);
        beanDefinitionBuilder.setLazyInit(true);

        SpringApplicationContext.registerDynamicBen(SysConstant.DaoConstant.SQL_SESSION_TEMPLATE_NAME
                , beanDefinitionBuilder.getRawBeanDefinition());
        LOGGER.info("bean custom sql session template register done,sql session template map {}"
                , sqlSessionTemplateMap);
    }

    public static void registerSqlSessionTemplate(final String templateName, final SqlSessionFactory dataSource){
        //new custom sql session template ben
        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder.rootBeanDefinition(SqlSessionTemplate.class);
        beanDefinitionBuilder.addConstructorArgValue(dataSource);
        SpringApplicationContext.registerDynamicBen(SysConstant.DaoConstant.SQL_SESSION_TEMPLATE_NAME
                , beanDefinitionBuilder.getRawBeanDefinition());
    }

    public static void registerJdbcTemplate(final String templateName, final DataSource dataSource){
        //new custom sql session template ben
        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder.rootBeanDefinition(JdbcTemplate.class);
        beanDefinitionBuilder.addConstructorArgValue(dataSource);
        SpringApplicationContext.registerDynamicBen(templateName
                , beanDefinitionBuilder.getRawBeanDefinition());
    }

    private static Properties builderSqlProperties() {
        String sqlSlowTimeStringValue = PropertiesContainer.valueOfString("mybatis.sql.slow.time.millisecond"
                , "3000");
        String showSqlStringValue = PropertiesContainer.valueOfString("mybatis.sql.slow.show", "true");

        Properties slowSqlLogProperties = new Properties();
        slowSqlLogProperties.setProperty("sql.timeout"
                , sqlSlowTimeStringValue);
        slowSqlLogProperties.setProperty("show.sql"
                , showSqlStringValue);
        return slowSqlLogProperties;
    }
}
