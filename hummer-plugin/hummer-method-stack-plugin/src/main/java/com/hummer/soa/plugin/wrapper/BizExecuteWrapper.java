package com.hummer.soa.plugin.wrapper;

import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.hummer.common.exceptions.AppException;
import com.hummer.common.utils.AppBusinessAssert;
import com.hummer.core.SpringApplicationContext;
import com.hummer.soa.plugin.enums.ExecuteFlowEnum;
import com.hummer.soa.plugin.enums.RollbackLinkStrategyEnum;
import com.hummer.soa.plugin.excute.BizExecute;
import com.hummer.soa.plugin.excute.BizExecuteResult;
import com.hummer.soa.plugin.rollback.BizRollback;
import com.hummer.soa.plugin.rollback.RollBackContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;


/**
 * 业务方法执行包装器
 *
 * @author liguo.
 * @date 2018/4/20.
 */
public class BizExecuteWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(BizExecuteWrapper.class);
    private ArrayDeque<RollBackContext> rollbackQueue = new ArrayDeque<>(7);

    private static final Integer SERVICE_KEY_NULL_ERROR_CODE = -40000;
    private static final Integer SERVICE_IMPL_INSTANCE_NOT_EXITS_ERROR_CODE = -40001;
    private static final Object OBJ = new Object();
    private static final String ROLLBACK_FUNC_NAME = "rollback";
    private static final String EXEC_FUNC_NAME = "execute";

    private RollbackLinkStrategyEnum rollbackLinkStrategy;


    /**
     * 获取执行对象
     *
     * @param strategyEnum 回滚存储策略
     * @return
     */
    public static BizExecuteWrapper factory(RollbackLinkStrategyEnum strategyEnum) {
        BizExecuteWrapper wrapper = new BizExecuteWrapper();
        wrapper.rollbackLinkStrategy = strategyEnum;
        return wrapper;
    }

    /**
     * 获取执行对象
     *
     * @return
     */
    public static BizExecuteWrapper factory() {
        return factory(RollbackLinkStrategyEnum.STACK_ORDER);
    }

    private BizExecuteWrapper() {

    }

    /**
     * 执行业务方法
     *
     * @param serviceKey      service Impl key
     * @param submitParameter BizExecute 执行参数
     * @param <T>             业务方法返回类型
     * @param <I>             业务方法输入参数
     * @return BizExecute 业务执行返回结果
     */
    public <T, I> T execute(final String serviceKey, final I submitParameter) {
        return execute(serviceKey, submitParameter, null);
    }

    /**
     * 执行业务方法,忽略返回结果
     *
     * @param serviceKey      service Impl key
     * @param submitParameter BizExecute 执行参数
     * @param <I>             业务方法输入参数
     */
    public <I> BizExecuteWrapper submit(final String serviceKey, final I submitParameter) {
        execute(serviceKey, submitParameter, null);
        return this;
    }

    /**
     * 执行业务方法,忽略返回结果
     *
     * @param serviceKey        service Impl key
     * @param submitParameter   BizExecute 执行参数
     * @param rollbackParameter 回滚输入参数
     * @param <I>               业务方法输入参数
     * @param <R>               回滚输入参数
     * @return
     */
    public <I, R> BizExecuteWrapper submit(final String serviceKey, final I submitParameter, final R rollbackParameter) {
        execute(serviceKey, submitParameter, rollbackParameter);
        return this;
    }

    /**
     * 执行业务方法
     *
     * @param serviceKey                  service Impl key
     * @param submitParameter             BizExecute 执行参数
     * @param useBizExecuteResultRollback 是否使用业务执行结果作为回滚业务参数
     * @param <T>                         业务方法返回类型
     * @param <I>                         业务方法输入参数
     * @return 业务执行返回结果
     */
    public <T, I> T execute(final String serviceKey, final I submitParameter, final boolean useBizExecuteResultRollback) {
        return execute(serviceKey, submitParameter, null, useBizExecuteResultRollback);
    }

    /**
     * 执行业务方法
     *
     * @param serviceKey        service Impl key
     * @param submitParameter   BizExecute 执行参数
     * @param rollbackParameter BizRollback 执行参数
     * @param <T>               业务方法返回类型
     * @param <I>               业务方法输入参数
     * @param <R>               回滚方法输入参数
     * @return BizExecute 业务执行返回结果
     */
    public <T, I, R> T execute(final String serviceKey, final I submitParameter, final R rollbackParameter) {
        return execute(serviceKey, submitParameter, rollbackParameter, false);
    }

    /**
     * 执行业务方法
     *
     * @param serviceKey                  service Impl key
     * @param submitParameter             BizExecute 执行参数
     * @param rollbackParameter           BizRollback 执行参数
     * @param useBizExecuteResultRollback 是否使用业务执行结果作为回滚参数
     * @param <T>                         业务方法返回类型
     * @param <I>                         业务方法输入参数
     * @param <R>                         回滚方法输入参数
     * @return BizExecute 业务执行返回结果
     */
    @SuppressWarnings("unchecked")
    public <T, I, R> T execute(final String serviceKey, final I submitParameter, final R rollbackParameter
            , final boolean useBizExecuteResultRollback) {

        AppBusinessAssert.isTrue(StringUtils.isNotEmpty(serviceKey), SERVICE_KEY_NULL_ERROR_CODE
                , "service Impl key 不能为空.");
        Object service = SpringApplicationContext.getBean(serviceKey);
        AppBusinessAssert.isTrue(service != null, SERVICE_IMPL_INSTANCE_NOT_EXITS_ERROR_CODE
                , "业务服务未实现");

        AppBusinessAssert.isTrue((service instanceof BizExecute<?, ?>), SERVICE_IMPL_INSTANCE_NOT_EXITS_ERROR_CODE
                , "业务服务未实现");
        BizExecute<I, T> bizExecute = (BizExecute<I, T>) service;

        boolean isImplBizRollback = service instanceof BizRollback<?>;
        BizRollback<R> rollback = null;
        if (isImplBizRollback) {
            rollback = (BizRollback<R>) service;
        }
        BizExecuteResult<T> executeResult = BizExecuteResult.DEFAULT_FLOW;
        long start = System.currentTimeMillis();
        try {
            LOGGER.debug("biz service,`{}`", serviceKey);
            //实现回滚接口
            if (rollback != null) {
                //回滚上下文
                RollBackContext<R> rollbackContext;
                //检查是否使用执行结果作为回滚输入参数
                try {
                    executeResult = bizExecute.execute(submitParameter);
                    LOGGER.debug("biz service `{}` execute done,rollback parameter {}."
                            , serviceKey
                            , executeResult.getResult());
                } finally {
                    if (useBizExecuteResultRollback
                            || executeResult.getRollbackParameter() != null) {
                        rollbackContext = new RollBackContext<>(rollback, (R) executeResult.getRollbackParameter(), serviceKey);
                    } else {
                        rollbackContext = new RollBackContext<>(rollback, rollbackParameter, serviceKey);
                    }
                }

                //添加至回滚列表
                synchronized (OBJ) {
                    rollbackQueue.add(rollbackContext);
                }

                LOGGER.debug("biz service `{}` rollback add queue done ,queue size:{}."
                        , serviceKey
                        , rollbackQueue.size());

            } else {
                executeResult = bizExecute.execute(submitParameter);
                LOGGER.debug("biz service `{}` execute done.", serviceKey);
            }
            switch (executeResult.getFlowEnum()) {
                case INTERRUPT:
                    //执行所有回滚列表
                    executeRollback(rollbackQueue);
                    break;
                case CONTINUE_NEED_RETRY:
                    //待重试
                    saveParameterForRetry(serviceKey, EXEC_FUNC_NAME, submitParameter);
                    //TODO save exception into db
//                    MemoryEventBusWrapper.postExceptionEvent(executeResult.getThrowable()
//                            , "BizExecuteWrapper|execute"
//                            , ReflectUtils.getClassDesc(bizExecute.getClass())
//                            , ReflectUtils.getMethodDesc(bizExecute.getClass(), EXEC_FUNC_NAME, submitParameter)
//                            , submitParameter
//                            , System.currentTimeMillis() - start
//                            , BizLogTypeEnum.WRAPPER);
                    break;
                case CONTINUE:
                default:
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("biz service `{}` execute exception need rollback,e:{},", serviceKey, e);
            //执行所有回滚列表 && 忽略当前service回滚
            executeRollback(rollbackQueue, serviceKey);
            //TODO save exception into db
//            MemoryEventBusWrapper.postExceptionEvent(e
//                    , "BizExecuteWrapper|execute"
//                    , ReflectUtils.getClassDesc(bizExecute.getClass())
//                    , ReflectUtils.getMethodDesc(bizExecute.getClass(), EXEC_FUNC_NAME, submitParameter)
//                    , submitParameter
//                    , System.currentTimeMillis() - start
//                    , BizLogTypeEnum.WRAPPER);
        } finally {
            //清理已回滚列表
            try {
                removeRollback();
            } catch (Exception e) {
                LOGGER.error("removeRollback exception: ", e);
            }
        }

        // TODO Liguo, error message incorrect
        if (executeResult.getFlowEnum() == ExecuteFlowEnum.INTERRUPT) {
            throw new AppException(50000
                    , "fail");
        }

        LOGGER.debug("biz service `{}` execute success.", serviceKey);
        return executeResult.getResult();
    }

    /**
     * 清空回滚列表
     */
    public void clear() {
        removeRollback();
        LOGGER.info("remove rollback queue clear done.");
    }

    public void clearAll() {
        rollbackQueue.clear();
        LOGGER.info("remove rollback queue clear done.");
    }

    /**
     * rollback
     */
    public void rollback() {
        executeRollback(rollbackQueue);
        LOGGER.info("rollback done...");
    }

    /**
     * 移除已回滚的列表
     */
    @SuppressWarnings("unchecked")
    public <R> void removeRollback() {
        if (CollectionUtils.isEmpty(rollbackQueue)) {
            return;
        }
        Collection<RollBackContext> rollbackContexts = Collections2.filter(rollbackQueue, RollBackContext::isRollback);
        LOGGER.info("remove rollback queue need remove item count {}", rollbackContexts.size());
        if (CollectionUtils.isEmpty(rollbackContexts)) {
            return;
        }
        synchronized (OBJ) {
            ArrayDeque<RollBackContext> tempRollbackQueue = new ArrayDeque<>(rollbackQueue.size());
            Iterator<RollBackContext> contextIterator = rollbackQueue.iterator();
            while (contextIterator.hasNext()) {
                RollBackContext<R> r = contextIterator.next();
                if (!r.isRollback()) {
                    tempRollbackQueue.add(r);
                }
            }
            rollbackQueue.clear();
            rollbackQueue = tempRollbackQueue;
        }
        LOGGER.info("rollback queue size {} .", rollbackQueue.size());
    }

    private void executeRollback(ArrayDeque<RollBackContext> rollbacks) {
        executeRollback(rollbacks, null);
    }

    /**
     * 执行回滚
     *
     * @param rollbacks                回滚队列
     * @param ignoreRollbackServiceKey 指定值且匹配成功，忽略该service回滚
     * @return void
     * @author lee
     * @Date 2018/7/6 19:25
     **/
    private void executeRollback(ArrayDeque<RollBackContext> rollbacks
            , String ignoreRollbackServiceKey) {

        if (CollectionUtils.isEmpty(rollbacks)) {
            return;
        }

        String rollbackList = Joiner
                .on(",")
                .join(Collections2.transform(Collections2.filter(rollbacks
                        , r -> !r.isRollback())
                        , RollBackContext::getServiceKey));
        LOGGER.info("rollback queue size: {},need rollback list :{}"
                , rollbacks.size()
                , rollbackList);

        //按先进后出顺序回滚
        if (RollbackLinkStrategyEnum.STACK_ORDER == rollbackLinkStrategy) {
            for (int i = rollbackQueue.size() - 1; i >= 0; i--) {
                RollBackContext context = Iterables.get(rollbacks, i, null);
                innerExecuteRollback(ignoreRollbackServiceKey, context);
            }
        } else {
            //按先进先出顺序回滚
            for (RollBackContext r : rollbacks) {
                innerExecuteRollback(ignoreRollbackServiceKey, r);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <R> void innerExecuteRollback(String ignoreRollbackServiceKey, RollBackContext r) {
        if (r.isRollback() ||
                r.getServiceKey().equalsIgnoreCase(ignoreRollbackServiceKey)) {
            LOGGER.warn("`{}` 已回滚忽略或忽略回滚", r.getServiceKey());
            return;
        }
        BizRollback<R> rollback = r.getRollback();
        long startTime = System.currentTimeMillis();
        try {
            rollback.rollback((R) r.getParameter());
            //标记为已执行回滚
            r.setRollback(true);
            LOGGER.info("execute service `{}` rollback done"
                    , r.getServiceKey());
        } catch (Exception e) {

            // TODO save exception into db
//            Class<?> classzz = rollback.getClass();
//            MemoryEventBusWrapper.postExceptionEvent(e
//                    , "BizExecuteWrapper|rollback"
//                    , ReflectUtils.getClassDesc(classzz)
//                    , ReflectUtils.getMethodDesc(classzz, ROLLBACK_FUNC_NAME, r.getParameter())
//                    , r.getParameter()
//                    , System.currentTimeMillis() - startTime
//                    , BizLogTypeEnum.WRAPPER);

            //回滚失败数据入库，待补偿
            saveParameterForRetry(r.getServiceKey(), ROLLBACK_FUNC_NAME, r.getParameter());

            LOGGER.error("execute service `{}` rollback exception {}"
                    , r.getServiceKey()
                    , e);
        }
    }

    private void saveParameterForRetry(String serviceKey, String functionName, Object arg) {
        // TODO
//        BizRetrySaveRecordService bean = SpringApplicationContext.getBean(BizRetrySaveRecordService.class);
//        if (bean != null) {
//            bean.saveParameterForRetry(serviceKey, functionName, arg);
//            LOGGER.warn("execute service `{}` rollback exception,data insert db done.", serviceKey);
//        }
    }
}
