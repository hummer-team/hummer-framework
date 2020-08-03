package com.hummer.doorgod.test;

import com.hummer.core.init.HummerApplicationContextInit;
import com.hummer.core.starter.BootStarterBean;
import com.hummer.dao.starter.ExportDaoInitBean;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(value = SpringRunner.class)
@Import(value = {BootStarterBean.class, ExportDaoInitBean.class})
@ContextConfiguration(initializers =
        {ConfigFileApplicationContextInitializer.class, HummerApplicationContextInit.class})
@ComponentScan(basePackages = "com.hummer.doorgod")
public class BaseTest {

}