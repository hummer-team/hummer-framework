package com.hummer.yug.user.plugin.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * MemberValidReqDto
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/12/24 15:57
 */
@Data
public class MemberValidReqDto {

    @NotEmpty(message = "缺少访问通行证1")
    private String sid;

    @NotEmpty(message = "缺少访问通行证2")
    private String userToken;
}
