package com.hummer.first.restfull.plugin;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class FirstRestFullAspect {
    @Autowired
    private RemoteServiceCallWrapper callWrapper;

    @Around("@annotation(call)")
    public Object loadCacheData(ProceedingJoinPoint point
            , HummerSimpleRest call) throws Throwable {

        if (call == null) {
            return point.proceed(point.getArgs());
        }

       // if (call.strategy() == CallStrategyEnum.DECLARE) {

//            return callWrapper.callByDeclare(callByDeclare, call.businessName()
//                    , call.parse(), null);
       // }

//        return callWrapper.callByConfig(callByConfig, call.businessName()
//                , call.parse(), null);
        return null;
    }
}
