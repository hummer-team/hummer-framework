package com.hummer.dao.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.Interners;
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
import com.hummer.common.SysConstant;
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
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hummer.common.SysConstant.DaoConstant.MYBATIS_DAO_INTERFACE_PACKAGE;
import static com.hummer.common.SysConstant.DaoConstant.MYBATIS_PO_MODEL_PACKAGE;

/**
 * multiple DataSource init.
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/25 17:38
 **/
public class MultipleDataSourceConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultipleDataSourceConfiguration.class);

    private final Map<Object, Object> allDataSources = Maps.newConcurrentMap();
    private DataSource defaultTargetDataSource;

    @Bean
    @Conditional(DaoLoadCondition.class)
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(){
        return new NamedParameterJdbcTemplate(defaultTargetDataSource) ;
    }

    /**
     * this {@link MapperScannerConfigurer} scan package {@link DaoConstant#MYBATIS_BASE_PACKAGE} include all interface,
     * after register dynamic proxy
     *
     * @return {@link org.mybatis.spring.mapper.MapperScannerConfigurer}
     * @author liguo
     * @date 2019/7/18 11:19
     * @since 1.0.0
     **/
    @Bean
    @Conditional(DaoLoadCondition.class)
    public MapperScannerConfigurer mapperScannerConfigurer() {
        String basePackage = PropertiesContainer.valueOfString(SysConstant.DaoConstant.MYBATIS_BASE_PACKAGE);
        Preconditions.checkNotNull(basePackage, "mybatis base package no settings,can not load dao");

        initDataSource();

        //@see MapperFactoryBean
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        //set session template bean name
        configurer.setSqlSessionTemplateBeanName(SysConstant.DaoConstant.SQL_SESSION_TEMPLATE_NAME);
        //only scan DaoAnnotation
        configurer.setAnnotationClass(DaoAnnotation.class);
        //register all doa package include interface
        configurer.setBasePackage(basePackage);
        LOGGER.info("dao mapper scanner register done,mybatis base package name {}", basePackage);
        return configurer;
    }


    private void initDataSource() {
        //scan all jdbc. prefix data source
        Map<String, Object> dbMap = PropertiesContainer.scanKeys(SysConstant.DaoConstant.DB_PREFIX);
        //group by data source
        Map<String, Map<String, Object>> dataSourceGroup = groupDataSource(dbMap);
        LOGGER.info("need initDataSource data source size {}", dataSourceGroup.size());
        if (MapUtils.isEmpty(dataSourceGroup)) {
            throw new SysException(50000, "no data source");
        }

        //store session factory
        Map<String, SqlSessionFactory> sqlSessionFactoryMap = new LinkedHashMap<>();
        String defaultDataSourceKey = PropertiesContainer.valueOfString("default.data.source");
        //foreach load target data source
        for (Map.Entry<String, Map<String, Object>> entry : dataSourceGroup.entrySet()) {
            long start = System.currentTimeMillis();
            String keyPrefix = entry.getKey();
            //builder druid pool instance
            try {
                DruidDataSource dataSource = DruidDataSourceBuilder.buildDataSource(entry.getValue());
                //register jdbc transaction bean
                MybatisDynamicBean.registerTransaction(newKey(keyPrefix, "_TM")
                        , dataSource);
                //register jdbc sql session factory
                MybatisDynamicBean.registerSqlSessionFactory(keyPrefix
                        , newKey(keyPrefix, "sqlSessionFactory")
                        , dataSource
                );
                //get already register session factory instance
                SqlSessionFactory sessionFactory =
                        (SqlSessionFactory) SpringApplicationContext.getBean(newKey(keyPrefix
                                , "sqlSessionFactory"));
                sqlSessionFactoryMap.put(keyPrefix,
                        sessionFactory);
                //
                allDataSources.put(keyPrefix, dataSource);
                //check is default data source key.
                if (keyPrefix.equalsIgnoreCase(defaultDataSourceKey)
                        && defaultTargetDataSource == null) {
                    defaultTargetDataSource = dataSource;
                }
                //
                //MybatisDynamicBean.registerSqlSessionTemplate(newKey(keyPrefix, "sessionTemplate"), sessionFactory);
                LOGGER.info("data source `{}` initDataSource done,cost {} ms"
                        , keyPrefix
                        , System.currentTimeMillis() - start);
            } catch (Throwable throwable) {
                LOGGER.error("data source `{}` initDataSource failed break flow" +
                        ",throwable", entry, throwable);
                throw new SysException(50000, "data source initDataSource failed");
            }
        }

        MybatisDynamicBean.registerSqlSessionTemplate(sqlSessionFactoryMap);
        //MybatisDynamicBean.registerSqlSessionTemplate(SysConstant.DaoConstant.SQL_SESSION_TEMPLATE_NAME
        //        , sqlSessionFactoryMap.entrySet().iterator().next().getValue());
        //ensure exists default data source.
        if (defaultTargetDataSource == null) {
            defaultTargetDataSource = (DataSource) Iterables.get(allDataSources.values(), 0);
            LOGGER.warn("no specific default dataSource,use first data {} as default data source"
                    , defaultTargetDataSource);
        }
        //MybatisDynamicBean.registerJdbcTemplate(SysConstant.DaoConstant.SQL_SESSION_TEMPLATE_NAME
        // , defaultTargetDataSource);
        //register default sql session template,will re factory
        //cache data source
        MultipleDataSourceMap.cacheDataSourceAll(sqlSessionFactoryMap.keySet());
        MultipleDataSourceMap.setDataSource(Iterables.get(sqlSessionFactoryMap.keySet(), 0));
        //dynamicDataSource = new DynamicDataSource(allDataSources, defaultTargetDataSource);
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
     * @return {@link java.util.Collection<java.util.Map<java.lang.String,java.lang.Object>>}
     * @author liguo
     * @date 2019/6/26 18:34
     * @version 1.0.0
     **/
    private Map<String, Map<String, Object>> groupDataSource(Map<String, Object> allMap) {
        Collection<String> dataSourceNamePrefix = Collections2.transform(allMap.keySet(), k -> {
            Iterable<String> keyArray = Splitter.on(".").omitEmptyStrings().split(k);
            return String.format("%s.%s.%s"
                    , Iterables.get(keyArray, 0)
                    , Iterables.get(keyArray, 1)
                    , Iterables.get(keyArray, 2));
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
            newMaps.put(k.replaceAll("spring.jdbc.", "")
                    , tempMap);
        });

        return newMaps;
    }

    private String newKey(String k, Map.Entry<String, Object> key) {
        return key.getKey().replaceAll(String.format("%s.", k), "");
    }

    private String newKey(String prefixKey, String suffix) {
        return String.format("%s%s", prefixKey, suffix);
    }
}
