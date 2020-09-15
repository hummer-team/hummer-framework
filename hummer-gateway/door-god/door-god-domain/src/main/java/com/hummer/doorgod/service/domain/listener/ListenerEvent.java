package com.hummer.doorgod.service.domain.listener;

import com.alibaba.nacos.api.config.listener.Listener;
import com.hummer.doorgod.service.domain.configuration.SentinelConfig;
import lombok.Builder;
import lombok.Data;

/**
 * @author edz
 */
@Data
@Builder
public class ListenerEvent {
    private String dataId;
    private String groupId;
    private Listener listener;
    private SentinelConfig sentinelConfig;
}
