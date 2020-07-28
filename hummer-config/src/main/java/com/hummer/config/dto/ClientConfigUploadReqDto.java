package com.hummer.config.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * ClientConfigUploadReqDto
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/7/24 15:52
 */
@Data
public class ClientConfigUploadReqDto {
    private String businessCode;
    private String operationType;
    private String uniqueId;
    private String oldValue;
    private String newValue;
    private String operatorId;
    private String operatorName;
    private String remark;
    private Date operatorTime;
    private Map<String, String> extendData;
}
