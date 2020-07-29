package com.hummer.doorgod.support.model.po;

import lombok.Data;

import java.util.Date;

/**
 * base po
 * @author edz
 */
@Data
public class BasePo {
    private Date lastModifiedDateTime;
    private String createdUserId;
    private Date createdDateTime;
    private String lastModifiedUserId;
    private Boolean isDeleted;
}
