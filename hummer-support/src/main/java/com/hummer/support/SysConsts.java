package com.hummer.support;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/20 14:39
 **/
public class SysConsts {
    private SysConsts(){

    }
    public static final Integer SYS_ERROR_CODE = 50000;
    public static final String REQUEST_ID = "requestId";
    public static final String HEADER_REQ_TIME = "X-HJ-Request-Time";

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String DEFAULT_CHARSET_NAME = "UTF-8";
    public static final String SERVER_IP = "serverIp";
    public static final String CLIENT_IP = "clientIp";
    public static final String MVC_SERIALIZERFEATURE = "mvc.serializerFeature";
    public static final String SYSTEM_REMOTE_IP_SPLIT_CHAR = "system.remote.ip.split.char";
}
