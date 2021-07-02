package com.hummer.local.persistence.plugin.test;


import com.hummer.local.persistence.plugin.LocalPersistence;
import com.hummer.local.persistence.plugin.MapDbPersistence;
import com.hummer.local.persistence.plugin.RocksDbPersistence;
import com.hummer.local.persistence.plugin.bean.MapLocalPersistence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author bingy
 */
public class LocalPersistenceTest {
    private LocalPersistence persistence;

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

    @Test
    public void mapDb() {
        MapLocalPersistence map = new MapDbPersistence();
        map.put("aa", "dd".getBytes());

        Assert.assertEquals("dd", new String(map.getByKey("aa")));
    }
}
