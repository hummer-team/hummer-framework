package com.hummer.dao.aspect;


import com.hummer.dao.annotation.TargetDataSource;
import com.hummer.dao.mybatis.context.DataSourceSwitch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * switch data source
 */
@Aspect
@Component
@Slf4j
public class TargetDataSourceAspect {

    @Before(" @annotation(ds)")
    public void changeDataSource(JoinPoint point, TargetDataSource ds) {
        if (!DataSourceSwitch.exists(ds.value())) {
            log.error("data source {} no exists，use default data source > {}", ds.value(), point.getSignature());
        } else {
            log.debug("use dataSource : {} >>>> {}", ds.value(), point.getSignature());
            DataSourceSwitch.set(ds.value());
        }
    }

    @After(" @annotation(ds)")
    public void restoreDataSource(JoinPoint point, TargetDataSource ds) {
        log.debug("revert dataSource : {} >>>> {}", ds.value(), point.getSignature());
        DataSourceSwitch.clean();
    }

}