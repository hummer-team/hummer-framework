package com.hummer.test;

import com.hummer.core.init.HummerApplicationContextInit;
import com.hummer.core.starter.BootStarterBean;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/4/28 11:14
 */

@RunWith(value = SpringRunner.class)
@Import(value = {BootStarterBean.class})
@ContextConfiguration(initializers = {ConfigFileApplicationContextInitializer.class,
        HummerApplicationContextInit.class})
@ComponentScan(basePackages = "com.hummer.test")
public class BaseTest {



}
