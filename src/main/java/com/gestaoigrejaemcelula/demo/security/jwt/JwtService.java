package com.gestaoigrejaemcelula.demo.security.jwt;

import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 horas por padrão (em milissegundos)
    private long jwtExpiration;

    /**
     * Gera a chave de assinatura a partir do secret
     */
    private SecretKey getSignKey() {
        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalStateException("JWT Secret deve ter no mínimo 32 caracteres");
        }
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extrai todos os claims do token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valida se o token é válido para o usuário
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gera token JWT com claims customizados
     */
    public String gerarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("perfil", usuario.getPerfil().name());
        claims.put("id", usuario.getId());

        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey())
                .compact();
    }

    /**
     * Apenas para debug em desenvolvimento - NUNCA logar o secret completo em produção
     */
    @PostConstruct
    public void init() {
        logger.info("✅ JWT Service inicializado com sucesso");
        logger.info("🔑 Tamanho do secret: {} caracteres", secretKey.length());
        // logger.debug("Secret: {}", secretKey);  ← descomente só se estiver debugando localmente
    }
}