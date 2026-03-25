package com.gestaoigrejaemcelula.demo.security.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
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

import java.util.Arrays;
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
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"status\":401,\"error\":\"Não autorizado\",\"message\":\"" + authException.getMessage() + "\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"status\":403,\"error\":\"Acesso negado\",\"message\":\"" + accessDeniedException.getMessage() + "\"}");
                        })
                )

                .authorizeHttpRequests(req -> req
                        // Públicas
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/api/membros/**").hasAnyAuthority("ADMIN", "SECRETARIO", "PASTOR","TESOUREIRO")  // POST/PUT/DELETE só admins/secretarios
                                .requestMatchers("/api/discipulado/**").hasAnyAuthority("SECRETARIO", "PASTOR", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/membros/*/vincular-celula/*").hasAnyRole("LIDER_CELULA", "ADMIN", "SECRETARIA","PASTOR")
                        .requestMatchers("/api/relatorios/**").hasAnyAuthority("ADMIN", "SECRETARIO", "LIDER_CELULA", "PASTOR")
                        .requestMatchers("/celulas/**").hasAnyAuthority("ADMIN", "SECRETARIO", "LIDER_CELULA", "PASTOR")
                        .requestMatchers("/visitantes/**").hasAnyAuthority("ADMIN", "SECRETARIO", "LIDER_CELULA", "PASTOR")
                        .requestMatchers(HttpMethod.GET, "/membros/sem-celula").hasAnyAuthority("ADMIN", "SECRETARIO", "LIDER_CELULA","PASTOR")
                        .requestMatchers("/membros/**").hasAnyAuthority("ADMIN", "SECRETARIO", "PASTOR")
// Se existir isso, mude para:
                                .requestMatchers("/api/alertas-celulas/**").permitAll()

                                .requestMatchers("/api/discipulado/**").hasAnyAuthority("SECRETARIO", "ADMIN", "PASTOR")
                                .requestMatchers("/api/membros/**").hasAnyAuthority("ADMIN", "SECRETARIO", "PASTOR")
                                .requestMatchers("/api/relatorios/**")
                                .hasAnyAuthority("ADMIN", "SECRETARIO", "LIDER_CELULA", "PASTOR")
                                .requestMatchers("/api/tesouraria/**").hasAnyAuthority("TESOUREIRO", "ADMIN","PASTOR")// Adicione esta linha!
                                .requestMatchers("/api/discipulado/todos-relatorios").hasAnyAuthority("SECRETARIO", "ADMIN","PASTOR")
                                .requestMatchers("/api/discipulado/**").hasAnyAuthority("ADMIN", "LIDER_CELULA", "PASTOR", "SECRETARIO")
                                .requestMatchers(HttpMethod.GET,"/api/discipulado").hasAnyAuthority("ADMIN","LIDER_CELULA","PASTOR")
                                .requestMatchers(HttpMethod.GET, "/api/membros/celula/**").hasAnyRole("ADMIN", "USER", "LIDER_CELULA","PASTOR")
                                .requestMatchers("/api/celulas/minha-celula").hasRole("LIDER_CELULA")
                                .requestMatchers("/api/celulas/**").hasAnyRole("ADMIN", "SECRETARIO","PASTOR")
                        .requestMatchers(HttpMethod.GET, "/resumo").hasAnyAuthority("LIDER_CELULA")
                        .requestMatchers(HttpMethod.POST, "/celulas/*/visitantes").hasAnyAuthority("ADMIN", "SECRETARIO", "LIDER_CELULA","PASTOR")
                        .requestMatchers(HttpMethod.POST, "/celulas/*/membros/*").hasAnyAuthority("ADMIN", "SECRETARIO", "LIDER_CELULA","PASTOR")
                        .requestMatchers(HttpMethod.POST, "/api/celulas/*/visitantes").hasAnyAuthority("ADMIN", "SECRETARIO", "LIDER_CELULA","PASTOR")
                                .requestMatchers(HttpMethod.POST, "/api/celulas/*/membros/*").hasAnyAuthority("ADMIN", "SECRETARIO", "LIDER_CELULA","PASTOR")

                        // Tudo o mais exige autenticação (mas como é JWT, sem token = 401)
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
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
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
        CorsConfiguration configuration = new CorsConfiguration();

        // Define as origens permitidas (seu React)
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // Define os métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));

        // Define os Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));

        // Permite o envio de cookies/autenticação se necessário
        configuration.setAllowCredentials(true);

        // Expõe headers específicos se o seu frontend precisar ler o Token do Header (opcional)
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}