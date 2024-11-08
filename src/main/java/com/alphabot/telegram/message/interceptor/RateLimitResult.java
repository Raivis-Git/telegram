package com.alphabot.telegram.message.interceptor;

public record RateLimitResult(boolean allowed, RejectionReason rejectionReason, long limit, long remaining, long resetTime) {
}
