package com.hummer.notification.channel.plugin.config;

import lombok.Getter;
import lombok.Setter;

/**
 * NotifyBaseConfig
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/15 11:25
 */
@Getter
@Setter
public class NotifyBaseConfig<T extends NotifyBaseConfig<T>> {

    private T data;

}
