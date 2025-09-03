package org.example.ecommerce.infrastructure.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.ecommerce.infrastructure.config.constant.ApplicationConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    @Value("${" + ApplicationConstants.JWT_SECRET_KEY + ":" + ApplicationConstants.JWT_SECRET_DEFAULT_VALUE + "}")
    private String jwtSecret;

    public String extractEmailFromJwt(String jwt) {
        jwt = cleanJwtToken(jwt);

        if (jwt == null || jwt.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT is null or empty");
        }

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();

        String email = (String) claims.get("username");

        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            email = claims.getSubject();
        }

        if (email == null || email.trim().isEmpty() || !email.contains("@") || email.equals("JWT Token")) {
            throw new IllegalArgumentException("No valid customerEmail found in JWT");
        }

        return email;
    }

    private String cleanJwtToken(String jwt) {
        if (jwt != null && jwt.startsWith("Bearer ")) {
            return jwt.substring(7);
        }
        return jwt;
    }
}
