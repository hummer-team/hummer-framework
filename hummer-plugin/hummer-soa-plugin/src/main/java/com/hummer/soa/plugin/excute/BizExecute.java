package com.hummer.soa.plugin.excute;


/**
 * 业务方法执行
 *
 * @author liguo.
 * @date 2018/4/20.
 */
public interface BizExecute<I, T> {
    /**
     * 执行业务
     *
     * @param input
     * @return
     */
    BizExecuteResult<T> execute(I input);
}
