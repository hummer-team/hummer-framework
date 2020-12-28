package com.hummer.redis.plugin.test.model;

import lombok.Data;

import javax.validation.constraints.Positive;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/4/28 11:32
 */
@Data
public class ExceptionReqDto {

    //    @NotNull(message = "pageNum is not null")
    @Positive(message = "pageNum is positive")
    private Integer pageNum;

    private String name;

    private String title;
}
