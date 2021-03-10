package com.hummer.dubbo.extend.plugin;

import com.google.common.base.Strings;
import com.hummer.common.SysConstant;
import com.hummer.common.utils.CommonUtil;
import com.hummer.common.utils.IpUtil;
import com.hummer.core.PropertiesContainer;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * this is business api trace filter
 *
 * @author edz
 */
@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER}, order = Integer.MIN_VALUE)
public class Trace2Filter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(Trace2Filter.class);

    /**
     * Make sure call invoker.invoke() in your implementation.
     *
     * @param invoker
     * @param invocation
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        buildRequestHeader(invocation);
        long start = System.currentTimeMillis();
        Result r = invoker.invoke(invocation);
        log(r, invocation, start);
        return r;
    }

    private void log(Result r, Invocation invocation, long startTime) {
        long costTime = System.currentTimeMillis() - startTime;
        int slowCostTime = PropertiesContainer.valueOf("request.cost.time.slow.value"
                , Integer.class, 100);
        if (costTime >= slowCostTime) {
            logger.warn("{} -{} - {} cost ms", invocation.getServiceName(), invocation.getMethodName(), costTime);
        }
        if (r.hasException()) {
            logger.error("{} - {} - {} cost ms,method args {},has exception: "
                    , invocation.getInvoker().getInterface()
                    , invocation.getMethodName()
                    , costTime
                    , invocation.getArguments()
                    , r.getException());
        }
    }

    private String buildRequestHeader(Invocation invocation) {
        String requestId = MDC.get(SysConstant.REQUEST_ID);
        if (StringUtils.isEmpty(requestId)) {
            requestId = invocation.getAttachment(SysConstant.REQUEST_ID);
            if (StringUtils.isEmpty(requestId)) {
                requestId = UUID.randomUUID().toString().replaceAll("-", "");
                invocation.setAttachment(SysConstant.REQUEST_ID, requestId);
            }
        }
        MDC.put(SysConstant.REQUEST_ID, requestId);
        MDC.put(SysConstant.RestConstant.SERVER_IP, IpUtil.getLocalIp());
        String clientIp = invocation.getAttachment(SysConstant.RestConstant.CLIENT_IP);
        MDC.put(SysConstant.RestConstant.CLIENT_IP, clientIp);
        if (Strings.isNullOrEmpty(clientIp)) {
            MDC.put(SysConstant.RestConstant.CLIENT_IP, IpUtil.getLocalIp());
            invocation.setAttachment(SysConstant.RestConstant.CLIENT_IP, clientIp);
        }
        String ctime = invocation.getAttachment(SysConstant.RestConstant.CLIENT_TIME);
        if (Strings.isNullOrEmpty(ctime)) {
            invocation.setAttachment(SysConstant.RestConstant.CLIENT_TIME, System.currentTimeMillis());
        }
        String pSpanId = invocation.getAttachment(SysConstant.RestConstant.SPAN_ID);
        MDC.put(SysConstant.RestConstant.PARENT_SPAN_ID, StringUtils.isNotEmpty(pSpanId)
                ? pSpanId : CommonUtil.getUuidShort());
        return requestId;
    }

}
