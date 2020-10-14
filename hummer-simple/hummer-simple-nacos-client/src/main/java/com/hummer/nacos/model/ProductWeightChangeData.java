package com.hummer.nacos.model;

import lombok.Data;

/**
 * ProductWeightChangeData
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/14 13:21
 */
@Data
public class ProductWeightChangeData {

    private String productId;

    private Double weight;

    private Boolean isSensitive;
}
