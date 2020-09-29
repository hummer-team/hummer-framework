package com.hummer.doorgod.test;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/7/9 14:42
 **/
public class SimpleDemoFacadeTest /**extends BaseTest**/
{
    @Test
    public void json() {
        Map<String, Object> map = new HashMap<>(3);
        map.put("A", null);
        map.put("B", "D");
        map.put("C", "s");

        System.out.println(JSON.toJSONString(map));
    }

    @Test
    public void configJson() {
        System.out.println(JSON.toJSONString(Config.builder()
                .order(12)
                .routeId("assss")
                .build()));
    }

    @Test
    public void hashSet() {
        HashSet<Config> hashSet = new HashSet<>();

        hashSet.add(Config.builder()
                .order(12)
                .routeId("assss")
                .build());

        hashSet.add(Config.builder()
                .order(345)
                .routeId("assss")
                .build());

        System.out.println(hashSet);
    }

    @Test
    public void localHost() throws UnknownHostException {
        System.out.println(InetAddress.getLocalHost().getHostName());
        System.out.println(InetAddress.getLocalHost().getCanonicalHostName());
    }

    @Test
    public void isNoeBlack() {
      boolean isNoeBlack =  StringUtils.isNoneBlank("a", "") && 787 != -1;
      System.out.println(isNoeBlack);
    }
}
