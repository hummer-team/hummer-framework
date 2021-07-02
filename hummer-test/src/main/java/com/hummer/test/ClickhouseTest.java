package com.hummer.test;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.hummer.core.config.PropertiesConfig;
import com.hummer.core.init.HummerApplicationContextInit;
import com.hummer.core.listener.SpringStarterListener;
import com.hummer.core.starter.BootStarterBean;
import com.hummer.dao.starter.ExportDaoInitBean;
import com.hummer.test.dao.UserCouponDao;
import com.hummer.test.main.ApplicationStart;
import com.hummer.test.po.UserCouponPo;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * olap database clickhouse unit test
 *
 * @author lee
 */
@RunWith(value = SpringRunner.class)
@ContextConfiguration(classes = {ConfigFileApplicationContextInitializer.class, HummerApplicationContextInit.class})
@Import(value = {PropertiesConfig.class, BootStarterBean.class, ExportDaoInitBean.class, SpringStarterListener.class})
@PropertySource(value = {"classpath:application.properties"})
@SpringBootTest(classes = ApplicationStart.class)
public class ClickhouseTest {
    @Autowired
    private UserCouponDao userCouponDao;

    @Test
    public void queryUserCoupon() {
        List<UserCouponPo> couponPos = userCouponDao.queryBuIds(Lists.newArrayList(1692594, 1692595, 1692613));
        Assert.assertEquals(3, couponPos.size());
        System.out.println(JSON.toJSONString(couponPos));
    }

    @Test
    public void maxUsedMoney() {
        double max = userCouponDao.maxUsedMoney();
        System.out.println(max);
        Assert.assertEquals(121.0, max);
    }

    @Test
    public void top10UserId() {
        String list = userCouponDao.top10UserId();
        System.out.println(list);
        Assert.assertEquals(true, list.contains("bd0ce3d4-4c9a-42ee-8652-7e41e07ec2f2"));
    }
}
