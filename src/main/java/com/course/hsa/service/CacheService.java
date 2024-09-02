package com.course.hsa.service;

import com.course.hsa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.params.SetParams;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class CacheService {

    private static final String USER_NOT_FOUND = "User not found, name=%s";
    private static final long CACHE_TTL = Duration.ofHours(1).toSeconds();
    private final AtomicInteger counter = new AtomicInteger(0);

    @Value("${cache.early-refresh-period-sec:300}")
    private final long cacheEarlyRefreshPeriodSec;
    private final JedisPooled jedis;
    private final UserRepository userRepository;

    public void fillCache(Integer addCount, Long ttl) {
        for (int i = 0; i < addCount; i++) {
            if (ttl != null) {
                jedis.set("key" + counter.incrementAndGet(), UUID.randomUUID().toString(), SetParams.setParams()
                        .ex(ttl));
            } else {
                jedis.set("key" + counter.incrementAndGet(), UUID.randomUUID().toString());
            }
        }
    }

    public String getValue(String cacheKey) {
        return jedis.get(cacheKey);
    }

    public long getTtl(String cacheKey) {
        return jedis.ttl(cacheKey);
    }

    public String getProbabilisticValue(String cacheKey) {
        var ttl = getTtl(cacheKey);
        if (ttl <= 0 || isCacheRefreshRequired(ttl)) {
            var user = userRepository.findByName(cacheKey)
                    .orElseThrow(() -> new IllegalArgumentException(String.format(USER_NOT_FOUND, cacheKey)));
            jedis.set(cacheKey, user.getCity(), SetParams.setParams().ex(CACHE_TTL));
        }
        return jedis.get(cacheKey);
    }

    private boolean isCacheRefreshRequired(long cacheKeyTtl) {
        return cacheEarlyRefreshPeriodSec * Math.abs(Math.log(Math.random())) >= cacheKeyTtl;
    }
}
