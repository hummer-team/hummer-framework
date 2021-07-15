package com.hummer.notification.channel.plugin.model;

import lombok.Data;

import java.util.List;

/**
 * DdCustomRobotAtData
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/15 14:10
 */
@Data
public class DdCustomRobotAtData {

    /**
     * 被@人的手机号
     * 注意 在content里添加@人的手机号。
     */
    private List<String> atMobiles;
    /**
     * 被@人的用户userid。
     */
    private List<String> atUserIds;

    /**
     * 是否@所有人。
     */
    private Boolean isAtAll;
}
