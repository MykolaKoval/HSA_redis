package com.course.hsa.controller;

import com.course.hsa.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    @PostMapping(path = "/fill")
    public void getAutocomplete(@RequestParam("addCount") Integer addCount, @RequestParam(name = "ttl", required = false) Long ttl) {
        cacheService.fillCache(addCount, ttl);
    }

    @GetMapping(path = "/value")
    public String getValue(@RequestParam("cacheKey") String cacheKey) {
        return cacheService.getValue(cacheKey);
    }

    @GetMapping(path = "/probabilistic-value")
    public String getProbabilisticValue(@RequestParam("cacheKey") String cacheKey) {
        return cacheService.getProbabilisticValue(cacheKey);
    }

    @GetMapping(path = "/probabilistic-value-with-lock")
    public String getProbabilisticValueWithLock(@RequestParam("cacheKey") String cacheKey) {
        return cacheService.getProbabilisticValueWithLock(cacheKey);
    }

    @GetMapping(path = "/ttl")
    public long getTll(@RequestParam("cacheKey") String cacheKey) {
        return cacheService.getTtl(cacheKey);
    }

}
