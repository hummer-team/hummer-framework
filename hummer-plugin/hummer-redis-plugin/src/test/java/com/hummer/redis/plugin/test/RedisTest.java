package com.hummer.redis.plugin.test;

import com.hummer.core.config.PropertiesConfig;
import com.hummer.core.starter.BootStarterBean;
import com.hummer.redis.plugin.RedisOp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/10/31 17:56
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
@Import(value = {PropertiesConfig.class, BootStarterBean.class, RedisOp.class})
@PropertySource(value = {"classpath:application.properties"})
public class RedisTest {
    @Autowired
    private RedisOp redisOp;

    @Test
    public void hset(){
        redisOp.hash().hset("hummerHash1","k1","sssssssssssss");
        Assert.assertEquals("sssssssssssss"
        ,redisOp.hash().getByFieldKey("hummerHash1","k1"));
    }
}
