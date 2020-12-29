package com.hummer.yug.tools.plugin.model.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * GoodsSpuInfoBo
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/12/29 11:24
 */
@Data
public class GoodsSpuInfoBo {

    @ApiModelProperty("商品ID")
    private Long ygfGoodsSpuId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品副标题")
    private String subhead;

    @ApiModelProperty("商品主图")
    private String goodsSpuImage;

    @ApiModelProperty("商品原售价")
    private Double goodsSellPrice;

    @ApiModelProperty("商品拼团价")
    private Double activityPrice;

    @ApiModelProperty("库存数量")
    private Integer storeNum;

    @ApiModelProperty("是否售罄")
    private Boolean soldOut;

    @ApiModelProperty("商品已销售数量")
    private Integer goodsSellNum;

    @ApiModelProperty("已加入购物车数量")
    private Integer cartGoodsNum = 0;

    @ApiModelProperty("商品规格信息")
    private List<GoodsSpecBo> specProperties;

    @ApiModelProperty("商品SKU信息")
    private List<GoodsSkuInfoBo> skuList;

    @ApiModelProperty("购物车已选商品SKU信息")
    private List<GoodsSkuInfoBo> cartSkuList = new ArrayList<>();
}
