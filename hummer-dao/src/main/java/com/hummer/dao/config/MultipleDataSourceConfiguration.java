package com.hummer.dao.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hummer.common.exceptions.SysException;
import com.hummer.dao.annotation.DaoAnnotation;
import com.hummer.dao.condition.DaoLoadCondition;
import com.hummer.dao.druiddatasource.DruidDataSourceBuilder;
import com.hummer.dao.mybatis.MybatisDynamicBean;
import com.hummer.dao.mybatis.context.MultipleDataSourceMap;
import com.hummer.dao.mybatis.route.DynamicDataSource;
import com.hummer.spring.plugin.context.PropertiesContainer;
import com.hummer.common.SysConsts;
import com.hummer.spring.plugin.context.SpringApplicationContext;
import lombok.Getter;
import org.apache.commons.collections.MapUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * multiple DataSource init.
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/25 17:38
 **/
public class MultipleDataSourceConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultipleDataSourceConfiguration.class);
    @Getter
    private Map<Object, Object> targetDataSources = new LinkedHashMap<>();
    @Getter
    private DataSource defaultTargetDataSource;

    @Bean
    @Lazy
    @ConditionalOnMissingBean
    @Conditional(DaoLoadCondition.class)
    public DataSource dynamicDataSource() {
        DynamicDataSource ds = new DynamicDataSource();
        ds.setTargetDataSources(targetDataSources);
        ds.setDefaultTargetDataSource(defaultTargetDataSource);
        return ds;
    }

    @Bean
    @Conditional(DaoLoadCondition.class)
    public MapperScannerConfigurer mapperScannerConfigurer() {
        String basePackage = PropertiesContainer.valueOfString(SysConsts.DaoConsts.MYBATIS_BASE_PACKAGE);
        Preconditions.checkNotNull(basePackage, "mybatis base package no settings,can not load dao");

        initDataSource();

        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setSqlSessionTemplateBeanName(SysConsts.DaoConsts.SQL_SESSION_TEMPLATE_NAME);
        configurer.setAnnotationClass(DaoAnnotation.class);
        configurer.setBasePackage(PropertiesContainer.valueOfString(SysConsts.DaoConsts.MYBATIS_BASE_PACKAGE));
        LOGGER.info("dao mapper scanner register done,mybatis base package name {}", basePackage);
        return configurer;
    }


    private void initDataSource() {
        Map<String, SqlSessionFactory> sqlSessionFactoryMap = new LinkedHashMap<>();
        //scan all jdbc. prefix data source
        Map<String, Object> dbMap = PropertiesContainer.scanKeys(SysConsts.DaoConsts.DB_PREFIX);
        //group by data source
        Map<String, Map<String, Object>> dataSourceGroup = groupDataSource(dbMap);
        LOGGER.info("need initDataSource data source size {}", dataSourceGroup.size());
        if (MapUtils.isEmpty(dataSourceGroup)) {
            return;
        }
        //foreach load target data source
        boolean defaultDataSource = true;
        for (Map.Entry<String, Map<String, Object>> entry : dataSourceGroup.entrySet()) {
            long start = System.currentTimeMillis();
            String keyPrefix = entry.getKey();
            //builder druid pool instance
            try  {
                DruidDataSource dataSource = DruidDataSourceBuilder.buildDataSource(entry.getValue());
                //initDataSource
                dataSource.init();
                //register jdbc transaction bean
                MybatisDynamicBean.registerTransaction(newKey(keyPrefix,"tx")
                        , dataSource);
                //register jdbc sql session factory
                MybatisDynamicBean.registerSqlSessionFactory(newKey(keyPrefix,"sqlSessionFactory")
                        , dataSource);
                sqlSessionFactoryMap.put(keyPrefix,
                        (SqlSessionFactory) SpringApplicationContext.getBean(newKey(keyPrefix
                                ,"sqlSessionFactory")));
                targetDataSources.put(keyPrefix, dataSource);
                if (defaultDataSource) {
                    defaultTargetDataSource = dataSource;
                }
                defaultDataSource = false;
                //cache data source
                MultipleDataSourceMap.cacheDataSource(keyPrefix);
                LOGGER.info("data source `{}` initDataSource done,cost {} ms"
                        , keyPrefix
                        , System.currentTimeMillis() - start);
            } catch (Throwable throwable) {
                LOGGER.error("data source `{}` initDataSource failed break flow" +
                        ",throwable", entry, throwable);
                throw new SysException(50000, "data source initDataSource failed");
            }
        }
        //register default sql session template,will re factory
        MybatisDynamicBean.registerSqlSessionTemplate(sqlSessionFactoryMap);
    }

    /**
     * all data source configuration group by prefix. ie:
     * <ul>
     * <li>
     * jdbc.A.conn ;
     * jdbc.A.password
     * </li>
     * <li>
     * jdbc.B.conn ;
     * jdbc.B.password
     * </li>
     * <li>
     * jdbc.C.conn ;
     * jdbc.C.password
     * </li>
     * </ul>
     * =[jdbc.A,jdbc.B,jdbc.C]
     *
     * @param allMap all data source configuration
     * @return <code> java.util.Collection<java.util.Map<java.lang.String,java.lang.Object>></code>
     * @author liguo
     * @date 2019/6/26 18:34
     * @version 1.0.0
     **/
    private Map<String, Map<String, Object>> groupDataSource(Map<String, Object> allMap) {
        Collection<String> dataSourceNamePrefix = Collections2.transform(allMap.keySet(), k -> {
            Iterable<String> keyArray = Splitter.on(".").omitEmptyStrings().split(k);
            return String.format("%s.%s"
                    , Iterables.get(keyArray, 0)
                    , Iterables.get(keyArray, 1));
        });

        Set<String> distinctKey = Sets.newHashSet(dataSourceNamePrefix);

        Map<String, Map<String, Object>> newMaps = Maps.newHashMapWithExpectedSize(distinctKey.size());
        distinctKey.forEach(k -> {
            Map<String, Object> tempMap = allMap.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().startsWith(k))
                    .collect(Collectors
                            .toMap(key -> newKey(k, key)
                                    , Map.Entry::getValue));
            newMaps.put(k.replaceAll("jdbc.", "")
                    , tempMap);
        });

        return newMaps;
    }

    private String newKey(String k, Map.Entry<String, Object> key) {
        return key.getKey().replaceAll(String.format("%s.", k), "");
    }

    private String newKey(String prefixKey,String suffix){
        return String.format("%s%s",prefixKey,suffix);
    }
}
