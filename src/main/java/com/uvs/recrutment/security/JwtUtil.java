package com.uvs.recrutment.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    private static final String SECRET_KEY = "verystrongsecretkeythatshouldbereplaced"; // Remplace-la par une clé sécurisée
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(String email, String role) {
        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role.toUpperCase()) // Convertir le rôle en majuscules
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Expire après 10h
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        
        logger.info("Token généré pour l'utilisateur {} avec rôle {}: {}", email, role, token);
        return token;
    }

    public String extractRole(String token) {
        String role = extractClaim(token, claims -> claims.get("role", String.class));
        logger.info("Rôle extrait du token: {}", role);
        return role;
    }

    public String extractUsername(String token) {
        String username = extractClaim(token, Claims::getSubject);
        logger.info("Nom d'utilisateur extrait du token: {}", username);
        return username;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            logger.error("Erreur lors de l'extraction des claims du token: {}", e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token, String email) {
        boolean isValid = email.equals(extractUsername(token)) && !isTokenExpired(token);
        if (isValid) {
            logger.info("Token valide pour l'utilisateur: {}", email);
        } else {
            logger.warn("Token invalide ou expiré pour l'utilisateur: {}", email);
        }
        return isValid;
    }

    private boolean isTokenExpired(String token) {
        boolean expired = extractClaim(token, Claims::getExpiration).before(new Date());
        if (expired) {
            logger.warn("Le token est expiré !");
        }
        return expired;
    }
}
