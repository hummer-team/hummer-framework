package com.hummer.dao.configuration;

import com.alibaba.druid.filter.stat.StatFilter;
import com.hummer.core.PropertiesContainer;
import org.springframework.context.annotation.Bean;

public class DruidFilterConfiguration {

    @Bean
    public StatFilter statFilter() {
        StatFilter statFilter = new StatFilter();
        statFilter.setSlowSqlMillis(PropertiesContainer.valueOf("druid.sql.timeout", Long.class, 1000L));
        statFilter.setLogSlowSql(PropertiesContainer.valueOf("druid.show.sql", Boolean.class, true));
        statFilter.setMergeSql(PropertiesContainer.valueOf("druid.merge.sql", Boolean.class, false));
        return statFilter;
    }
}
