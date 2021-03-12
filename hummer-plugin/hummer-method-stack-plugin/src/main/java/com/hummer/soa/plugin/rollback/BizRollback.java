package com.hummer.soa.plugin.rollback;

/**
 * @author liguo.
 * @date 2018/4/20.
 */
public interface BizRollback<R> {

    /**
     * 执行业务回滚操作
     *
     * @param input 回滚输入参数.
     */
    void rollback(R input);

    /**
     * Decodes 使用 @BizMethodFilter 的methodDesc 属性替换，设置具体业务描述
     * 回滚操作描述
     */
    @Deprecated
    default BizRollbackDescription rollbackDescription() {
        return null;
    }
}
