package com.hummer.yug.tools.plugin.model.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class GoodsSkuPo {
    @ApiModelProperty("商品skuID")
    private Long ygfGoodsSkuId;

    @ApiModelProperty("商品SPUID")
    private Long ygfGoodsSpuId;

    @ApiModelProperty("是否是赠品")
    private Integer isGift;

    @ApiModelProperty("商品是否删除")
    private Boolean isDel;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品副标题")
    private String subhead;

    @ApiModelProperty("商品货号")
    private String goodsNo;

    @ApiModelProperty("商品条形码")
    private String goodsCode;

    @ApiModelProperty("商品主图地址")
    private String goodsSkuImage;

    @ApiModelProperty("商品愚公坊售价")
    private BigDecimal goodsSellPrice;

    @ApiModelProperty("商品APP售价")
    private BigDecimal goodsAppPrice;

    @ApiModelProperty("商品微信售价")
    private BigDecimal goodsWxPrice;

    @ApiModelProperty("商品市场价")
    private BigDecimal goodsMarketPrice;

    @ApiModelProperty("商品成本价")
    private BigDecimal goodsCostPrice;

    @ApiModelProperty("库存预警数量")
    private Integer warnNum;

    @ApiModelProperty("商品库存数量")
    private Integer storeNum;

    @ApiModelProperty("商品sku销量")
    private Integer skuSellNum;

    @ApiModelProperty("是否限购")
    private Integer isLimit;

    @ApiModelProperty("是否预售")
    private Integer isPresell;

    @ApiModelProperty("商品生产日期")
    private Date produceDate;

    @ApiModelProperty("商品保质期")
    private Integer keepDate;

    @ApiModelProperty("点击量")
    private Integer clickNum;

    @ApiModelProperty("商品重量")
    private BigDecimal goodsWeight;

    @ApiModelProperty("商品体积")
    private BigDecimal goodsVolume;

    @ApiModelProperty("是否使用SKU图片")
    private Boolean skuImageUse;

    @ApiModelProperty("是否是默认SKU（前商品默认展示的SKU）")
    private Boolean isDefault;

    @ApiModelProperty("商品供应商ID")
    private Long ygfSupplyId;

    @ApiModelProperty("商品关键字")
    private String keyWord;

    @ApiModelProperty("商品详情")
    private String detail;

    @ApiModelProperty("商品手机端详情")
    private String mobileDetail;

    @ApiModelProperty("sku规格值（JSONArray格式 [{'specId':ygfSpecId,'specName':specName,'specValueId',specValueId,'specValueName',specValueName},{}]）。ygfSpecId：规格ID。 specName：规格名称。specValueId：规格值ID。 specValueName：规格值名称。")
    private String skuSpecValue;

    @ApiModelProperty("商品类型(1、计件，2、计重)")
    private Integer goodsType;

    @ApiModelProperty("商品类型单位)")
    private String goodsUnit;
}