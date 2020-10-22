package com.hummer.user.plugin.user.member;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.hummer.common.exceptions.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ResponseParseUtil
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/9/10 13:49
 */
public class NetCoreResponseParseUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetCoreResponseParseUtil.class);

    public static <T> NetCoreResponse<T> parsingNetResponseByAssert(String response
            , TypeReference<NetCoreResponse<T>> reference) {

        return parsingNetResponseByAssert(response, reference, null, null);
    }

    public static <T> NetCoreResponse<T> parsingNetResponseByAssert(String response
            , TypeReference<NetCoreResponse<T>> reference, Integer errorCode, String message) {
        NetCoreResponse<T> respDto = JSON.parseObject(response, reference);
        assertNetResponse(respDto, errorCode, message);
        return respDto;
    }

    private static void assertNetResponse(NetCoreResponse<?> respDto, Integer errorCode, String message) {
        if (respDto != null && respDto.getCode() == 200 && respDto.getData() != null) {
            return;
        }
        if (errorCode != null) {
            LOGGER.error("call response is fail , response=={}", JSONObject.toJSONString(respDto));
            throw new AppException(errorCode, message);
        }
        if (respDto == null) {
            throw new AppException(50000, "call service failed. no response");
        } else {
            throw new AppException(respDto.getCode(), respDto.getMsg());
        }
    }
}
