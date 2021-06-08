package com.hummer.first.restfull.plugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hummer.common.http.HttpInternalUtil;
import com.hummer.common.http.HttpSyncClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * @author edz
 */
@Component
@Slf4j
public class RemoteServiceCallWrapperImpl implements RemoteServiceCallWrapper {

    @Override
    public Object callByConfig(HummerRestByConfig config, String businessName, Class<? extends CustomParseResp> parse
            , Type type) throws Exception {
        return null;
    }

    @Override
    public Object callByDeclare(HummerRestByDeclare declare, String businessName
            , Class<? extends CustomParseResp> parse, Type type) throws Exception {
        HttpRequestBase requestBase = HttpInternalUtil.getHttpRequestV2(String.format("%s%s", declare.host()
                , declare.apiPath()), RequestMethod.valueOf(declare.httpMethod().toUpperCase()));
        String result = HttpSyncClient.sendHttpRequestByRetry(requestBase, declare.timeOutMills(), TimeUnit.MILLISECONDS
                , declare.retryCount());

        if (parse != null) {
            return parse.newInstance().parse(result);
        }

        return JSON.parseObject(result, new TypeReference(type) {
        });
    }
}
