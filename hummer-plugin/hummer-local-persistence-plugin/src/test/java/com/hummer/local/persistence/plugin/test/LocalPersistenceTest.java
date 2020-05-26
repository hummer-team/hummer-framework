package com.hummer.local.persistence.plugin.test;


import com.hummer.local.persistence.plugin.RocksDBLocalPersistence;
import com.hummer.local.persistence.plugin.RocksDbPersistence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author bingy
 */
public class LocalPersistenceTest {
    private RocksDBLocalPersistence persistence;

    @Before
    public void init() {
        persistence = new RocksDbPersistence();
    }

    @Test
    public void putAndGet() {
        persistence.put("a", "bbb".getBytes());
        String val = new String(persistence.getByKey("a"));
        Assert.assertEquals("bbb", val);
    }
}
