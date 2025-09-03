package org.example.ecommerce.infrastructure.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.model.user.UserRole;
import org.example.ecommerce.domain.model.user.repository.UserRepository;
import org.example.ecommerce.infrastructure.config.constant.ApplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidatorFilter.class);

    private final UserRepository userRepository;

    // Constructor injection بدلاً من @Autowired
    public JwtTokenValidatorFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = request.getHeader(ApplicationConstants.JWT_HEADER);
        logger.info("Received JWT header: {}", jwt);

        if (jwt != null) {
            try {
                if (jwt.startsWith("Bearer ")) {
                    jwt = jwt.substring(7);
                    logger.info("Removed 'Bearer ' prefix: {}", jwt);
                }

                Environment environment = getEnvironment();
                String secret;
                if (environment != null) {
                    secret = environment.getProperty(
                            ApplicationConstants.JWT_SECRET_KEY,
                            ApplicationConstants.JWT_SECRET_DEFAULT_VALUE
                    );
                } else {
                    secret = ApplicationConstants.JWT_SECRET_DEFAULT_VALUE;
                }
                logger.debug("Using secret key: {}", secret.length() > 5 ? secret.substring(0, 5) + "..." : secret);

                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                logger.info("SecretKey created successfully");

                Claims claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(jwt)
                        .getPayload();
                logger.info("Claims parsed: {}", claims);

                String email = claims.getSubject();
                if (email == null) {
                    email = String.valueOf(claims.get("username"));
                }
                logger.info("Email extracted from claims: {}", email);

                // التحقق من وجود المستخدم في قاعدة البيانات
                Optional<User> userOptional = userRepository.findByEmail(email);
                if (!userOptional.isPresent()) {
                    logger.warn("User not found in database: {}", email);
                    throw new BadCredentialsException("User not found in database");
                }

                User user = userOptional.get();
                logger.info("User found: {}", user.getEmail());

                String authoritiesClaim = claims.get("authorities", String.class);
                logger.debug("Authorities claim: {}", authoritiesClaim);

                List<GrantedAuthority> grantedAuthorities;
                if (authoritiesClaim != null && !authoritiesClaim.isEmpty()) {
                    grantedAuthorities = Arrays.stream(authoritiesClaim.split(","))
                            .filter(auth -> auth != null && !auth.trim().isEmpty())
                            .map(auth -> auth.startsWith("ROLE_") ?
                                    new SimpleGrantedAuthority(auth) : new SimpleGrantedAuthority("ROLE_" + auth))
                            .collect(Collectors.toList());
                    logger.info("Granted authorities from token: {}", grantedAuthorities);
                } else {
                    // استخدام الأدوار من قاعدة البيانات بدلاً من الافتراضي
                    if (user.getAuthorities() != null && !user.getAuthorities().isEmpty()) {
                        grantedAuthorities = user.getAuthorities().stream()
                                .map(authority -> new SimpleGrantedAuthority(authority.getRole().name()))
                                .collect(Collectors.toList());
                    } else {
                        // في حالة عدم وجود أدوار، استخدام الدور الافتراضي
                        grantedAuthorities = Arrays.asList(
                                new SimpleGrantedAuthority(UserRole.ROLE_CUSTOMER.name())
                        );
                    }
                    logger.info("Granted authorities from DB or default: {}", grantedAuthorities);
                }

                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, grantedAuthorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authentication set in SecurityContext");

            } catch (Exception e) {
                logger.error("Invalid JWT token or user verification failed", e);
                throw new BadCredentialsException("Invalid JWT token or user verification failed", e);
            }
        } else {
            logger.debug("No JWT header found");
        }

        filterChain.doFilter(request, response);
        logger.debug("Filter chain continued");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean skip = path.startsWith("/api/v1/auth");
        logger.debug("shouldNotFilter? {} for path: {}", skip, path);
        return skip;
    }

}