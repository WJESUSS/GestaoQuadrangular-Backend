package com.gestaoigrejaemcelula.demo.security.jwt;

import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // 🔑 CORRIGIDO (SEM BASE64)
    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 📦 Extrai claims com segurança
    private Claims extractAllClaims(String jwt) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Token JWT inválido ou expirado");
        }
    }

    // 🔑 Username
    public String extractUsername(String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    // ⏳ Expiração
    public Date extractExpiration(String jwt) {
        return extractAllClaims(jwt).getExpiration();
    }

    // ⛔ Verifica expiração
    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    // ✅ Validação
    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        try {
            String username = extractUsername(jwt);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(jwt);
        } catch (Exception e) {
            return false;
        }
    }

    // 🔐 Geração de token
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
}