package com.gestaoigrejaemcelula.demo.security.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write(
                                    "{\"status\":401,\"error\":\"Nao autorizado\",\"message\":\"Token ausente, invalido ou expirado\"}"
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write(
                                    "{\"status\":403,\"error\":\"Acesso negado\"}"
                            );
                        })
                )
                .authorizeHttpRequests(req -> req
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/solicitar-cadastro-lider").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios/solicitar-alteracao").permitAll()

                        // ✅ removida linha duplicada de /relatorios/** com permitAll()
                        // ✅ removida linha duplicada de /membros/sem-celula
                        .requestMatchers("/api/casas-de-paz/**").hasAnyAuthority("ADMIN", "SECRETARIO", "PASTOR", "LIDER_CELULA")
                        .requestMatchers(HttpMethod.PATCH, "/usuarios/*/foto").hasAuthority("ADMIN")
                        .requestMatchers("/api/missao70/**").hasAnyAuthority("ADMIN", "SECRETARIO", "PASTOR", "LIDER_CELULA")
                        .requestMatchers("/api/missao70").hasAnyAuthority("ADMIN", "SECRETARIO", "PASTOR", "LIDER_CELULA")
                        .requestMatchers("/discipulado/**").permitAll()
                        .requestMatchers("/celulas/minha-celula").hasAnyAuthority("LIDER_CELULA")
                        .requestMatchers("/celulas/**").hasAnyAuthority("ADMIN", "SECRETARIO", "PASTOR", "LIDER_CELULA")
                        .requestMatchers("/membros/sem-celula").hasAnyAuthority("LIDER_CELULA", "PASTOR","SECRETARIO")
                        .requestMatchers("/membros/**").hasAnyAuthority("ADMIN", "SECRETARIO", "PASTOR", "TESOUREIRO","LIDER_CELULA")
                        .requestMatchers("/api/pastor/**").hasAnyAuthority("PASTOR")
                        .requestMatchers("/tesouraria/**").hasAnyAuthority("ADMIN", "TESOUREIRO", "PASTOR")
                        .requestMatchers("/relatorios/**").hasAnyAuthority("ADMIN", "SECRETARIO", "PASTOR", "LIDER_CELULA")

                        .anyRequest().authenticated()

                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:4200",
                "https://gestaoquadrangularpituacubr.vercel.app",
                "https://gestao-quadrangular-frontend.vercel.app",
                "https://gestaoquadrangular-backend-dad1.onrender.com",
                "https://portalieqpituacu.com.br",
                "https://www.portalieqpituacu.com.br"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}