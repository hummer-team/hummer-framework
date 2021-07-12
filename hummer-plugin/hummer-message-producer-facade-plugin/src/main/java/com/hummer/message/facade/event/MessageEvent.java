package com.hummer.message.facade.event;

import com.alibaba.fastjson.JSON;
import com.hummer.common.utils.DateUtil;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @author edz
 */
@Data
public class MessageEvent {
    /**
     * message body
     */
    private Object body;
    /**
     * business unique id
     */
    private String topicId;
    /**
     * message partition
     */
    private Integer partition;
    /**
     * message driver data
     */
    private Map<String, String> messageAffiliateData;
    /**
     * message key
     */
    private String messageKey;
    /**
     * send to message bocker timeout
     */
    private long syncSendMessageTimeOutMills;
    /**
     * message created time
     */
    private Date createdTime;
    /**
     * expire on datetime
     */
    private Date expireDateTime;
    /**
     * retry count
     */
    private int retryCount;
    /**
     * last retry datetime
     */
    private Date lastRetryDatetime;
    /**
     * if true  then async send to message bocker else sync send to message bocker
     */
    private boolean async;
    /**
     * max retry limit
     */
    private int maxRetry;
    /**
     * message bocker type
     */
    private String busDriverType;
    private String tag;
    private boolean ack;
    private int delayLevel;

    public static MessageEvent parseBytes(byte[] body) {
        return JSON.parseObject(body, MessageEvent.class);
    }

    public byte[] toBytes() {
        return JSON.toJSONBytes(this);
    }

    public void updateRetry() {
        retryCount = retryCount + 1;
        lastRetryDatetime = DateUtil.now();
    }
}
