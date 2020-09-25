package com.hummer.nacos.service;

import com.hummer.common.utils.AppBusinessAssert;
import com.hummer.soa.plugin.excute.BizExecute;
import com.hummer.soa.plugin.excute.BizExecuteResult;
import com.hummer.soa.plugin.rollback.BizRollback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * BusinessPartnerA
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/9/14 17:12
 */
@Service("BusinessPartnerA")
@Slf4j
public class BusinessPartnerA implements BizExecute<Boolean, Boolean>, BizRollback<Integer> {


    @Override
    public BizExecuteResult<Boolean> execute(Boolean input) {
        log.debug("BusinessPartnerA execute start=============");
        try {
            AppBusinessAssert.isTrue(Boolean.TRUE.equals(input), 400, "BusinessPartnerA execute fail");
        } catch (Exception e) {
            return BizExecuteResult.fail(input, e);
        }
        log.debug("BusinessPartnerA execute end=============");

        return BizExecuteResult.success(true);
    }

    @Override
    public void rollback(Integer input) {

        log.debug("BusinessPartnerA rollback start=============");

        log.debug("BusinessPartnerA rollback end=============");

    }
}
