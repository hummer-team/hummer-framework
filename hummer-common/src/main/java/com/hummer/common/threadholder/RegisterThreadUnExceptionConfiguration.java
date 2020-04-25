package com.hummer.common.threadholder;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author edz
 */
@Configurable
public class RegisterThreadUnExceptionConfiguration implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        System.getProperties().setProperty("java.util.concurrent.ForkJoinPool.common.exceptionHandler"
        ,"com.hummer.common.threadholder.CustomUncaughtExceptionHandler");
    }
}
