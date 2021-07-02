package com.hummer.message.facade.metadata;


import com.hummer.common.utils.FunctionUtil;
import com.hummer.core.PropertiesContainer;
import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/2 15:47
 **/
@Getter
public class MessagePublishMetadata {
    private static final ConcurrentHashMap<String, Object> CACHE = new ConcurrentHashMap<>(2);
    private static final String MESSAGE_PREFIX_KEY = "hummer.message";

    private String appId;
    private int perSecondSemaphore;
    private boolean enable;
    private int retryCount;
    private int sendMessageTimeOutMills;

    public MessagePublishMetadata() {

    }

    @SuppressWarnings("unchecked")
    protected static <T extends MessagePublishMetadata> T get(final String appId
            , final Supplier<T> supplier) {
        T metadata = (T) CACHE.putIfAbsent(appId, supplier.get());
        if (metadata == null) {
            return (T) CACHE.get(appId);
        }

        return metadata;
    }

    protected static String formatKey(final String appId
            , final String key
            , final String messageDriverType) {
        return String.format("%s.%s.%s.%s", MESSAGE_PREFIX_KEY, messageDriverType, appId, key);
    }

    protected static String formatKeyByDefault(final String key
            , final String messageDriverType) {
        return String.format("%s.%s.default.%s", MESSAGE_PREFIX_KEY, messageDriverType, key);
    }

    protected void builder(final String appId) {
        this.appId = appId;

        final String driverType = PropertiesContainer.valueOfString("hummer.message.driver.type"
                , "kafka");

        this.enable = FunctionUtil.with(
                () -> PropertiesContainer.valueOf(formatKey(this.appId, "enable", driverType), Boolean.class)
                , Objects::nonNull
                , () -> PropertiesContainer.valueOf(formatKeyByDefault("enable", driverType), Boolean.class
                        , Boolean.TRUE));

        //if current app id message disabled then break builder flow
        if (!this.enable) {
            return;
        }

        this.perSecondSemaphore = FunctionUtil.with(
                () -> PropertiesContainer.valueOfInteger(formatKey(this.appId
                        , "perSecondSemaphore"
                        , driverType))
                , r -> r > 0
                , () -> PropertiesContainer.valueOfInteger(formatKeyByDefault("perSecondSemaphore", driverType)));

        this.sendMessageTimeOutMills = FunctionUtil.with(
                () -> PropertiesContainer.valueOfInteger(formatKey(this.appId
                        , "sendMessageTimeOutMills", driverType))
                , r -> r > 0
                , () -> PropertiesContainer.valueOfInteger(formatKeyByDefault("sendMessageTimeOutMills"
                        , driverType)));

        this.retryCount = FunctionUtil.with(
                () -> PropertiesContainer.valueOfInteger(formatKey(this.appId, "retryCount"
                        , driverType))
                , r -> r > 0
                , () -> PropertiesContainer.valueOfInteger(formatKeyByDefault("retryCount"
                        , driverType)));
    }
}
