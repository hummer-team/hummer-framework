package com.hummer.soa.plugin.rollback;


/**
 * @author liguo.
 * @date 2018/4/27.
 */
public class RollBackContext<R> {
    private BizRollback rollback;
    private R parameter;

    private boolean isRollback;
    private String serviceKey;

    public RollBackContext(BizRollback rollback, R parameter, String serviceKey) {
        this.rollback = rollback;
        this.parameter = parameter;
        this.isRollback = false;
        this.serviceKey = serviceKey;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public boolean isRollback() {
        return isRollback;
    }

    public void setRollback(boolean rollback) {
        isRollback = rollback;
    }

    public R getParameter() {
        return parameter;
    }

    public BizRollback getRollback() {
        return rollback;
    }
}
