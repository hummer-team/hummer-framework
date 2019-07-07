package com.hummer.dao.config;

import com.hummer.dao.condition.DaoLoadCondition;
import com.hummer.spring.plugin.context.CoreContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;

/**
 * this class is data source benan enter
 *
 * @author bingy
 */
@DependsOn(value = {"springApplicationContext"})
@Conditional(DaoLoadCondition.class)
@Import(value = {MultipleDataSourceConfiguration.class, DruidFilterConfiguration.class})
@Order(value = 10000)
public class ExportDaoBean {
    @Autowired
    private CoreContext coreContext;

    @PostConstruct()
    private void init(){

    }
}
