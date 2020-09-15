package com.hummer.doorgod.test;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class Config {
    private List<String> blackIp;
    private List<String> blackUserAgent;
    private Map<String, List<String>> blackHead;
    private String routeId;
    private Integer order;
}
