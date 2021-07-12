package com.hummer.message.facade.metadata;


import com.hummer.common.utils.FunctionUtil;
import com.hummer.core.PropertiesContainer;
import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static com.hummer.message.facade.metadata.MessagePublishMetadataKey.MESSAGE_PREFIX_KEY;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/2 15:47
 **/
@Getter
public class MessagePublishMetadata {
    private static final ConcurrentHashMap<String, Object> CACHE = new ConcurrentHashMap<>(2);


    private String topicId;
    private int perSecondSemaphore;
    private boolean enable;
    private int retryCount;
    private int sendMessageTimeOutMills;
    private String serializerType;
    private String driverType;

    public MessagePublishMetadata() {

    }

    @SuppressWarnings("unchecked")
    protected static <T extends MessagePublishMetadata> T get(final String topicId
            , final Supplier<T> supplier) {
        T metadata = (T) CACHE.putIfAbsent(topicId, supplier.get());
        if (metadata == null) {
            return (T) CACHE.get(topicId);
        }

        return metadata;
    }

    protected static String formatKey(final String topicId
            , final String key
            , final String messageDriverType) {
        return String.format("%s.%s.%s.producer.%s", MESSAGE_PREFIX_KEY, messageDriverType, topicId, key);
    }

    protected static String formatKeyByDefault(final String key
            , final String messageDriverType) {
        return String.format("%s.%s.producer.%s", MESSAGE_PREFIX_KEY, messageDriverType, key);
    }

    protected void builder(final String topicId) {
        this.topicId = topicId;

        final String driverType = PropertiesContainer.valueOfString("hummer.message.driver.type"
                , "kafka");
        this.driverType = driverType;
        this.enable = FunctionUtil.with(
                () -> PropertiesContainer.valueOf(formatKey(this.topicId, "enable", driverType), Boolean.class)
                , Objects::nonNull
                , () -> PropertiesContainer.valueOf(formatKeyByDefault("enable", driverType), Boolean.class
                        , Boolean.TRUE));

        //if current app id message disabled then break builder flow
        if (!this.enable) {
            return;
        }

        this.perSecondSemaphore = FunctionUtil.with(
                () -> PropertiesContainer.valueOfInteger(formatKey(this.topicId
                        , "perSecondSemaphore"
                        , driverType))
                , r -> r > 0
                , () -> PropertiesContainer.valueOfInteger(formatKeyByDefault("perSecondSemaphore", driverType)));

        this.sendMessageTimeOutMills = FunctionUtil.with(
                () -> PropertiesContainer.valueOfInteger(formatKey(this.topicId
                        , "sendMessageTimeOutMills", driverType))
                , r -> r > 0
                , () -> PropertiesContainer.valueOfInteger(formatKeyByDefault("sendMessageTimeOutMills"
                        , driverType)));

        this.retryCount = FunctionUtil.with(
                () -> PropertiesContainer.valueOfInteger(formatKey(this.topicId, "retryCount"
                        , driverType))
                , r -> r > 0
                , () -> PropertiesContainer.valueOfInteger(formatKeyByDefault("retryCount"
                        , driverType)));

        this.serializerType = PropertiesContainer.valueOfString(formatKeyByDefault("producer.value.serializer"
                , driverType)
                , "fastjson");


    }
}
