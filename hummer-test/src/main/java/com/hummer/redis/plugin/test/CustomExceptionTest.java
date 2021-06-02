package com.hummer.redis.plugin.test;

import com.hummer.common.http.HttpSyncClient;
import com.hummer.common.utils.DateUtil;
import com.hummer.common.utils.NumberUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/4/28 11:17
 */
public class CustomExceptionTest {

    public static final int A_A = 1;
    public static final int A_B = 2;

    @Test
    public void systemException() {
        System.out.println(Runtime.getRuntime().availableProcessors());
    }

    @Test
    public void dateTest() {
        Date date = DateUtil.now();
    }

    @Test
    public void costTest() {

        String content = HttpSyncClient.sendHttpGet("http://localhost:20008/swagger-ui.html");
        System.out.println(content.length());
    }

    @Test
    public void test1() {
        Map<String, Boolean> map = new HashMap<>();
        map.put("key", true);
        System.out.println(getOne(map));
        System.out.println(map.get("key"));
    }

    private int getOne(Map<String, Boolean> flag) {
        flag.put("key", false);
        return 1;
    }

    @Test
    public void number() {
        String v = "10";
        Long l = NumberUtil.to(v, 0L, Long.class);
        Long a = 10L;
        Assert.assertEquals(a, l);
    }
}
