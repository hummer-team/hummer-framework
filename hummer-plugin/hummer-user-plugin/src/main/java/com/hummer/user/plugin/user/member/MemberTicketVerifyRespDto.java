package com.hummer.user.plugin.user.member;

import lombok.Data;

/**
 * MemberTicketVerifyRespDto
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/9/2 15:33
 */
@Data
public class MemberTicketVerifyRespDto {

    private Integer code;
    private Integer subCode;
    private String msg;
    private String currentDateTime;
    private MemberTicketVerifyInfoRespDto data;
}
