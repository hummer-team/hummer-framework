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

    private String namespaceId;
    private int perSecondSemaphore;
    private boolean enable;
    private int retryCount;
    private int sendMessageTimeOutMills;

    public MessagePublishMetadata() {

    }

    @SuppressWarnings("unchecked")
    protected static <T extends MessagePublishMetadata> T get(final String appId
            , final Supplier<T> supplier) {
        T metadata = (T)CACHE.putIfAbsent(appId, supplier.get());
        if (metadata == null) {
            return (T)CACHE.get(appId);
        }

        return metadata;
    }

    protected void builder(final String namespaceId) {
        this.namespaceId = namespaceId;
        this.enable = FunctionUtil.with(
                () -> PropertiesContainer.valueOf(formatKey(namespaceId, "enable"), Boolean.class)
                , Objects::nonNull
                , () -> PropertiesContainer.valueOf(formatKeyByDefault("enable"), Boolean.class, Boolean.TRUE));

        //if current app id message disabled then break builder flow
        if (!this.enable) {
            return;
        }

        this.perSecondSemaphore = FunctionUtil.with(
                () -> PropertiesContainer.valueOfInteger(formatKey(namespaceId, "perSecondSemaphore"))
                , r -> r > 0
                , () -> PropertiesContainer.valueOfInteger(formatKeyByDefault("perSecondSemaphore")));

        this.sendMessageTimeOutMills = FunctionUtil.with(
                () -> PropertiesContainer.valueOfInteger(formatKey(namespaceId, "sendMessageTimeOutMills"))
                , r -> r > 0
                , () -> PropertiesContainer.valueOfInteger(formatKeyByDefault("sendMessageTimeOutMills")));

        this.retryCount = FunctionUtil.with(
                () -> PropertiesContainer.valueOfInteger(formatKey(namespaceId, "retryCount"))
                , r -> r > 0
                , () -> PropertiesContainer.valueOfInteger(formatKeyByDefault("retryCount")));
    }

    protected static String formatKey(final String appId, final String key) {
        return String.format("hummer.message.%s.%s", appId, key);
    }

    protected static String formatKeyByDefault(final String key) {
        return String.format("hummer.message.default.%s", key);
    }
}
