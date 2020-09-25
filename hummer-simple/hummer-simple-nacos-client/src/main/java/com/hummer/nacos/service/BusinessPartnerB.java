package com.hummer.nacos.service;

import com.hummer.common.utils.AppBusinessAssert;
import com.hummer.soa.plugin.excute.BizExecute;
import com.hummer.soa.plugin.excute.BizExecuteResult;
import com.hummer.soa.plugin.rollback.BizRollback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * BusinessPartnerB
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/9/14 17:13
 */
@Service("BusinessPartnerB")
@Slf4j
public class BusinessPartnerB implements BizExecute<Boolean, Boolean>, BizRollback<Integer> {


    @Override
    public BizExecuteResult<Boolean> execute(Boolean input) {
        log.debug("BusinessPartnerB execute start");
        try {
            AppBusinessAssert.isTrue(Boolean.TRUE.equals(input), 400, "BusinessPartnerB execute fail");
        } catch (Exception e) {
            return BizExecuteResult.fail(input, e);
        }
        log.debug("BusinessPartnerB execute end");

        return BizExecuteResult.success(true);
    }

    @Override
    public void rollback(Integer input) {

        log.debug("BusinessPartnerB rollback start");

        log.debug("BusinessPartnerB rollback end");

    }
}
