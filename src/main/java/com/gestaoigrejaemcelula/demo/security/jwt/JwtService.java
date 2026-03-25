package com.gestaoigrejaemcelula.demo.security.jwt;

import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    // 🔑 Converte a string secreta em SecretKey
    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 📦 Método para extrair todas as claims de um token
    private Claims extractAllClaims(String jwt) {
        Jws<Claims> jwsClaims = Jwts.parser()
                .verifyWith(getSignKey())        // define chave para verificar assinatura
                .build()                         // constrói o parser
                .parseSignedClaims(jwt);         // lê e valida o JWT

        return jwsClaims.getPayload();         // extrai o payload (Claims)
    }

    // 🔍 Método genérico para extrair qualquer claim
    public <T> T extractClaim(String jwt, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(jwt));
    }

    // 🔑 Extrai o username (subject)
    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    // ⏳ Verifica se o token expirou
    private boolean isTokenExpired(String jwt) {
        return extractClaim(jwt, Claims::getExpiration).before(new Date());
    }

    // ✅ Valida o token
    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        String username = extractUsername(jwt);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(jwt);
    }

    // 🔐 Gera um novo JWT
    // 🔐 Gera um novo JWT corrigido para o Frontend
    public String gerarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();

        // Mudamos de "role" para "perfil" para bater com o jwtDecode(token).perfil do React
        // E pegamos o nome do perfil (Ex: ADMIN, PASTOR...)
        claims.put("perfil", usuario.getPerfil().name());
        claims.put("id", usuario.getId());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getEmail()) // Usando email como subject
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24)) // 24h
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
