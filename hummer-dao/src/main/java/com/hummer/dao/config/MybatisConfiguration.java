package com.hummer.dao.config;

import com.hummer.spring.plugin.context.PropertiesContainer;
import com.hummer.spring.plugin.context.SpringApplicationContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/26 18:48
 **/
public class MybatisConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisConfiguration.class);

    private MybatisConfiguration() {

    }

    /**
     * register sql session factory
     *
     * @param name
     * @param dataSource
     * @link http://www.mybatis.org/mybatis-3/zh/configuration.html
     * @since 1.0.0
     */
    public static void registerDynamicSqlSessionFactory(String name, DataSource dataSource) {
        //get ben sql session
        BeanDefinitionBuilder rootBeanDefinition =
                BeanDefinitionBuilder.rootBeanDefinition(SqlSessionFactoryBean.class);
        //settings target  data source
        rootBeanDefinition.addPropertyValue("dataSource", dataSource);
        //settings initial method
        rootBeanDefinition.setInitMethodName("getObject");
        //settings alias po package path
        //notice： Not recommended use feature
        String poAliasPackage = PropertiesContainer.valueOfString("po.alias.package");
        if (StringUtils.isNoneEmpty(poAliasPackage)) {
            rootBeanDefinition.addPropertyValue("typeAliasesPackage"
                    , poAliasPackage);
        }
        //settings session configuration
        //todo
        //settings type handle package
        rootBeanDefinition.addPropertyValue("typeHandlersPackage",
                PropertiesContainer.valueOfString("mybatis.typeHandlersPackage"));
        //settings vfs
        //todo

        //loading business customer interceptor plugin
        Map<String, Interceptor> interceptor = SpringApplicationContext.getBeans(Interceptor.class);
    }
}
