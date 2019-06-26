package com.hummer.dao.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.hummer.dao.annotation.DaoAnnotation;
import com.hummer.dao.condition.DaoLoadCondition;
import com.hummer.dao.datasource.DruidDataSourceBuilder;
import com.hummer.spring.plugin.context.PropertiesContainer;
import com.hummer.support.SysConsts;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * multiple DataSource settings
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/25 17:38
 **/
public class MultipleDataSourceConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultipleDataSourceConfiguration.class);

    @Bean
    @Conditional(DaoLoadCondition.class)
    public MapperScannerConfigurer mapperScannerConfigurer(ApplicationContext context) {

        String basePackage = PropertiesContainer.valueOfString(SysConsts.DaoConsts.MYBATIS_BASE_PACKAGE);
        Preconditions.checkNotNull(basePackage, "mybatis base package no settings,can not load dao");

        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setSqlSessionTemplateBeanName(SysConsts.DaoConsts.SQL_SESSION_TEMPLATE_NAME);
        configurer.setAnnotationClass(DaoAnnotation.class);
        configurer.setBasePackage(PropertiesContainer.valueOfString(SysConsts.DaoConsts.MYBATIS_BASE_PACKAGE));
        LOGGER.info("dao mapper scanner register done,mybatis base package name {}", basePackage);
        return configurer;
    }


    private void init() {

        //scan all jdbc. prefix data source
        Map<String, Object> dbMap = PropertiesContainer.scanKeys(SysConsts.DaoConsts.DB_PREFIX);
        LOGGER.info("target data source array item size {},details {}", dbMap.size(), dbMap);
        //group by
        Collection<Map<String, Object>> dataSourceGroup = groupDataSource(dbMap);
        //foreach load target data source
        for (Map<String, Object> entry : dataSourceGroup) {
            //builder druid pool instance
            DruidDataSource dataSource = DruidDataSourceBuilder.buildDataSource(entry);
            //register jdbc transaction bean

            //register jdbc sql session factory
        }
    }

    /**
     * all data source configuration group by prefix. ie:
     * <ul>
     *     <li>
     *         jdbc.A.conn ;
     *         jdbc.A.password
     *     </li>
     *     <li>
     *         jdbc.B.conn ;
     *         jdbc.B.password
     *     </li>
     *     <li>
     *       jdbc.C.conn ;
     *       jdbc.C.password
     *     </li>
     * </ul>
     * =[jdbc.A,jdbc.B,jdbc.C]
     * @param allMap all data source configuration
     * @return <code> java.util.Collection<java.util.Map<java.lang.String,java.lang.Object>></code>
     * @author liguo
     * @date 2019/6/26 18:34
     * @version 1.0.0
     **/
    private Collection<Map<String, Object>> groupDataSource(Map<String, Object> allMap) {
        Collection<String> dataSourceNamePrefix = Collections2.transform(allMap.keySet(), k -> {
            Iterable<String> keyArray = Splitter.on(".").omitEmptyStrings().split(k);
            String prefix = String.format("%s.%s", Iterables.get(keyArray, 0)
                    , Iterables.get(keyArray, 1));
            return prefix;
        });

        Collection<Map<String, Object>> groupList = Lists.newArrayListWithCapacity(dataSourceNamePrefix.size());
        dataSourceNamePrefix.forEach(k -> {
            Map<String, Object> tempMap = allMap.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().endsWith(k))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            groupList.add(tempMap);
        });

        return groupList;
    }
}
