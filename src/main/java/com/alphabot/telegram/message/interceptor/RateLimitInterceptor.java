package com.alphabot.telegram.message.interceptor;

import jakarta.servlet.http.*;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;
import org.springframework.web.servlet.*;

import java.io.*;
import java.util.*;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final UserRateLimitService rateLimitService;
    private final Map<String, RateLimitRule> rateLimitRules;

    public RateLimitInterceptor(UserRateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
        this.rateLimitRules = initializeRateLimitRules();
    }

    private Map<String, RateLimitRule> initializeRateLimitRules() {
        Map<String, RateLimitRule> rules = new HashMap<>();
        // Default rule for /api/**
        rules.put("/api/**", new RateLimitRule(30, 10)); // 30 requests per minute, burst of 10

        // Specific rules for different endpoints
        rules.put("/api/public/**", new RateLimitRule(60, 20)); // More lenient for public APIs
        rules.put("/api/admin/**", new RateLimitRule(100, 30)); // More permissive for admin APIs
        rules.put("/api/auth/**", new RateLimitRule(10, 5)); // Stricter for auth endpoints

        return rules;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // Skip rate limiting for certain paths if needed
        if (shouldSkipRateLimit(request)) {
            return true;
        }

        String username = extractUsername(request);
        String ipAddress = extractIpAddress(request);
        String path = request.getRequestURI();

        RateLimitRule rule = findMatchingRule(path);
        RateLimitResult result = rateLimitService.tryConsume(username, ipAddress, request, rule);

        if (!result.allowed()) {
            handleRejection(response, result);
            return false;
        }

        addRateLimitHeaders(response, result);
        return true;
    }

    private RateLimitRule findMatchingRule(String path) {
        return rateLimitRules.entrySet().stream()
                .filter(entry -> pathMatches(path, entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(new RateLimitRule(30, 10)); // Default rule
    }

    private boolean pathMatches(String path, String pattern) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, path);
    }

    private void handleRejection(HttpServletResponse response, RateLimitResult result) throws IOException {
        response.setContentType("application/json");

        switch (result.rejectionReason()) {
            case RATE_LIMIT_EXCEEDED -> {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("{\"error\": \"Rate limit exceeded\"}");
            }
            case BLACKLISTED -> {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("{\"error\": \"Access denied\"}");
            }
            case SUSPICIOUS_ACTIVITY -> {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("{\"error\": \"Suspicious activity detected\"}");
            }
        }
    }

    private void addRateLimitHeaders(HttpServletResponse response, RateLimitResult result) {
        response.addHeader("X-RateLimit-Limit", String.valueOf(result.limit()));
        response.addHeader("X-RateLimit-Remaining", String.valueOf(result.remaining()));
        response.addHeader("X-RateLimit-Reset", String.valueOf(result.resetTime()));
    }

    private boolean shouldSkipRateLimit(HttpServletRequest request) {
        // Skip rate limiting for specific paths (e.g., health checks)
        return request.getRequestURI().startsWith("/actuator/") ||
                request.getRequestURI().startsWith("/swagger-ui/") ||
                isLocalCall(request);
    }

    private boolean isLocalCall(HttpServletRequest request) {
        String remoteAddress = request.getRemoteAddr();
        return "127.0.0.1".equals(remoteAddress)
                || "0:0:0:0:0:0:0:1".equals(remoteAddress)  // IPv6 localhost
                || "localhost".equals(request.getServerName());
    }

    private String extractUsername(HttpServletRequest request) {
        // Try to get from Spring Security context first
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        // Fallback to IP-based identification
        return "anonymous";
    }

    private String extractIpAddress(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if (StringUtils.hasText(ip))
            return ip;

        ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip))
            return ip;

        return "unknown";
    }
}
