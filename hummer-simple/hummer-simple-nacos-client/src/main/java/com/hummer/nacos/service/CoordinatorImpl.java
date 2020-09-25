package com.hummer.nacos.service;

import com.hummer.soa.plugin.wrapper.BizExecuteWrapper;
import org.springframework.stereotype.Service;

/**
 * CoordinatorImpl
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/9/14 17:12
 */
@Service("CoordinatorImpl")
public class CoordinatorImpl {


    public void testSoa(boolean flag) {

        BizExecuteWrapper executeWrapper = BizExecuteWrapper.factory();

        executeWrapper.execute("BusinessPartnerA", flag, 1);
        executeWrapper.execute("BusinessPartnerB", !flag, 2);

    }
}
