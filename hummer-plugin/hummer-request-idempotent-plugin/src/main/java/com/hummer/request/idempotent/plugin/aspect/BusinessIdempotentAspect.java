package com.hummer.request.idempotent.plugin.aspect;

import com.hummer.common.exceptions.AppException;
import com.hummer.common.exceptions.BusinessIdempotentException;
import com.hummer.core.PropertiesContainer;
import com.hummer.request.idempotent.plugin.annotation.BusinessIdempotentAnnotation;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * BusinessIdempotentAspect
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/12/14 13:52
 */
@Aspect
@Component
@Order(Integer.MAX_VALUE - 1)
public class BusinessIdempotentAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessIdempotentAspect.class);


    @AfterThrowing(value = "@annotation(businessIdempotent)", throwing = "ex")
    public void businessIdempotent(BusinessIdempotentAnnotation businessIdempotent, Throwable ex) {
        if (ex instanceof BusinessIdempotentException) {
            throw (BusinessIdempotentException) ex;
        }
        if (ex instanceof SQLIntegrityConstraintViolationException) {
            LOGGER.warn("sql Duplicate entry exception,", ex);
            throw new BusinessIdempotentException(businessIdempotent.code(), ex.getMessage());
        } else if (ex instanceof AppException) {
            AppException exception = (AppException) ex;
            if (exception.getCode() == businessIdempotent.code() *
                    PropertiesContainer.valueOfInteger("hummer.response.error.code.ratio", 100)) {

                throw new BusinessIdempotentException(businessIdempotent.code(), ex.getMessage());
            }
        }

    }
}
