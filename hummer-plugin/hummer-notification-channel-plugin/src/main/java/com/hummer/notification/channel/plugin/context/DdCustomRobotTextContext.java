package com.hummer.notification.channel.plugin.context;

import com.hummer.notification.channel.plugin.enums.DdCustomRobotEnums;
import com.hummer.notification.channel.plugin.model.DdCustomRobotAtData;
import com.hummer.notification.channel.plugin.model.DdCustomRobotTextData;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * DdCustomRobotTextContext
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/15 13:59
 */
@Getter
@Setter
public class DdCustomRobotTextContext extends DdCustomRobotContext {

    private DdCustomRobotAtData at;

    @NotNull
    private DdCustomRobotTextData text;

    public DdCustomRobotTextContext() {
        setMsgType(DdCustomRobotEnums.ContentType.text);
    }
}
