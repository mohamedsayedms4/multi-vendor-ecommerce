package org.example.ecommerce.infrastructure.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.infrastructure.config.constant.ApplicationConstants;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
public class JwtTokenGeneratorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            log.info("Authentication found for user: {}", authentication.getName());

            Environment environment = getEnvironment();
            String secret;
            if (environment != null) {
                secret = environment.getProperty(
                        ApplicationConstants.JWT_SECRET_KEY,
                        ApplicationConstants.JWT_SECRET_DEFAULT_VALUE
                );
                log.info("JWT secret loaded from environment or default value.");
            } else {
                secret = ApplicationConstants.JWT_SECRET_DEFAULT_VALUE;
                log.warn("Environment not found. Using default JWT secret.");
            }

            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            String authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
            log.info("User authorities: {}", authorities);

            Date issuedAt = new Date();
            Date expiration = new Date(System.currentTimeMillis() + 3_000_000); // ~50 دقيقة
            log.info("JWT issued at: {}, expires at: {}", issuedAt, expiration);

            String jwt = Jwts.builder()
                    .setIssuer("Masala")
                    .setSubject(authentication.getName())
                    .claim("username", authentication.getName())
                    .claim("authorities", authorities)
                    .setIssuedAt(issuedAt)
                    .setExpiration(expiration)
                    .signWith(secretKey)
                    .compact();

            response.setHeader(ApplicationConstants.JWT_HEADER, jwt);
            log.info("JWT token generated and set in response header.");
        } else {
            log.warn("No authentication found in SecurityContext.");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/auth/login");
    }
}
