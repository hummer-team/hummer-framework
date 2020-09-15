package com.hummer.user.plugin.user.member;

import lombok.Builder;
import lombok.Data;

/**
 * MemberTicketVerifyReqDto
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/9/2 15:24
 */
@Data
@Builder
public class MemberTicketVerifyReqDto {

    private String cookieValue;

    private String token;
}
