package com.hummer.notification.channel.plugin.context;

import com.hummer.notification.channel.plugin.enums.DdCustomRobotEnums;
import com.hummer.notification.channel.plugin.model.DdCustomRobotLinkData;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * DdCustomRobotLinkContext
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/15 13:59
 */
@Getter
@Setter
public class DdCustomRobotLinkContext extends DdCustomRobotContext {

    @NotNull
    private DdCustomRobotLinkData link;

    public DdCustomRobotLinkContext() {
        setMsgType(DdCustomRobotEnums.ContentType.link);
    }
}
