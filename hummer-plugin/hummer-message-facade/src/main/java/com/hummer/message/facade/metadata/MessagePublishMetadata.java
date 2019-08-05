package com.hummer.message.facade.metadata;


import com.google.common.base.Strings;
import com.hummer.common.utils.SupplierUtil;
import com.hummer.message.facade.publish.PublishMessageExceptionCallback;
import com.hummer.spring.plugin.context.PropertiesContainer;
import lombok.Getter;

import java.util.Objects;
import java.util.function.Supplier;

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
    private boolean enable;

    public MessagePublishMetadata(String appId) {
        this.appId = appId;
        this.enable = SupplierUtil.with(
                () -> PropertiesContainer.valueOf(formatKey(appId, "enable"), Boolean.class)
                , Objects::nonNull
                , () -> PropertiesContainer.valueOf(formatKeyByDefault("enable"), Boolean.class, Boolean.TRUE));

        this.address = SupplierUtil.with(
                () -> PropertiesContainer.valueOfString(formatKey(appId, "address"))
                , r -> !Strings.isNullOrEmpty(r)
                , () -> PropertiesContainer.valueOfStringWithAssertNotNull(formatKeyByDefault("address")));

        this.perSecondSemaphore = SupplierUtil.with(
                () -> PropertiesContainer.valueOfInteger(formatKey(appId, "perSecondSemaphore"))
                , r -> r > 0
                , () -> PropertiesContainer.valueOfInteger(formatKeyByDefault("perSecondSemaphore")));

        this.callback = SupplierUtil.with(
                () -> PropertiesContainer.valueOf(formatKey(appId, "callback")
                        , PublishMessageExceptionCallback.class
                        , null)
                , Objects::nonNull
                , () -> PropertiesContainer.valueOf(formatKeyByDefault("callback")
                        , PublishMessageExceptionCallback.class, null));
    }

    protected String formatKey(final String appId, final String key) {
        return String.format("hummer.message.%s.%s", appId, key);
    }

    protected String formatKeyByDefault(final String key) {
        return String.format("hummer.message.default.%s", key);
    }
}
