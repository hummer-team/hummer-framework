package com.hummer.test.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/6/3 14:32
 */
@ApiModel
@Data
public class DemoReqDto {

    @ApiModelProperty("id")
    @NotNull(message = "id is required")
    private Long id;
}
