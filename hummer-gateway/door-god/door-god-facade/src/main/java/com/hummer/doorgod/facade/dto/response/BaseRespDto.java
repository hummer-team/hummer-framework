package com.hummer.doorgod.facade.dto.response;

import lombok.Data;

import java.util.Date;

/**
 * @author edz
 */
@Data
public class BaseRespDto {
    private Date lastModifiedDateTime;
    private String createdUserId;
    private Date createdDateTime;
    private String lastModifiedUserId;
    private Boolean isDeleted;
}
