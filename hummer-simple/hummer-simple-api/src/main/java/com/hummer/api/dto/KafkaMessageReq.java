package com.hummer.api.dto;

import lombok.Data;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/13 10:23
 **/
@Data
public class KafkaMessageReq {
    private String body;
    private String id;
    private String type;
    private long count;
}
