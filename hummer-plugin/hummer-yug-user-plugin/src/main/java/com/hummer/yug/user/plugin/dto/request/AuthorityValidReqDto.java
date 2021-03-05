package com.hummer.yug.user.plugin.dto.request;

import lombok.Data;

import java.util.Map;

/**
 * AuthorityValidReqDto
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/3/3 15:22
 */
@Data
public class AuthorityValidReqDto {

    private Map<String, Object> userTokens;

    private AuthorityReqDto authority;
}
