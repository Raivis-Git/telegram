package com.alphabot.telegram.message.interceptor;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.*;

@Component
public class RateLimitMetrics {
    private final MeterRegistry meterRegistry;

    public RateLimitMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordRateLimitEvent(String path, RateLimitResult result) {
        Tags tags = Tags.of(
                "path", path,
                "result", result.allowed() ? "allowed" : "blocked",
                "reason", result.rejectionReason() != null ?
                        result.rejectionReason().toString() : "none"
        );

        meterRegistry.counter("rate_limit_requests", tags).increment();
    }
}
