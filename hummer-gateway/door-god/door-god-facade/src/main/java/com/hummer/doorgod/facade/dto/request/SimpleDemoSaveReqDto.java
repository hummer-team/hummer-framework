package com.hummer.doorgod.facade.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;

/**
 * @author edz
 */
@Data
public class SimpleDemoSaveReqDto {
    @NotEmpty(message = "third Party Delivery value can't empty.")
    @ApiModelProperty(required = true)
    private String thirdPartyDeliveryId;

    @NotEmpty(message = "third Party Delivery code value can't empty.")
    @ApiModelProperty(required = true)
    @Length(max = 100,message = "max length is 100 char")
    private String deliveryCompanyCode;

    @NotEmpty(message = "batch id can't empty.")
    @Length(max = 200, message = "max length is 200 char")
    private String batchId;
    private String buyerId;
    private String createdUserId;
    @ApiModelProperty(notes = "物流类型，0:国内，1:番丽，2:国际")
    @Range(max = 2, min = 1, message = "valid value is 0 or 1 or 2")
    private Integer deliveryType;
    private String deliveryTypeDescribe;
}
