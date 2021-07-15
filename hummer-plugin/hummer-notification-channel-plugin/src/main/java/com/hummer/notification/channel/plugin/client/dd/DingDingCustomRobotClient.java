package com.hummer.notification.channel.plugin.client.dd;

import com.alibaba.fastjson.JSONObject;
import com.hummer.common.SysConstant;
import com.hummer.common.exceptions.AppException;
import com.hummer.common.http.HttpSyncClient;
import com.hummer.common.utils.CommonUtil;
import com.hummer.common.utils.EncoderUtil;
import com.hummer.notification.channel.plugin.config.DdCustomRobotConfig;
import com.hummer.notification.channel.plugin.context.DdCustomRobotContext;
import com.hummer.notification.channel.plugin.result.DdCustomRobotNotifyResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * DingDingCustomClient
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/13 13:57
 */
public class DingDingCustomRobotClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DingDingCustomRobotClient.class);


    public static DdCustomRobotNotifyResponse doNotify(DdCustomRobotContext context
            , DdCustomRobotConfig config) {

        DdCustomRobotNotifyResponse result = null;
        Exception exp = null;
        try {
            String url = composeUrl(config);
            String response = HttpSyncClient.sendHttpPostByRetry(url, JSONObject.toJSONString(context)
                    , CommonUtil.ifNullDefault(config.getTimeOutMillions(), 3000L), TimeUnit.MILLISECONDS
                    , CommonUtil.ifNullDefault(config.getRetry(), 1));
            result = JSONObject.parseObject(response, DdCustomRobotNotifyResponse.class);
        } catch (Exception e) {
            exp = e;
        }

        return parseResponse(result, exp);
    }

    private static DdCustomRobotNotifyResponse parseResponse(DdCustomRobotNotifyResponse response, Exception exp) {
        if (exp != null) {
            LOGGER.error("ding ding notification send fail,", exp);
            DdCustomRobotNotifyResponse result = new DdCustomRobotNotifyResponse();
            result.setErrcode(SysConstant.SYS_ERROR_CODE);
            result.setMsg(exp.getMessage());
            return result;
        }
        if (response.getErrcode() != 0) {
            response.setCode(40000);
            response.setMsg("通知服务返回错误");
        }
        return response;
    }


    private static String composeUrl(DdCustomRobotConfig config) {
        String sign = null;
        long timestamp = System.currentTimeMillis();
        if (Boolean.TRUE.equals(config.getNeedSign()) && StringUtils.isNotEmpty(config.getSecret())) {
            sign = createSign(timestamp, config.getSecret());
        }
        Map<String, Object> params = new HashMap<>(3);
        params.put("access_token", config.getAccessToken());
        params.put("timestamp", timestamp);
        params.put("sign", sign);

        return CommonUtil.composeUrlByUriParams(config.getSendApi(), params);
    }

    private static String createSign(long timestamp, String secret) {
        String stringToSign = timestamp + "\n" + secret;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8.name()));
            return EncoderUtil.urlEncode(new String(Base64.encodeBase64(signData))
                    , StandardCharsets.UTF_8.name());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {

            LOGGER.error("ding ding custom robot create sign fail,timestamp {},secret {} ", timestamp, secret);
            throw new AppException(SysConstant.SYS_ERROR_CODE, "ding ding custom robot create sign fail,", e);
        }
    }
}
