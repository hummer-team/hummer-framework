package com.hummer.rest.monitor;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/20 18:48
 **/
public class GlobaleExceptionContext {
    private Throwable throwable;
    private String url;
    private Object param;
    private long costTimeMillisecond;
}
