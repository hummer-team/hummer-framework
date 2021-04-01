package com.hummer.yug.user.plugin.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * ShopInfoRespDto
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/12/23 16:57
 */
@Data
public class ShopInfoRespDto {

    @ApiModelProperty("自增主键")
    private Integer id;

    @ApiModelProperty("门店管理员用户ID")
    private Long shopManagerUserId;

    @ApiModelProperty("门店类型（1、自营）")
    private Integer shopType;

    @ApiModelProperty("门店编号")
    private String shopCode;

    @ApiModelProperty("门店名称")
    private String shopName;

    @ApiModelProperty("门店详细地址")
    private String shopAddress;

    @ApiModelProperty("门店地址省ID")
    private Long shopProvinceId;

    @ApiModelProperty("门店地址市ID")
    private Long shopCityId;

    @ApiModelProperty("门店地址县ID")
    private Long shopCountyId;

    @ApiModelProperty("门店管理员姓名")
    private String shopManagerName;

    @ApiModelProperty("门店管理员联系电话")
    private String shopManagerPhone;

    @ApiModelProperty("开启/禁用")
    private Boolean isEnable;

    @ApiModelProperty("创建时间")
    private Date createdDateTime;

    @ApiModelProperty("创建人")
    private Long createdUserId;

    @ApiModelProperty("最新修改时间")
    private Date lastModifiedDateTime;

    @ApiModelProperty("最新修改人")
    private Long lastModifiedUserId;

    private String warehouseCode;
}
