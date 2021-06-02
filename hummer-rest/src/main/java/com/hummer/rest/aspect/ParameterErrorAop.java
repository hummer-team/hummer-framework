package com.hummer.rest.aspect;

import com.hummer.core.PropertiesContainer;
import com.hummer.rest.utils.ParameterAssertUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletResponse;

/**
 * @author chen wei
 */
@Aspect
@Component
public class ParameterErrorAop {
    private static final String ASPECT_ENABLE = "com.hummer.rest.request.body.error.aspect.enable";

    @Pointcut("execution(* com..controller.*Controller.*(..))")
    public void restMethod() {

    }

    @Before("restMethod()")
    public void doBeforeMethod(JoinPoint jp) {
        if (!PropertiesContainer.valueOf(ASPECT_ENABLE, Boolean.class, false)) {
            return;
        }
        Object[] args = jp.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        for (Object obj : args) {
            if (obj == null) {
                continue;
            }
            if (obj instanceof Errors) {
                ParameterAssertUtil.assertRequestFirstValidated(HttpServletResponse.SC_BAD_REQUEST, (Errors) obj);
            }
        }
    }

}
