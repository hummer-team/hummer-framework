package com.hummer.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hummer.common.exceptions.AppException;
import com.hummer.common.resource.ResourceResponse;


/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/21 16:57
 **/
public class ResponseUtil {
    private ResponseUtil() {

    }

    public static <T> ResourceResponse<T> parseResponse(final String jsonValue
            , TypeReference<ResourceResponse<T>> typeReference) {
        return JSON.parseObject(jsonValue, typeReference);
    }

    public static <T> ResourceResponse<T> parseResponse(final String jsonValue) {
        return JSON.parseObject(jsonValue, new TypeReference<ResourceResponse<T>>() {
        });
    }

    public static <T> T parseResponseV2(final String jsonValue
            , TypeReference<ResourceResponse<T>> typeReference) {
        ResourceResponse<T> response = JSON.parseObject(jsonValue, typeReference);
        return response != null ? response.getData() : null;
    }

    public static <T> T parseResponseV2(final String jsonValue) {
        ResourceResponse<T> response = JSON.parseObject(jsonValue, new TypeReference<ResourceResponse<T>>() {
        });
        return response != null ? response.getData() : null;
    }

    public static <T> ResourceResponse<T> parseResponseWithStatus(final String jsonValue
            , TypeReference<ResourceResponse<T>> typeReference) {
        ResourceResponse<T> response = JSON.parseObject(jsonValue, typeReference);
        if (response != null && response.getCode() != 0) {
            throw new AppException(response.getCode(), response.getMessage());
        }
        return response;
    }

    public static <T> ResourceResponse<T> parseResponseWithStatus(final String jsonValue) {
        ResourceResponse<T> response = JSON.parseObject(jsonValue, new TypeReference<ResourceResponse<T>>() {
        });
        if (response != null && response.getCode() != 0) {
            throw new AppException(response.getCode(), response.getMessage());
        }
        return response;
    }

    public static <T> T parseResponseWithStatusV2(final String jsonValue) {
        ResourceResponse<T> response = JSON.parseObject(jsonValue, new TypeReference<ResourceResponse<T>>() {
        });
        if (response != null && response.getCode() != 0) {
            throw new AppException(response.getCode(), response.getMessage());
        }
        return response == null ? null : response.getData();
    }


    public static <T> T parseResponseV2WithStatus(final String jsonValue
            , TypeReference<ResourceResponse<T>> typeReference) {
        ResourceResponse<T> response = JSON.parseObject(jsonValue, typeReference);

        if (response == null) {
            throw new AppException(50000, "call service failed.");
        }

        if (response.getCode() != 0) {
            throw new AppException(response.getCode(), response.getMessage());
        }
        return response.getData();
    }
}