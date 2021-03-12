package com.hummer.soa.plugin.excute;


import com.hummer.soa.plugin.enums.RollbackEnum;

/**
 * Decodes 待替换
 *
 * @author liguo.
 * @date 2018/4/21.
 * @deprecated 此策略暂不使用，待重构
 */
@Deprecated
public class BizExecuteStrategy {

    public static final BizExecuteStrategy DEFAULT_STRATEGY = new BizExecuteStrategy();

    private String description = "";
    private int order = 0;
    private boolean async = false;
    private boolean executeExceptionThrowOut = true;
    private String message = "biz execute fail.";
    private int errorCode = -50000;
    /**
     * 默认处理异常，数据入库等待重试
     *
     * @author lee
     * @Date 2018/7/4 15:01
     **/
    private boolean useDefaultHandleError = true;
    private boolean rollbackBeforeWriteToDb = true;
    private boolean needRollbackLinkList = true;

    public boolean isNeedRetry() {
        return needRetry;
    }

    private boolean needRetry = true;
    private RollbackEnum rollbackEnum = RollbackEnum.ROLLBACK_ALL_BIZ;

    public BizExecuteStrategy() {
        rollbackEnum = RollbackEnum.ROLLBACK_ALL_BIZ;
    }

    public static final BizExecuteStrategy SWALLOW_EXCEPTION_STRATEGY = new BizExecuteStrategy(false);


    public BizExecuteStrategy(boolean executeExceptionThrowOut) {
        this.executeExceptionThrowOut = executeExceptionThrowOut;
    }

    public RollbackEnum getRollbackEnum() {
        return rollbackEnum;
    }

    public void setRollbackEnum(RollbackEnum rollbackEnum) {
        this.rollbackEnum = rollbackEnum;
    }

    public boolean isNeedRollbackLinkList() {
        return needRollbackLinkList;
    }

    public void setNeedRollbackLinkList(boolean needRollbackLinkList) {
        this.needRollbackLinkList = needRollbackLinkList;
    }

    public boolean isUseDefaultHandleError() {
        return useDefaultHandleError;
    }

    public boolean isRollbackBeforeWriteToDb() {
        return rollbackBeforeWriteToDb;
    }

    public void setRollbackBeforeWriteToDb(boolean rollbackBeforeWriteToDb) {
        this.rollbackBeforeWriteToDb = rollbackBeforeWriteToDb;
    }

    public void setUseDefaultHandleError(boolean useDefaultHandleError) {
        this.useDefaultHandleError = useDefaultHandleError;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isExecuteExceptionThrowOut() {
        return executeExceptionThrowOut;
    }

    public void setExecuteExceptionThrowOut(boolean executeExceptionThrowOut) {
        this.executeExceptionThrowOut = executeExceptionThrowOut;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "BizExecuteStrategy{" +
                "description='" + description + '\'' +
                ", order=" + order +
                ", async=" + async +
                ", executeExceptionThrowOut=" + executeExceptionThrowOut +
                ", message='" + message + '\'' +
                ", errorCode=" + errorCode +
                ", useDefaultHandleError=" + useDefaultHandleError +
                ", rollbackBeforeWriteToDb=" + rollbackBeforeWriteToDb +
                ", needRollbackLinkList=" + needRollbackLinkList +
                ", rollbackEnum=" + rollbackEnum +
                '}';
    }
}
