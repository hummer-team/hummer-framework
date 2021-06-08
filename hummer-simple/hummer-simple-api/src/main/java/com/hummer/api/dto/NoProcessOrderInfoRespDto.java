package com.hummer.api.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NoProcessOrderInfoRespDto {
    private Integer noProcessOrderCount;
    private Date firstOrderCreatedDateTime;
}
