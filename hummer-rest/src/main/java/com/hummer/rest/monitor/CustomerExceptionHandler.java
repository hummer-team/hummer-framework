package com.hummer.rest.monitor;

/**
 * business logic customer exception handle ,notice this not required
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/20 17:52
 **/
public interface CustomerExceptionHandler {
    /**
     * impl handle exception
     *
     * @param context
     * @return void
     * @author liguo
     * @date 2019/6/20 17:55
     * @version 1.0.0
     **/
    void hande(GlobalExceptionContext context);
}
