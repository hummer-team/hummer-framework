package com.hummer.rest.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * ResourcePageReqDto
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/6/3 14:03
 */
@ApiModel
@Data
public class ResourcePageReqDto<T> {

    @ApiModelProperty("当前页码")
    @Min(value = 1, message = "pageNumber min 1")
    private Integer pageNumber;

    @ApiModelProperty("单页记录数")
    @Min(value = 1, message = "pageSize min 1")
    private Integer pageSize;

    @ApiModelProperty("查询对象")
    @Valid
    private T queryObject;
}
