package com.alphabot.telegram.message.interceptor;

public record RateLimitRule(int requestsPerMinute, int burstCapacity) {
}
