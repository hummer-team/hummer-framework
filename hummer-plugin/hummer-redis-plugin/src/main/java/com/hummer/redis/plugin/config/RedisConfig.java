package com.hummer.redis.plugin.config;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/10/25 14:24
 **/
public class RedisConfig {
    @Builder
    @Getter
    @ToString
    public static class SimpleConfig {
        @Tolerate
        SimpleConfig() {

        }

        private String host;
        private Integer port;
        private Integer timeOut;
        private Integer dbNumber;
        private String password;
        private String clientName;

        public String getPassword() {
            return StringUtils.isEmpty(password) || StringUtils.isBlank(password) ? null : password;
        }
    }

    @Builder
    @Getter
    @ToString
    public static class SentinelConfig {
        @Tolerate
        SentinelConfig() {

        }

        private String masterName;
        private Set<String> sentinelNode;
        private String password;
        private Integer timeOut;
        private Integer dbNumber;
        private String clientName;

        public String getPassword() {
            return StringUtils.isEmpty(password) || StringUtils.isBlank(password) ? null : password;
        }
    }
}
