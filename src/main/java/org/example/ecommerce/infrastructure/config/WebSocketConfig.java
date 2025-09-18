package org.example.ecommerce.infrastructure.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.ecommerce.infrastructure.config.constant.ApplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // في الإنتاج، استخدم domains محددة
                .withSockJS();
        logger.info("STOMP endpoint /ws registered with SockJS support");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // تمكين Simple Broker للموضوعات
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
        logger.info("Message broker configured: /topic, /queue, /app, /user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    logger.info("WebSocket CONNECT attempt received");

                    String authToken = accessor.getFirstNativeHeader("Authorization");
                    logger.info("Authorization header present: {}", authToken != null);

                    if (authToken == null || !authToken.startsWith("Bearer ")) {
                        logger.warn("Missing or invalid Authorization header format");
                        throw new IllegalArgumentException("Missing or invalid Authorization header. Expected format: Bearer <token>");
                    }

                    try {
                        String token = authToken.substring("Bearer ".length()).trim();
                        logger.info("Extracted JWT token length: {}", token.length());

                        SecretKey key = Keys.hmacShaKeyFor(
                                ApplicationConstants.JWT_SECRET_DEFAULT_VALUE.getBytes(StandardCharsets.UTF_8)
                        );

                        Claims claims = Jwts.parser()
                                .verifyWith(key)
                                .build()
                                .parseSignedClaims(token)
                                .getPayload();

                        String username = claims.getSubject();
                        String roles = claims.get("authorities", String.class);

                        logger.info("JWT validated for user: {}", username);
                        logger.info("User roles: {}", roles);

                        if (username == null || username.trim().isEmpty()) {
                            throw new IllegalArgumentException("JWT subject (username) is null or empty");
                        }

                        // التحقق من الأدوار - يمكن تعديل هذا حسب المتطلبات
                        List<SimpleGrantedAuthority> authorities;
                        if (roles != null && !roles.trim().isEmpty()) {
                            authorities = Arrays.stream(roles.split(","))
                                    .map(String::trim)
                                    .filter(role -> !role.isEmpty())
                                    .map(SimpleGrantedAuthority::new)
                                    .toList();
                        } else {
                            // إذا لم تكن هناك أدوار، يمكن إعطاء دور افتراضي
                            authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                        }

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(username, null, authorities);

                        accessor.setUser(authentication);
                        logger.info("WebSocket authentication set for user: {} with {} authorities",
                                username, authorities.size());

                    } catch (Exception e) {
                        logger.error("JWT validation failed: {}", e.getMessage());
                        throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage(), e);
                    }
                }

                return message;
            }
        });
    }
}