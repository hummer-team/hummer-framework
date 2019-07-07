package com.hummer.dao.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.hummer.spring.plugin.context.PropertiesContainer;
import org.springframework.context.annotation.Bean;

public class DruidFilterConfiguration {

    @Bean
    public StatFilter statFilter() {
        StatFilter statFilter = new StatFilter();
        statFilter.setSlowSqlMillis(PropertiesContainer.valueOf("druid.sql.timeout", Long.class, 3000L));
        statFilter.setLogSlowSql(PropertiesContainer.valueOf("druid.show.sql", Boolean.class, true));
        statFilter.setMergeSql(PropertiesContainer.valueOf("druid.merge.sql", Boolean.class, false));
        return statFilter;
    }
}
