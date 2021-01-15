package com.hummer.rest.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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
    @NotNull(message = "this page number can't null")
    @Min(value = 1, message = "pageNumber min 1")
    private Integer pageNumber;

    @ApiModelProperty("单页记录数")
    @NotNull(message = "this page size can't null")
    @Min(value = 1, message = "pageSize min 1")
    private Integer pageSize;

    @ApiModelProperty("查询对象")
    @Valid
    private T queryObject;


    public Integer getPageNumber() {
        if (this.pageNumber == null) {
            return null;
        }
        return this.pageNumber > 0 && this.pageNumber <= 4294967 ? this.pageNumber : 1;
    }

    public Integer getPageSize() {
        if (this.pageSize == null) {
            return null;
        }

        return this.pageSize > 0 && this.pageSize <= 500 ? this.pageSize : 10;
    }
}
