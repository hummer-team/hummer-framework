package com.hummer.notification.channel.plugin.context;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * NotifyBaseContext
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/15 11:36
 */
@Setter
@Getter
public class NotifyBaseContext<T extends NotifyBaseContext<T>> {

    @JSONField(serialize = false)
    private T context;

}
