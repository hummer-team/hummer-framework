package com.hummer.notification.channel.plugin.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * DdCustomRobotAtData
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/15 14:10
 */
@Data
public class DdCustomRobotTextData {

    /**
     * 消息内容。
     */
    @NotNull
    private String content;
}
