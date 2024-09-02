package com.course.hsa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cache.redis")
public class RedisConfigurationProperties {

    private String host;
    private Integer port;
    private String user;
    private String password;
}
