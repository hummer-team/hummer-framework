package com.hummer.dao.config;

import com.hummer.dao.condition.DaoLoadCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

/**
 * this class is data source benan enter
 *
 * @author bingy
 */
@Import(value = {MultipleDataSourceConfiguration.class, DruidFilterConfiguration.class})
@DependsOn(value = {"springApplicationContext"})
@Conditional(DaoLoadCondition.class)
public class ExportDaoBean {

}
