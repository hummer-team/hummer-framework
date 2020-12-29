package com.hummer.yug.tools.plugin.model.bo;

import lombok.Data;

import java.util.List;

/**
 * GoodsSkuInfoBo
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/7 14:38
 */
@Data
public class GoodsSkuInfoBo {

    private Long ygfGoodsSpuId;

    private Long ygfGoodsSkuId;

    private List<GoodsSkuSpecBo> specBos;

    private String propPath;

    private Double goodsSellPrice;

    private Double activityPrice;

    private Integer storeNum;

    private Integer cartGoodsNum;

    private Boolean soldOut;

    private String propPathDesc;

    private Integer skuSellNum;

    private Boolean isDefault;

    private String skuSpecValue;
}
