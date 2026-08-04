package com.gestaoigrejaemcelula.demo.security.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {

        CaffeineCache metricasCache = new CaffeineCache("metricas-pastor",
                Caffeine.newBuilder()
                        .maximumSize(10)                          // 50 → 10
                        .expireAfterWrite(3, TimeUnit.MINUTES)    // 5m → 3m
                        .build());

        CaffeineCache rankingCache = new CaffeineCache("ranking-celulas",
                Caffeine.newBuilder()
                        .maximumSize(5)                           // 50 → 5
                        .expireAfterWrite(5, TimeUnit.MINUTES)    // mantido
                        .build());

        CaffeineCache aniversariantesCache = new CaffeineCache("aniversariantes",
                Caffeine.newBuilder()
                        .maximumSize(5)                           // 10 → 5
                        .expireAfterWrite(30, TimeUnit.MINUTES)   // 1h → 30min
                        .build());

        CaffeineCache alertasCache = new CaffeineCache("alertas-discipulado",
                Caffeine.newBuilder()
                        .maximumSize(20)                          // 50 → 20
                        .expireAfterWrite(3, TimeUnit.MINUTES)    // 5m → 3m
                        .build());
        CaffeineCache secretariaCache = new CaffeineCache("secretaria-discipulado",
                Caffeine.newBuilder()
                        .maximumSize(1)
                        .expireAfterWrite(2, TimeUnit.MINUTES)
                        .build());

        CaffeineCache relatoriosDiscipuladoCache = new CaffeineCache("relatorios-discipulado-todos",
                Caffeine.newBuilder()
                        .maximumSize(1)
                        .expireAfterWrite(2, TimeUnit.MINUTES)
                        .build());

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(
                metricasCache,
                rankingCache,
                aniversariantesCache,
                alertasCache,
                secretariaCache,
                relatoriosDiscipuladoCache
        ));

        return manager;
    }
}