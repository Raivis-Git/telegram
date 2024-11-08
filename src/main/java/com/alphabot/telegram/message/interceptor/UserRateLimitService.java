package com.alphabot.telegram.message.interceptor;

import io.github.bucket4j.*;
import jakarta.servlet.http.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cache.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

@Service
public class UserRateLimitService {

    Logger logger = LoggerFactory.getLogger(UserRateLimitService.class);
    private final Map<String, Map<String, Bucket>> buckets = new ConcurrentHashMap<>();
    private final Cache blacklistCache;
    private final AtomicInteger totalRequestsCount = new AtomicInteger(0);
    private static final int BLACKLIST_THRESHOLD = 50; // Blacklist after 50 violations
    private static final Duration BLACKLIST_DURATION = Duration.ofHours(24);

    public UserRateLimitService(CacheManager cacheManager) {
        this.blacklistCache = cacheManager.getCache("rateLimits");
    }

    @Value("${rate.limit.requests.per.minute:30}")
    private int requestsPerMinute;

    @Value("${rate.limit.burst:10}")
    private int burstCapacity;

    public RateLimitResult tryConsume(String username,
                                      String ipAddress,
                                      HttpServletRequest request,
                                      RateLimitRule rule) {
        String key = username + ipAddress + request.getRequestURI();

        // Check blacklist first
        if (isBlacklisted(username) || isBlacklisted(ipAddress)) {
            return new RateLimitResult(false, RejectionReason.BLACKLISTED, requestsPerMinute, requestsPerMinute, 600);
        }

        Bucket bucket = getBucket(key, rule);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (!probe.isConsumed()) {
            handleRateLimitViolation(username, ipAddress);
            return new RateLimitResult(false,
                    RejectionReason.RATE_LIMIT_EXCEEDED,
                    rule.requestsPerMinute(),
                    probe.getRemainingTokens(),
                    probe.getNanosToWaitForRefill());
        }

        return new RateLimitResult(true,
                null,
                rule.requestsPerMinute(),
                probe.getRemainingTokens(),
                probe.getNanosToWaitForRefill());
    }

    private Bucket getBucket(String key, RateLimitRule rule) {
        return buckets
                .computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(String.valueOf(rule.requestsPerMinute()),
                        k -> createBucket(rule));
    }

    private Bucket createBucket(RateLimitRule rule) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(rule.requestsPerMinute(),
                        Refill.greedy(rule.requestsPerMinute(), Duration.ofMinutes(1))))
                .addLimit(Bandwidth.classic(rule.burstCapacity(),
                        Refill.greedy(rule.burstCapacity(), Duration.ofSeconds(30))))
                .build();
    }

    private boolean isBlacklisted(String key) {
        return Boolean.TRUE.equals(blacklistCache.get(key, Boolean.class));
    }

    private void handleRateLimitViolation(String username, String ipAddress) {
        logger.warn("Rate limit violated by user: {} from IP: {}", username, ipAddress);
        incrementViolationCount(username);
        incrementViolationCount(ipAddress);
    }

    private void handleSuspiciousActivity(String username, String ipAddress) {
        logger.warn("Suspicious activity detected from user: {} IP: {}", username, ipAddress);
        blacklistCache.put(username, true);
        blacklistCache.put(ipAddress, true);
        // Notify security team or trigger alerts
    }

    private void incrementViolationCount(String key) {
        Integer violations = blacklistCache.get(key + "_violations", Integer.class);
        int newCount = (violations == null ? 1 : violations + 1);
        blacklistCache.put(key + "_violations", newCount);

        if (newCount >= BLACKLIST_THRESHOLD) {
            blacklistCache.put(key, true);
        }
    }

    // Metrics for monitoring
    public int getTotalRequestsCount() {
        return totalRequestsCount.get();
    }

    // ... rest of the methods from previous implementation
}
