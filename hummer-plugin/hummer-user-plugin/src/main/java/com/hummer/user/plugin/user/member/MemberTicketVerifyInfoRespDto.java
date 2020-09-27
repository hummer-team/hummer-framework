package com.hummer.user.plugin.user.member;

import lombok.Data;

/**
 * MemberTicketVerifyInfoRespDto
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/9/2 15:35
 */
@Data
public class MemberTicketVerifyInfoRespDto {

    private Boolean isAuthed;

    private String userId;

}
