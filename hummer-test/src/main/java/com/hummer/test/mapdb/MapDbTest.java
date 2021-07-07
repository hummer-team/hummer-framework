package com.hummer.test.mapdb;

import com.hummer.local.persistence.plugin.bean.MapLocalPersistence;
import com.hummer.test.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MapDbTest extends BaseTest {
    @Autowired
    private MapLocalPersistence mapLocalPersistence;

    @Test
    public void list() {
        mapLocalPersistence.removeOfList("l1");
        mapLocalPersistence.removeOfList("l2");

        mapLocalPersistence.addToList("l1", "l112345".getBytes());
        mapLocalPersistence.addToList("l1", "l112345".getBytes());

        mapLocalPersistence.addToList("l1", "l112345".getBytes());
        mapLocalPersistence.addToList("l1", "l112345".getBytes());

        mapLocalPersistence.addToList("l1", "l112345".getBytes());

        mapLocalPersistence.addToList("l2", "l212345".getBytes());
        mapLocalPersistence.addToList("l2", "l212345".getBytes());
        mapLocalPersistence.addToList("l2", "l212345".getBytes());
        mapLocalPersistence.addToList("l2", "l212345".getBytes());
        mapLocalPersistence.addToList("l2", "l212345".getBytes());


        int lsSize = mapLocalPersistence.getForListAll("l1").size();
        Assert.assertEquals(5, lsSize);

        lsSize = mapLocalPersistence.getForListAll("l2").size();
        Assert.assertEquals(5, lsSize);


        lsSize = mapLocalPersistence.getForListWithOffset("l1",0,2).size();
        Assert.assertEquals(2, lsSize);


        lsSize =  mapLocalPersistence.removeForListWithOffset("l1",0,2).size();
        Assert.assertEquals(2, lsSize);

        lsSize =  mapLocalPersistence.removeForListWithOffset("l1",1,2).size();
        Assert.assertEquals(2, lsSize);

        lsSize =  mapLocalPersistence.removeForListWithOffset("l1",2,2).size();
        Assert.assertEquals(1, lsSize);
    }
}
