package com.alphabot.telegram.message.config;

import com.alphabot.telegram.message.interceptor.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**");  // Apply to all API endpoints
//                .excludePathPatterns("/api/public/health"); // Exclude specific paths if needed
    }
}
