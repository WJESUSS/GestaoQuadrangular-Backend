package com.gestaoigrejaemcelula.demo.security.config;

import com.gestaoigrejaemcelula.demo.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // ✅ CORS preflight — deixa passar sem verificar token
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getServletPath();

        // ✅ Rotas públicas — deixa passar sem verificar token
        if (path.startsWith("/auth") || path.startsWith("/api/auth") || path.startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        // ✅ Sem header Authorization — deixa o Spring Security decidir (vai retornar 401 se rota protegida)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("⚠️ [JWT] Header Authorization ausente ou inválido para: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7).trim();

        // ✅ Token vazio após "Bearer "
        if (jwt.isEmpty()) {
            System.out.println("⚠️ [JWT] Token vazio para: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String email;
        try {
            email = jwtService.extractUsername(jwt);
            System.out.println("✅ [JWT] Username extraído: " + email + " | rota: " + path);
        } catch (Exception e) {
            // ✅ Log detalhado para ver exatamente o que está falhando
            System.out.println("❌ [JWT] Token inválido para rota " + path);
            System.out.println("❌ [JWT] Erro: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            System.out.println("❌ [JWT] Token recebido (primeiros 40 chars): " + jwt.substring(0, Math.min(40, jwt.length())));
            filterChain.doFilter(request, response);
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("✅ [JWT] Autenticado com sucesso: " + email
                            + " | authorities: " + userDetails.getAuthorities()
                            + " | rota: " + path);
                } else {
                    System.out.println("❌ [JWT] Token inválido ou expirado para usuário: " + email);
                }
            } catch (Exception e) {
                System.out.println("❌ [JWT] Erro ao carregar usuário: " + email + " - " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}