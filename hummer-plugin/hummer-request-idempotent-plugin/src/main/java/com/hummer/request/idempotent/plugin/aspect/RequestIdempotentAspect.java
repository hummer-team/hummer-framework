package com.hummer.request.idempotent.plugin.aspect;

import com.hummer.common.SysConstant;
import com.hummer.common.exceptions.BusinessIdempotentException;
import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import com.hummer.request.idempotent.plugin.KeyUtil;
import com.hummer.request.idempotent.plugin.annotation.RequestIdempotentAnnotation;
import com.hummer.request.idempotent.plugin.valid.ParamsIdempotentValidator;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * RequestIdempotentAspect
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/28 10:28
 */
@Aspect
@Component
public class RequestIdempotentAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestIdempotentAspect.class);

    @Around(" @annotation(requestIdempotent)")
    public Object requestIdempotent(ProceedingJoinPoint point, RequestIdempotentAnnotation requestIdempotent) throws Throwable {
        LOGGER.debug("request idempotent aspect : >>>> {}", point.getSignature());
        if (!requestIdempotent.enable()
                || !PropertiesContainer.valueOf("request.idempotent.verify.enable", Boolean.class, true)) {
            return point.proceed(point.getArgs());
        }
        String key = KeyUtil.formatKey(getApplicationName(requestIdempotent.applicationName())
                , requestIdempotent.businessCode(), point, requestIdempotent);
        ParamsIdempotentValidator validator = SpringApplicationContext.getBean(ParamsIdempotentValidator.class);
        if (validator.validParamsIdempotent(key, requestIdempotent.expireSeconds())) {
            throw new BusinessIdempotentException(SysConstant.BUSINESS_IDEMPOTENT_ERROR_CODE, "请求重复");
        }
        try {
            return point.proceed(point.getArgs());
        } catch (Exception e) {
            LOGGER.debug("method=={}, proceed fail,remove key", point.getSignature());
            validator.removeValidKey(key);
            throw e;
        }
    }

    private String getApplicationName(String applicationName) {
        return StringUtils.isEmpty(applicationName) ? PropertiesContainer.valueOfString("spring.application.name")
                : applicationName;
    }
}
