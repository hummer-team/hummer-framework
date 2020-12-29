package com.hummer.yug.tools.plugin.model.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class GoodsSpuPo {
    @ApiModelProperty("商品SPUID")
    private Long ygfGoodsSpuId;

    @ApiModelProperty("是否是赠品")
    private Boolean isGift;

    @ApiModelProperty("商品是否被删除")
    private Boolean isDel;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品货号")
    private String goodsNo;

    @ApiModelProperty("商品副标题")
    private String subhead;

    @ApiModelProperty("是否使用活动标题")
    private Boolean useActive;

    @ApiModelProperty("商品品牌ID")
    private Long brandId;

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

    @ApiModelProperty("'商品上架状态： 1-上架 2-下架 3-未处理'")
    private Boolean goodsStatus;

    @ApiModelProperty("'商品审核状态：1-未审核 2-审核通过  3-审核不通过'")
    private Boolean goodsIsPass;

    @ApiModelProperty("审核人id")
    private Long confirmPersonId;

    @ApiModelProperty("商品审核时间")
    private Date goodsPassTime;

    @ApiModelProperty("审核不通过原因")
    private String passReason;

    @ApiModelProperty("商品下架人ID")
    private Long offShelfPerson;

    @ApiModelProperty("商品下架理由")
    private String offShelfReason;

    @ApiModelProperty("商品添加时间")
    private Date goodsAddTime;

    @ApiModelProperty("商品最新修改时间")
    private Date goodsUpdateTime;

    @ApiModelProperty("商品最新修改人ID")
    private Long updateUserId;

    @ApiModelProperty("是否是特殊商品（特殊商品不参与单品包邮外的其他包邮活动）")
    private Boolean freightSpecial;

    @ApiModelProperty("单品包邮方式（0、不支持单品包邮，1、满件包邮，2、满金额包邮）")
    private Boolean noFreightType;

    @ApiModelProperty("单品满件包邮件数")
    private Integer noFreightNum;

    @ApiModelProperty("单品满金额包邮金额")
    private BigDecimal noFreightMoney;

    @ApiModelProperty("不参与单品满包邮地区ID（区ID英文逗号隔开）")
    private String goodsFreightAreaId;

    @ApiModelProperty("运费模式 （1、运费模板，2、统一运费） ")
    private Boolean freightType;

    @ApiModelProperty("固定运费金额")
    private BigDecimal freightMoney;

    @ApiModelProperty("运费模板ID")
    private Long ygfFreightTempId;

    @ApiModelProperty("销售区域限制方式（0、不限制区域，1、可销售区域，2、不可销售区域）。")
    private Boolean saleAreaType;

    @ApiModelProperty("是否支持货到付款")
    private Boolean isCashOnDeliver;

    @ApiModelProperty("是否支持发票")
    private Boolean isInvoice;

    @ApiModelProperty("发票种类（type,type,.....）。ype：1,、个人发票，2、单位发票，3、增值税发票")
    private String invoiceKinds;

    @ApiModelProperty("是否新品")
    private Boolean isNew;

    @ApiModelProperty("是否优品")
    private Boolean isExcellent;

    @ApiModelProperty("是否热卖品")
    private Boolean isHot;

    @ApiModelProperty("是否愚公坊品牌")
    private Boolean isYgf;

    @ApiModelProperty("是否展示在首页")
    private Boolean isShowhome;

    @ApiModelProperty("是否是坊主推荐")
    private Boolean isShowclass;

    @ApiModelProperty("默认自动收货时间")
    private Integer autoReceiveDays;

    @ApiModelProperty("商品库存数量")
    private Integer storeNum;

    @ApiModelProperty("库存预警数量")
    private Integer warnNum;

    @ApiModelProperty("商品生产日期")
    private Date produceDate;

    @ApiModelProperty("商品保质期")
    private Integer goodsShelf;

    @ApiModelProperty("商品主图地址")
    private String goodsSpuImage;

    @ApiModelProperty("商品推荐指数")
    private Integer goodsScore;

    @ApiModelProperty("推荐理由1")
    private String recommendReason1;

    @ApiModelProperty("推荐理由2")
    private String recommendReason2;

    @ApiModelProperty("推荐理由3")
    private String recommendReason3;

    @ApiModelProperty("商品分享标题")
    private String shareTitle;

    @ApiModelProperty("商品分享图标")
    private String shareLogo;

    @ApiModelProperty("是否限时卖")
    private Boolean isTimeLimit;

    @ApiModelProperty("商品限时卖价格")
    private BigDecimal limitTimePrice;

    @ApiModelProperty("限时卖开始时间")
    private Date limitTimeStart;

    @ApiModelProperty("限时卖结束时间")
    private Date limitTimeEnd;

    @ApiModelProperty("单品购买量权限（JSON格式：{'numMin':value,'numMax':value,'totalNum':value}）。numMin：单次最低购买。numMax：单次最多购买。totalNum：购买总量。value：件数。")
    private String buyNumAuth;

    @ApiModelProperty("会员浏览等级权限")
    private Long browseUserLevel;

    @ApiModelProperty("会员购买等级权限")
    private Long buyUserLevel;

    @ApiModelProperty("会员组浏览权限")
    private Long browseUserGroup;

    @ApiModelProperty("会员组购买权限")
    private Long buyUserGroup;

    @ApiModelProperty("会员来源浏览权限")
    private String browseResource;

    @ApiModelProperty("会员来源购买权限")
    private String buyResource;

    @ApiModelProperty("商品spu销量")
    private Integer spuSellNum;

    @ApiModelProperty("商品重量")
    private BigDecimal goodsWeight;

    @ApiModelProperty("商品体积")
    private BigDecimal goodsVolume;

    @ApiModelProperty("商品规格标签")
    private String specTag;

    @ApiModelProperty("商品供应商ID")
    private Long ygfSupplyId;

    @ApiModelProperty("首页商品排序")
    private Integer indexSort;

    @ApiModelProperty("坊商品排序")
    private Integer classSort;

    @ApiModelProperty("是否参与分销")
    private Integer agentOn;

    @ApiModelProperty("0、无限制，1、仅详商品情页面展示")
    private Integer showLimitType;

    @ApiModelProperty("商品关键字")
    private String keyWord;

    @ApiModelProperty("可销售区域（市ID逗号分隔）")
    private String saleArea;

    @ApiModelProperty("不可销售区域（市ID逗号分隔）")
    private String noSaleArea;

    @ApiModelProperty("商品标签（JOSNArray格式：[{'name':name,'isUse':value},{'name':name,'isUse':value}]）name：便签名；isUse：标签是否勾选。")
    private String goodsTags;

    @ApiModelProperty("商品详情")
    private String detail;

    @ApiModelProperty("商品手机端详情")
    private String mobileDetail;

    @ApiModelProperty("愚公坊理念契合理由")
    private String goodsReason;

    @ApiModelProperty("商品分享描述")
    private String shareDesc;
}