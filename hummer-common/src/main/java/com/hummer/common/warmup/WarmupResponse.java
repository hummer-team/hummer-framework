package com.hummer.common.warmup;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WarmupResponse {
    private String key;
    private String message;
    private boolean success;
    private Long costMillis;
}
