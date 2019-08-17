package com.hummer.local.persistence.plugin;


import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author bingy
 */
public class LocalPersistenceTest {
    private RocksDBLocalPersistence persistence;

    @BeforeTest
    public void init() {
        persistence = new RocksDbPersistence();
    }

    @Test
    public void putAndGet() {
        persistence.put("a", "bbb".getBytes());
        String val = new String(persistence.getByKey("a"));
        Assert.assertEquals("bb", val);
    }
}
