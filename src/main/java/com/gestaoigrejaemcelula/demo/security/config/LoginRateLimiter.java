package com.gestaoigrejaemcelula.demo.security.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class LoginRateLimiter {

    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .build();

    public Bucket getBucket(String email) {
        return buckets.get(email, k -> {
            Bandwidth limite = Bandwidth.classic(3, Refill.intervally(3, Duration.ofMinutes(5)));
            return Bucket.builder().addLimit(limite).build();
        });
    }

    public void resetar(String email) {
        buckets.invalidate(email);
    }
}