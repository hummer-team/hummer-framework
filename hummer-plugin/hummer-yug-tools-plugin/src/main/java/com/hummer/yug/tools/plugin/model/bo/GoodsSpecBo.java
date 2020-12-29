package com.hummer.yug.tools.plugin.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * description 商品参数
 *
 * @author chen wei
 * @version 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 * </p>
 * @date 2020/3/4 14:19
 */
@ApiModel
@Data
public class GoodsSpecBo {

    @ApiModelProperty("规格ID")
    private Long id;

    @ApiModelProperty("规格名称")
    private String name;

    @ApiModelProperty("规格值选项")
    private List<GoodsSpecValueBo> values = new ArrayList<>();
}
