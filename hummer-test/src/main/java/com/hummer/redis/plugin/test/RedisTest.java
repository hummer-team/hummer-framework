package com.hummer.redis.plugin.test;

import com.hummer.core.config.PropertiesConfig;
import com.hummer.core.init.HummerApplicationContextInit;
import com.hummer.core.starter.BootStarterBean;
import com.hummer.redis.plugin.RedisOp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConfigFileApplicationContextInitializer.class
        , HummerApplicationContextInit.class})
@Import(value = {PropertiesConfig.class, BootStarterBean.class, RedisOp.class})
@PropertySource(value = {"classpath:application.properties"})
public class RedisTest {

    @Autowired
    private RedisOp redisOp;

    @Test
    public void hset() throws IOException {

        String geoKey = "community_group_head_geo";
        Long result = redisOp.geo().add(geoKey, "b75c40a944f2497b93122b87c71c3e78", 121.323261
                , 31.209601);
        System.out.println(result);
    }
}
