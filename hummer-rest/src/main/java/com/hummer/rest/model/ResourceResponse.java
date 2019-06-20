package com.hummer.rest.model;

import java.util.Date;

/**
 * rest resource data wrapper
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/20 18:00
 **/
public class ResourceResponse<T> {
    private int code;
    private String message;
    private T data;
    private String trackId;
    private Date time;

    public ResourceResponse(final int code, final String message
            , final T data
            , final String trackId
            , final Date time) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.trackId = trackId;
        this.time = time;
    }

    public ResourceResponse() {

    }

    public int getCode() {
        return code;
    }

    public ResourceResponse<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResourceResponse<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public Date getTime() {
        return time;
    }

    public ResourceResponse<T> setTime(Date time) {
        this.time = time;
        return this;
    }

    public ResourceResponse<T> setData(T data) {
        this.data = data;
        return this;
    }

    public String getTrackId() {
        return trackId;
    }

    public static <T> ResourceResponse<T> newInstance() {
        return new ResourceResponse<>();
    }

    public ResourceResponse<T> build() {
        return this;
    }

    public ResourceResponse<T> setTrackId(final String trackId) {
        this.trackId = trackId;
        return this;
    }

    public static <V> ResourceResponse<V> fail(final String message) {
        ResourceResponse<V> result = new ResourceResponse<>();
        result.setMessage(message);
        result.setCode(-1);
        return result;
    }

    public static <V> ResourceResponse<V> fail(final int status, final String message) {
        ResourceResponse<V> result = new ResourceResponse<>();
        result.setCode(status);
        result.setMessage(message);
        return result;
    }

    public static <V> ResourceResponse<V> ok() {
        ResourceResponse<V> result = new ResourceResponse<>();
        result.setMessage("success");
        return result;
    }


    public static <V> ResourceResponse<V> ok(final V data) {
        ResourceResponse<V> result = new ResourceResponse<>();
        result.setCode(0);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <V> ResourceResponse<V> ok(final String message, final V data) {
        ResourceResponse<V> result = new ResourceResponse<>();
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <V> ResourceResponse<V> ok(final int status,final String message,final V data) {
        ResourceResponse<V> result = new ResourceResponse<>();
        result.setCode(status);
        result.setMessage(message);
        result.setData(data);
        result.setTime(new Date());
        return result;
    }

    @Override
    public String toString() {
        return "ResourceResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", trackId='" + trackId + '\'' +
                ", time=" + time +
                '}';
    }
}
