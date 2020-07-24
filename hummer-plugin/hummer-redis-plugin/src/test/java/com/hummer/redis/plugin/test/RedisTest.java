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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    public void hset() throws IOException {
        redisOp.hash().hset("hummerHash1", "k1", "sssssssssssss");
        Assert.assertEquals("sssssssssssss"
                , redisOp.hash().getByFieldKey("hummerHash1", "k1"));
        //System.in.read();
    }

    @Test
    public void set() throws IOException {
        Map<String, String> kv = new HashMap<>();
        kv.put("setA1", "test");
        kv.put("setA2", "test2");
        redisOp.set().setMultipleStringByPipeline(kv, 0, Boolean.FALSE);
        //System.in.read();
    }

    @Test
    public void get() {
        System.out.println("_________________________");
        System.out.println(redisOp.set().getKey("LOCK_A"));
    }


    @Test
    public void lock() {
        Assert.assertEquals(Boolean.TRUE, redisOp.lock().lock("LOCK_A", 120));
        Assert.assertEquals(Boolean.TRUE, redisOp.lock().freeLock("LOCK_A"));
    }

    @Test
    public void multiOp() {
        redisOp.multiOp().setAndHsetByPipeline("TTTT", "OOOOOOOO"
                , "THash", 60);

        String v1 = redisOp.set().getKey("TTTT");
        Assert.assertEquals("OOOOOOOO", v1);

        v1 = redisOp.hash().getByFieldKey("THash", "TTTT");
        Assert.assertEquals("NL", v1);

        Map<String, String> map = redisOp.hash().getAll("THash");
        System.out.println(map);

        redisOp.set().del(map.keySet().toArray(new String[]{}));

        v1 = redisOp.set().getKey("TTTT");
        Assert.assertEquals(null, v1);
    }
}
