package com.hummer.dao.configuration;

import com.hummer.dao.aspect.TargetDataSourceAspect;
import com.hummer.dao.aspect.TargetDataSourceTMAspect;
import com.hummer.dao.condition.DaoLoadCondition;
import com.hummer.dao.monitor.DruidStatController;
import com.hummer.dao.monitor.stat.DruidConfiguration;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;

/**
 * this class is data source benan enter
 *
 * @author bingy
 */
@Import(value = {MultipleDataSourceConfiguration.class
        , DruidFilterConfiguration.class
        , TargetDataSourceAspect.class
        , TargetDataSourceTMAspect.class
        , DruidStatController.class
        , DruidConfiguration.class})
@Conditional(DaoLoadCondition.class)
public class ExportDaoConfiguration {

}
