package com.hummer.yug.tools.plugin.model.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * GoodsSearchPo
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/12/10 10:38
 */
@Data
public class GoodsSearchPo {

    @ApiModelProperty("商品关键字")
    private String keyword;

    @ApiModelProperty("商品ID集")
    private List<Long> goodsIds;

    @ApiModelProperty("商品顶级分类ID集")
    private List<Long> categoryIds;

    @ApiModelProperty("商品次级分类ID集")
    private List<Long> category2Ids;

    @ApiModelProperty("商品三级分类ID集")
    private List<Long> category3Ids;

    @ApiModelProperty("商品品牌ID集")
    private List<Long> brandIds;

    @ApiModelProperty("不包含商品ID集")
    private List<Long> excludeGoodsIds;

    @ApiModelProperty("不包含商品一级分类ID集")
    private List<Long> excludeCategoryIds;

    @ApiModelProperty("不包含商品二级分类ID集")
    private List<Long> excludeCategory2Ids;

    @ApiModelProperty("不包含商品三级分类ID集")
    private List<Long> excludeCategory3Ids;

    @ApiModelProperty("不包含商品品牌ID集")
    private List<Long> excludeBrandIds;

    @ApiModelProperty("上下架状态")
    private Integer goodsStatus;

    @ApiModelProperty("审核状态")
    private Integer goodsIsPass;

    @ApiModelProperty("是否是赠品")
    private Boolean isGift;

    @ApiModelProperty("展示限制")
    private Integer showLimitType;

    private boolean withCategory;
}
