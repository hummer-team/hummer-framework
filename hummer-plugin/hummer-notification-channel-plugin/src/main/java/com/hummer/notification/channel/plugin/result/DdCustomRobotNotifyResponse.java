package com.hummer.notification.channel.plugin.result;

import lombok.Getter;
import lombok.Setter;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/15 17:45
 */
@Getter
@Setter
public class DdCustomRobotNotifyResponse extends NotificationResponse<DdCustomRobotNotifyResponse> {

    private int errcode;

    private String errmsg;
}
