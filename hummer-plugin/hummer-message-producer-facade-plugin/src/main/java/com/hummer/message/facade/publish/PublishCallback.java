package com.hummer.message.facade.publish;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 15:30
 **/
public interface PublishCallback {
    /**
     * send message call back，throwable if null then send ok else failed
     *
     * @param messageBody body
     * @param throwable   exception
     * @param partition   kafka partition
     * @param offset      kafka offset
     * @return void
     * @author liguo
     * @date 2019/8/5 15:31
     * @since 1.0.0
     **/
    default void callBack(int partition, long offset, Object messageBody, Throwable throwable) {
        if (throwable != null) {
            exception(messageBody, throwable);
        } else {
            success(partition, offset);
        }
    }

    /**
     * success
     *
     * @param partition partition
     * @param offset    offset
     */
    default void success(int partition, long offset) {

    }

    /**
     * exception
     *
     * @param messageBody messageBody
     * @param throwable   throwable
     */
    default void exception(Object messageBody, Throwable throwable) {

    }
}
