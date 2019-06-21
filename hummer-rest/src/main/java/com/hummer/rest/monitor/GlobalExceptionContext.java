package com.hummer.rest.monitor;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/20 18:48
 **/
@Data
@Builder
public class GlobalExceptionContext {
    private Throwable throwable;
    private String url;
    private Object param;
    private long costTimeMillisecond;
}
