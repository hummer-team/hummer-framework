package com.hummer.nacos.service;

import com.hummer.cache.plugin.HummerSimpleObjectCache;
import com.hummer.nacos.model.CustomItemBo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * QueryByCacheImpl
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/6/1 11:08
 */
@Component
public class QueryByCacheImpl {

    @HummerSimpleObjectCache(businessCode = "hummer-cache-test", timeoutSeconds = 600)
    public List<CustomItemBo> queryTest() {

        List<CustomItemBo> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(CustomItemBo.builder().b(i).c(i + 10).build());
        }
        return list;
    }
}
