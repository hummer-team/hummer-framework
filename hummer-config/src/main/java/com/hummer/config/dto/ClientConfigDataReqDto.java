package com.hummer.config.dto;

import lombok.Data;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/7/24 16:36
 */
@Data
public class ClientConfigDataReqDto {

    private String appName;

    private String appIp;

    private String appPort;

    private String configInfo;
}
