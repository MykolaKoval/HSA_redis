package com.course.hsa.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisConfigurationProperties redisConfiguration;

    @Bean
    public JedisPooled jedisClient() {
        return new JedisPooled(redisConfiguration.getHost(),
                redisConfiguration.getPort(), redisConfiguration.getUser(), redisConfiguration.getPassword());
    }
}
