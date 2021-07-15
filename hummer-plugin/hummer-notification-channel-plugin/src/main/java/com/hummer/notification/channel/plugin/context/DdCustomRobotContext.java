package com.hummer.notification.channel.plugin.context;

import com.alibaba.fastjson.annotation.JSONField;
import com.hummer.notification.channel.plugin.enums.DdCustomRobotEnums;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * DdCustomRobotContext
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/15 11:37
 */
@Getter
@Setter
public class DdCustomRobotContext extends NotifyBaseContext<DdCustomRobotContext> {

    @NotNull
    @JSONField(name = "msgtype")
    private DdCustomRobotEnums.ContentType msgType;

    protected void setMsgType(DdCustomRobotEnums.ContentType msgType) {

        this.msgType = msgType;
    }
}
