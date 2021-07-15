package com.hummer.notification.channel.plugin.config;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * DdCustomRobotConfig
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/15 11:28
 */
@Getter
@Setter
public class DdCustomRobotConfig extends NotifyBaseConfig<DdCustomRobotConfig> {

    private String secret;

    private Boolean needSign;

    @NotNull
    private String sendApi;

    @NotNull
    private String accessToken;

    private Long timeOutMillions;

    private Integer retry;

}
