package com.hummer.yug.user.plugin.dto.request;

import lombok.Data;

import java.util.List;

/**
 * AuthorityReqDto
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/3/3 16:15
 */
@Data
public class AuthorityReqDto {

    private List<String> authCodes;

    private Integer condition;

}
