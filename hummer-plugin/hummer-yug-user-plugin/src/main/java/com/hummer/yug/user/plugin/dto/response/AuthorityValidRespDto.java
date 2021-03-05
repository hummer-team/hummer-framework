package com.hummer.yug.user.plugin.dto.response;

import lombok.Data;

/**
 * AuthorityValidRespDto
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/3/3 15:24
 */
@Data
public class AuthorityValidRespDto {

    private String userId;

    private String userName;

    private Object data;
}
