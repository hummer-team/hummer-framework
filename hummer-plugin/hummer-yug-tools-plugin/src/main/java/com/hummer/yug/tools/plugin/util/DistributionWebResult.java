package com.hummer.yug.tools.plugin.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * description: REST 返回信息类
 *
 * @Author: chen wei
 * @CreateDate: 2019/7/1 16:13
 * @UpdateDate: 2019/7/1 16:13
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2019
 * </p>
 */
@Data
@ApiModel
public class DistributionWebResult<T> {
    /**
     * 错误码
     */
    @ApiModelProperty(value = "返回状态码")
    private String code = "SUCCESS";
    /**
     * 提示信息
     */
    @ApiModelProperty(value = "返回信息")
    private String message = "SUCCESS";

    @ApiModelProperty(value = "业务数据")
    // @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    private String trackId;

    private Date time;

}
