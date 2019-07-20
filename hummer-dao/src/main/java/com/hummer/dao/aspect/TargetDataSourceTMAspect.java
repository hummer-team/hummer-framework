package com.hummer.dao.aspect;


import com.hummer.dao.annotation.TargetDataSourceTM;
import com.hummer.dao.mybatis.context.DataSourceSwitch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * multiple data source transactional switch aspect
 *
 * @author bingy
 */
@Aspect
@Component
@Slf4j
public class TargetDataSourceTMAspect {

    @Before(" @annotation(ds)")
    public void changeDataSource(JoinPoint point, TargetDataSourceTM ds) {

        if (!DataSourceSwitch.exists(ds.dbName())) {
            log.error("data source {} no exists，use default data source > {}"
                    , ds.dbName(), point.getSignature());
        } else {
            log.debug("Use DataSource : {} > {}, tx name : {}"
                    , ds.dbName(), point.getSignature(), ds.dbName());
            DataSourceSwitch.set(ds.dbName());
        }
    }

    @After(" @annotation(ds)")
    public void restoreDataSource(JoinPoint point, TargetDataSourceTM ds) {
        log.debug("Revert DataSource : {} > {}", ds.dbName(), point.getSignature());
        DataSourceSwitch.clean();
    }
}
