package com.hummer.dao.configuration;

import com.hummer.dao.aspect.TargetDataSourceAspect;
import com.hummer.dao.condition.DaoLoadCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;

/**
 * this class is data source benan enter
 *
 * @author bingy
 */
@Import(value = {MultipleDataSourceConfiguration.class, DruidFilterConfiguration.class, TargetDataSourceAspect.class})
@Conditional(DaoLoadCondition.class)
public class ExportDaoConfiguration {

}
