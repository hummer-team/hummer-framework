package com.hummer.user.plugin.user.member;

import lombok.Data;

/**
 * NetCoreResponse
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/22 14:01
 */
@Data
public class NetCoreResponse<T> {

    private Integer code;
    private Integer subCode;
    private String msg;
    private String currentDateTime;
    private T data;
}
