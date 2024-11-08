package com.alphabot.telegram.message.interceptor;

import org.springframework.boot.context.properties.*;
import org.springframework.stereotype.*;

import java.util.*;

@ConfigurationProperties(prefix = "rate-limit")
@Component
public class RateLimitProperties {
    private Map<String, RuleProperties> rules;

    public RateLimitProperties(Map<String, RuleProperties> rules) {
        this.rules = rules;
    }

    public Map<String, RuleProperties> getRules() {
        return rules;
    }

    public void setRules(Map<String, RuleProperties> rules) {
        this.rules = rules;
    }

    public static class RuleProperties {
        private int requestsPerMinute = 30;
        private int burstCapacity = 10;

        public RuleProperties(int requestsPerMinute, int burstCapacity) {
            this.requestsPerMinute = requestsPerMinute;
            this.burstCapacity = burstCapacity;
        }

        public int getRequestsPerMinute() {
            return requestsPerMinute;
        }

        public void setRequestsPerMinute(int requestsPerMinute) {
            this.requestsPerMinute = requestsPerMinute;
        }

        public int getBurstCapacity() {
            return burstCapacity;
        }

        public void setBurstCapacity(int burstCapacity) {
            this.burstCapacity = burstCapacity;
        }
    }
}
