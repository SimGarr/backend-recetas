package com.basededatosrecetas.recetas.Seguridad;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generar token con email, rol y userId
    public String generateToken(String email, String rol, Long userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("rol", rol)
                .claim("userId", userId)  // ← AÑADIDO: userId como claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Obtener email del token
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Obtener rol del token
    public String extractRole(String token) {
        return getClaims(token).get("rol", String.class);
    }

    // Obtener userId del token
    public Long extractUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    // Validar token
    public boolean validateToken(String token, String email) {
        return extractUsername(token).equals(email) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}