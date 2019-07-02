package com.hummer.common.http;

/**
 * business logic need Impl this interface, http async client will callback .
 * <pre>
 *     R:response
 *     I:input
 * </pre>
 *
 * @author liguo.
 * @date 2018/11/6.
 */
public interface SendMessageHandle<R, I> {
    /**
     * send request success
     *
     * @param response service response body
     * @return void
     * @author lee
     * @Date 2018/11/6 15:54
     **/
    default void complete(R response) {

    }

    /**
     * send request fail
     *
     * @param message request message
     * @param e       exception
     * @return void
     * @author lee
     * @Date 2018/11/6 15:54
     **/
    default void fail(I message, Exception e) {

    }
}
