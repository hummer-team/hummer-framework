package com.hummer.doorgod.service.domain.listener;

import com.alibaba.nacos.api.config.listener.Listener;
import lombok.Builder;
import lombok.Data;

/**
 * @author edz
 */
@Data
@Builder
public class ListenerEventInfo {
    private String dataId;
    private String groupId;
    private Listener listener;
}
