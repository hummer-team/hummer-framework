package com.hummer.doorgod.test;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
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
        Config config = new Config();
        Map<String, List<String>> map=new HashMap<>();
        map.put("a", Lists.newArrayList("a","b"));
        map.put("b", Lists.newArrayList("a","b"));
        config.setBlackHead(map);

        System.out.println(JSON.toJSONString(config));
    }
}
