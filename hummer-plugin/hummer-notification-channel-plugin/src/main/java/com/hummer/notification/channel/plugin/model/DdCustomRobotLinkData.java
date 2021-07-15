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
public class DdCustomRobotLinkData {

    /**
     * 消息标题。
     */
    @NotNull
    private String title;

    /**
     * 消息内容。如果太长只会部分展示。
     */
    @NotNull
    private String text;

    /**
     * 点击消息跳转的URL。
     */
    @NotNull
    private String messageUrl;

    /**
     * 图片URL。
     */
    private String picUrl;
}
