package com.course.hsa.service;

import com.course.hsa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.params.SetParams;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private static final String USER_NOT_FOUND = "User not found, name=%s";
    private static final String KEY_REFRESH_LOCK_TEMPLATE = "%s-refreshLock";
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
        if (ttl <= 0) {
            fillCache(cacheKey);
        } else if (isCacheRefreshRequired(ttl)) {
            fillCache(cacheKey);
        }
        return jedis.get(cacheKey);
    }

    public String getProbabilisticValueWithLock(String cacheKey) {
        var ttl = getTtl(cacheKey);
        if (ttl <= 0) {
            fillCache(cacheKey);
        } else if (isCacheRefreshRequired(ttl) && isRefreshLockMissing(cacheKey)) {
            acquireRefreshLock(cacheKey);
            fillCache(cacheKey);
            releaseRefreshLock(cacheKey);
        }
        return jedis.get(cacheKey);
    }

    @Scheduled(cron = "0/5 * * ? * *")
    public void setEarlyRefreshTtl() {
        log.info("Cache updated: key=Vasyl, ttl=500");
        jedis.set("Vasyl", "Default value", SetParams.setParams().ex(500));
    }

    private boolean isCacheRefreshRequired(long cacheKeyTtl) {
        return cacheEarlyRefreshPeriodSec * Math.abs(Math.log(Math.random())) >= cacheKeyTtl;
    }

    public boolean isRefreshLockMissing(String cacheKey) {
        var refreshLock = String.format(KEY_REFRESH_LOCK_TEMPLATE, cacheKey);
        return !jedis.exists(refreshLock);
    }

    public void acquireRefreshLock(String cacheKey) {
        var refreshLock = String.format(KEY_REFRESH_LOCK_TEMPLATE, cacheKey);
        jedis.set(refreshLock, "key is refreshing");
    }

    public void releaseRefreshLock(String cacheKey) {
        var refreshLock = String.format(KEY_REFRESH_LOCK_TEMPLATE, cacheKey);
        jedis.expire(refreshLock, 0);
    }

    private void fillCache(String cacheKey) {
        var user = userRepository.findByName(cacheKey)
                .orElseThrow(() -> new IllegalArgumentException(String.format(USER_NOT_FOUND, cacheKey)));
        jedis.set(cacheKey, user.getCity(), SetParams.setParams().ex(CACHE_TTL));
    }
}
