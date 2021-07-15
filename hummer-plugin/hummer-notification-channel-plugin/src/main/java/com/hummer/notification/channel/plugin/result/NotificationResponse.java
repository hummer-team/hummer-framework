package com.hummer.notification.channel.plugin.result;

import lombok.Getter;
import lombok.Setter;

/**
 * NotificationResponse
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/15 17:43
 */
@Getter
@Setter
public class NotificationResponse<T extends NotificationResponse<T>> {

    private int code;

    private String msg;

    private T response;

    public boolean success() {
        return code == 0;
    }
}
