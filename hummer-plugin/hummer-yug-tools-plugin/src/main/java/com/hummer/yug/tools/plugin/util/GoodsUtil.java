package com.hummer.yug.tools.plugin.util;

import com.alibaba.fastjson.JSONArray;
import com.hummer.common.utils.DateUtil;
import com.hummer.common.utils.ObjectCopyUtils;
import com.hummer.yug.tools.plugin.model.bo.GoodsSkuInfoBo;
import com.hummer.yug.tools.plugin.model.bo.GoodsSkuSpecBo;
import com.hummer.yug.tools.plugin.model.bo.GoodsSpecBo;
import com.hummer.yug.tools.plugin.model.bo.GoodsSpecValueBo;
import com.hummer.yug.tools.plugin.model.bo.GoodsSpuInfoBo;
import com.hummer.yug.tools.plugin.model.po.GoodsSkuPo;
import com.hummer.yug.tools.plugin.model.po.GoodsSpuPo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GoodsUtil
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/12/29 11:09
 */
public class GoodsUtil {

    public static List<? extends GoodsSpuInfoBo> composeGroupGoodsInfoBo(List<? extends GoodsSpuPo> goodsPos
            , List<? extends GoodsSkuPo> skuPos, SysEnums.ClientResourceEnum resourceEnum
            , Class<? extends GoodsSkuInfoBo> skuInfoClass, Class<? extends GoodsSpuInfoBo> spuInfoClass) {
        if (CollectionUtils.isEmpty(skuPos) || CollectionUtils.isEmpty(goodsPos)) {
            return Collections.emptyList();
        }
        List<GoodsSpuInfoBo> goodsBos = goodsPos.stream()
                .map(item -> ObjectCopyUtils.copy(item, spuInfoClass)).collect(Collectors.toList());

        goodsBos.forEach(goods -> {
            goods.setSoldOut(goods.getStoreNum() != null && goods.getStoreNum() <= 0);
            List<GoodsSkuInfoBo> skuInfoBos = new ArrayList<>();
            skuPos.forEach(sku -> {
                if (sku.getYgfGoodsSpuId().equals(goods.getYgfGoodsSpuId())) {
                    skuInfoBos.add(composeGoodsSkuInfoBo(sku, resourceEnum, skuInfoClass));
                }
            });
            // 商品规格处理
            goods.setSpecProperties(composeGoodsSpec(skuInfoBos));
            // 商品SKU
            goods.setSkuList(ObjectCopyUtils.copyByList(skuInfoBos, GoodsSkuInfoBo.class));
        });
        return goodsBos.stream().filter(item -> !CollectionUtils.isEmpty(item.getSkuList()))
                .collect(Collectors.toList());
    }

    public static List<? extends GoodsSkuInfoBo> composeGoodsSkuInfoBos(List<? extends GoodsSkuPo> skuPos
            , SysEnums.ClientResourceEnum resourceEnum, Class<? extends GoodsSkuInfoBo> cla) {
        if (CollectionUtils.isEmpty(skuPos)) {
            return Collections.emptyList();
        }
        return skuPos.stream().map(item -> composeGoodsSkuInfoBo(item, resourceEnum, cla))
                .collect(Collectors.toList());
    }

    public static GoodsSkuInfoBo composeGoodsSkuInfoBo(GoodsSkuPo skuPo
            , SysEnums.ClientResourceEnum resourceEnum, Class<? extends GoodsSkuInfoBo> cla) {
        GoodsSkuInfoBo skuInfoBo = ObjectCopyUtils.copy(skuPo, cla);
        skuInfoBo.setSoldOut(skuInfoBo.getStoreNum() == null || skuInfoBo.getStoreNum() <= 0);
        skuInfoBo.setGoodsSellPrice(getGoodsClientSellPrice(skuPo.getGoodsSellPrice(), skuPo.getGoodsWxPrice()
                , skuPo.getGoodsAppPrice(), resourceEnum));
        skuInfoBo.setSpecBos(JSONArray.parseArray(skuPo.getSkuSpecValue(), GoodsSkuSpecBo.class));
        List<GoodsSkuSpecBo> skuSpecBos = skuInfoBo.getSpecBos();
        skuInfoBo.setPropPathDesc(composeGoodsSpecDesc(skuSpecBos));
        String propPath = null;
        if (!CollectionUtils.isEmpty(skuSpecBos)) {
            for (GoodsSkuSpecBo skuSpecBo : skuSpecBos) {
                if (propPath == null) {
                    propPath = skuSpecBo.getSpecId() + ":" + skuSpecBo.getSpecValueId();
                } else {
                    propPath = propPath + "," + skuSpecBo.getSpecId() + ":" + skuSpecBo.getSpecValueId();
                }
            }
        }
        skuInfoBo.setPropPath(propPath);
        return skuInfoBo;
    }

