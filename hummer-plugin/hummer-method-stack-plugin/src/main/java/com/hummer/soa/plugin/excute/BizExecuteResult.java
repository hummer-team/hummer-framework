package com.hummer.soa.plugin.excute;

import com.hummer.soa.plugin.enums.ExecuteFlowEnum;
import org.apache.commons.lang3.ObjectUtils;

import javax.validation.constraints.Null;

/**
 * @author liguo.
 * @date 2018/4/20.
 */
public class BizExecuteResult<T> {

    public static final BizExecuteResult DEFAULT_FLOW =
            new BizExecuteResult<ObjectUtils.Null>(null, ExecuteFlowEnum.CONTINUE, null);

    private T result;
    private ExecuteFlowEnum flowEnum;
    private Object rollbackParameter;
    private Throwable throwable;

    public BizExecuteResult() {

    }

    public BizExecuteResult(T result) {
        this.result = result;
        this.flowEnum = ExecuteFlowEnum.CONTINUE;
    }

    public BizExecuteResult(T result, ExecuteFlowEnum flowEnum) {
        this.result = result;
        this.flowEnum = flowEnum;
    }

    public BizExecuteResult(T result
            , ExecuteFlowEnum flowEnum
            , @Null Object rollbackParameter
    ) {
        this.result = result;
        this.flowEnum = flowEnum;
        this.rollbackParameter = rollbackParameter;
    }

    /**
     * @param result 2018/7/9结18:40  *[result, flowEnum, rollbackParameter, throwable]param throwable         异常
     * @return
     * @author lee
     * @Date 2018/7/9 16:53
     **/
    public BizExecuteResult(T result
            , ExecuteFlowEnum flowEnum
            , @Null Object rollbackParameter
            , @Null Throwable throwable) {
        this.result = result;
        this.flowEnum = flowEnum;
        this.rollbackParameter = rollbackParameter;
        this.throwable = throwable;
    }

    public static BizExecuteResult<ObjectUtils.Null> success() {
        return new BizExecuteResult<>(null, ExecuteFlowEnum.CONTINUE, null);
    }

    public static <T> BizExecuteResult<T> success(final T result) {
        return new BizExecuteResult<>(result, ExecuteFlowEnum.CONTINUE);
    }

    public static <T> BizExecuteResult<T> fail(final T result) {
        return new BizExecuteResult<>(result, ExecuteFlowEnum.INTERRUPT, null);
    }

    public static BizExecuteResult<ObjectUtils.Null> fail() {
        return new BizExecuteResult<>(null, ExecuteFlowEnum.INTERRUPT, null);
    }

    public static <T> BizExecuteResult<T> fail(final T result, Object rollbackParameter) {
        return new BizExecuteResult<>(result, ExecuteFlowEnum.INTERRUPT, rollbackParameter);
    }


    public static <T> BizExecuteResult<T> fail(final T result, Throwable throwable) {
        return new BizExecuteResult<>(result
                , ExecuteFlowEnum.INTERRUPT
                , null
                , throwable);
    }

    public static BizExecuteResult<ObjectUtils.Null> fail(Throwable throwable) {
        return new BizExecuteResult<>(null
                , ExecuteFlowEnum.INTERRUPT
                , null
                , throwable);
    }

    public static <T> BizExecuteResult<T> fail(final T result, Object rollbackParameter, Throwable throwable) {
        return new BizExecuteResult<>(result
                , ExecuteFlowEnum.INTERRUPT
                , rollbackParameter
                , throwable);
    }

    public T getResult() {
        return result;
    }

    public Object getRollbackParameter() {
        return rollbackParameter;
    }

    public ExecuteFlowEnum getFlowEnum() {
        return flowEnum;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        return "BizExecuteResult{" +
                "result=" + result +
                ", flowEnum=" + flowEnum +
                ", rollbackParameter=" + rollbackParameter +
                ", throwable=" + throwable +
                '}';
    }
}
