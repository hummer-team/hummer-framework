package com.hummer.user.plugin.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * description java类作用描述
 *
 * @author chen wei
 * @version 1.0
 *          <p>
 *          Copyright: Copyright (c) 2020
 *          </p>
 * @date 2020/6/24 18:38
 */
@Data
public class UserBasicInfoPluginRespDto {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("用户登录名")
    private String trueName;

    @ApiModelProperty("用户昵称")
    private String nickName;
}
