package com.gestaoigrejaemcelula.demo.security.config;



import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    // Um bucket por email — isola tentativas por usuário
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket getBucket(String email) {
        return buckets.computeIfAbsent(email, this::criarBucket);
    }

    private Bucket criarBucket(String email) {
        // 3 tentativas, recarrega 3 a cada 5 minutos
        Bandwidth limite = Bandwidth.classic(3, Refill.intervally(3, Duration.ofMinutes(5)));
        return Bucket.builder().addLimit(limite).build();
    }

    // Chama isso no login bem-sucedido para resetar o contador
    public void resetar(String email) {
        buckets.remove(email);
    }
}