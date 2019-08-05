package com.hummer.message.facade.metadata;


import com.hummer.message.facade.publish.PublishMessageExceptionCallback;
import lombok.Getter;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/2 15:47
 **/
@Getter
public class MessagePublishMetadata {
    private String appId;
    private int perSecondSemaphore;
    private String address;
    private PublishMessageExceptionCallback callback;

    public MessagePublishMetadata(String appId
            , int perSecondSemaphore
            , String address
            , PublishMessageExceptionCallback callback) {
        this.appId = appId;
        this.perSecondSemaphore = perSecondSemaphore;
        this.address = address;
        this.callback = callback;
    }
}
