package com.hummer.yug.tools.plugin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hummer.common.exceptions.AppException;

/**
 * DistributionResponseUtil
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/12/30 15:33
 */
public class DistributionResponseUtil {

    public static <T> T parseResponseV2WithStatus(final String jsonValue
            , TypeReference<DistributionWebResult<T>> typeReference) {
        DistributionWebResult<T> response = JSON.parseObject(jsonValue, typeReference);
        if (response == null) {
            throw new AppException(50000, "call service failed.");
        } else if (!"SUCCESS".equalsIgnoreCase(response.getCode())) {
            throw new AppException(40000, response.getCode() + response.getMessage());
        } else {
            return response.getData();
        }
    }
}
