package com.hummer.message.facade.publish;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 15:30
 **/
public interface PublishMessageExceptionCallback {
    /**
     * send message call back
     *
     * @param messageBody body
     * @param throwable   exception
     * @return void
     * @author liguo
     * @date 2019/8/5 15:31
     * @since 1.0.0
     **/
    void callBack(Object messageBody, Throwable throwable);
}
