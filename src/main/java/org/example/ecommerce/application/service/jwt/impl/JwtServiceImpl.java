package org.example.ecommerce.application.service.jwt.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.jwt.JwtService;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.infrastructure.config.constant.ApplicationConstants;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {
    @Override
    public String generateToken(User user) {
        // الحصول على مفتاح السر من البيئة أو القيمة الافتراضية
        String secret = System.getenv(ApplicationConstants.JWT_SECRET_KEY);
        if (secret == null || secret.isBlank()) {
            secret = ApplicationConstants.JWT_SECRET_DEFAULT_VALUE;
            log.warn("JWT secret key not found in environment. Using default secret.");
        } else {
            log.info("JWT secret key loaded from environment.");
        }

        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // الوقت الحالي
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        Date issuedAt = Date.from(now.toInstant());
        Date expirationDate = Date.from(now.plus(1, ChronoUnit.MONTHS).toInstant());
        log.info("Token issued at: {}", issuedAt);
        log.info("Token will expire at: {}", expirationDate);

        // بيانات المستخدم
        log.info("Generating token for user: {}", user.getEmail());
        String authorities = user.getAuthorities().stream()
                .map(a -> a.getRole().name())
                .collect(Collectors.joining(","));
        log.info("User authorities: {}", authorities);

        // بناء التوكن
        String token = Jwts.builder()
                .setIssuer("Masala")
                .setSubject(user.getEmail())
                .claim("username", user.getEmail())
                .claim("authorities", authorities)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)
                .signWith(secretKey)
                .compact();

        log.info("JWT token generated successfully for user: {}", user.getEmail());
        return token;
    }
}
