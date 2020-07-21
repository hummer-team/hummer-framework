package com.hummer.api.dto;

import com.hummer.cache.plugin.HummerSimpleObjectCacheKey;
import lombok.Data;

@Data
public class CacheTestReqDto {
    @HummerSimpleObjectCacheKey
    private String userId;
    private String aa;
}