    public static List<GoodsSpecBo> composeGoodsSpec(List<GoodsSkuInfoBo> skuInfoBos) {
        if (CollectionUtils.isEmpty(skuInfoBos)) {
            return Collections.emptyList();
        }
        List<GoodsSpecBo> specs = new ArrayList<>();
        for (GoodsSkuInfoBo sku : skuInfoBos) {
            if (CollectionUtils.isEmpty(sku.getSpecBos())) {
                return Collections.emptyList();
            }
            List<GoodsSkuSpecBo> skuSpecBos = sku.getSpecBos();
            for (GoodsSkuSpecBo skuSpecBo : skuSpecBos) {

                GoodsSpecBo specRespDto = specs.isEmpty() ? null : specs.stream()
                        .filter(item -> item.getId().equals(skuSpecBo.getSpecId()))
                        .findFirst()
                        .orElse(null);
                if (specRespDto == null) {
                    specRespDto = new GoodsSpecBo();
                    specRespDto.setId(skuSpecBo.getSpecId());
                    specRespDto.setName(skuSpecBo.getSpecName());
                    specs.add(specRespDto);
                }
                if (CollectionUtils.isEmpty(specRespDto.getValues()) ||
                        specRespDto.getValues().stream()
                                .noneMatch(specValue -> specValue.getId().equals(skuSpecBo.getSpecValueId()))
                ) {
                    GoodsSpecValueBo specValue = new GoodsSpecValueBo();
                    specValue.setId(skuSpecBo.getSpecValueId());
                    specValue.setName(skuSpecBo.getSpecValueName());
                    specRespDto.getValues().add(specValue);
                }
            }

        }
        return specs;
    }


    /**
     * 获取商品客户端售价
     *
     * @return java.lang.Double
     * @author chen wei
     * @date 2020/8/7
     */
    public static BigDecimal getGoodsClientSellPrice(BigDecimal goodsSellPrice, BigDecimal goodsWxPrice
            , BigDecimal goodsAppPrice, SysEnums.ClientResourceEnum resourceEnum) {
        if (resourceEnum == null) {
            return goodsSellPrice;
        }
        switch (resourceEnum) {
            case H5:
                return goodsWxPrice != null ? goodsWxPrice : goodsSellPrice;
            case ANDROID:
            case IOS:
                return goodsAppPrice != null ? goodsAppPrice : goodsSellPrice;
            default:
                return goodsSellPrice;
        }
    }


    public static String createCodeByTime(String prefix, int randNum) {
        String timeStr = DateUtil.formatNowDate("yyyyMMddHHmmss");
        return prefix + timeStr + getRandomNumber(0, (int) Math.sqrt(randNum) - 1);
    }

    public static int getRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    private static final String SPEC_DESC_SEPARATOR = " ";

    public static String composeGoodsSpecDesc(List<GoodsSkuSpecBo> skuSpecBos) {
        if (CollectionUtils.isEmpty(skuSpecBos)) {
            return null;
        }
        StringBuilder desc = null;
        for (GoodsSkuSpecBo item : skuSpecBos) {
            if (desc == null) {
                desc = new StringBuilder(item.getSpecValueName());
            } else {
                desc.append(SPEC_DESC_SEPARATOR).append(item.getSpecValueName());
            }
        }
        return desc == null ? null : desc.toString();
    }

    public static String composeGoodsSpecDesc(String skuSpecInfo) {
        if (StringUtils.isEmpty(skuSpecInfo)) {
            return null;
        }
        return composeGoodsSpecDesc(JSONArray.parseArray(skuSpecInfo, GoodsSkuSpecBo.class));
    }


    public static boolean parsingGoodsStatus(Integer goodsStatus, Integer goodsIsPass) {

        return goodsStatus == 1 && goodsIsPass == 2;
    }


}
