package com.hummer.yug.user.plugin.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ShopInfoRespDto
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/12/23 16:57
 */
@Data
public class ShopInfoReqDto {

    @ApiModelProperty("自增主键")
    private Integer id;

    @ApiModelProperty("门店管理员用户ID")
    private Long shopManagerUserId;

    @ApiModelProperty("门店类型（1、自营）")
    private Integer shopType;

    @ApiModelProperty("门店编号")
    private String shopCode;

    @ApiModelProperty("开启/禁用")
    private Boolean isEnable;

    @ApiModelProperty("关键字")
    private String keyword;
}
