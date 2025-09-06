package org.example.ecommerce.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.domain.model.user.repository.UserRepository;
import org.example.ecommerce.infrastructure.exception.CustomAccessDeniedHandler;
import org.example.ecommerce.infrastructure.exception.CustomBasicAuthenticationEntryPoint;
import org.example.ecommerce.infrastructure.filter.AuthoritiesLoggingAfterFilter;
import org.example.ecommerce.infrastructure.filter.JwtTokenGeneratorFilter;
import org.example.ecommerce.infrastructure.filter.JwtTokenValidatorFilter;
import org.example.ecommerce.infrastructure.filter.RequestValidationBeforeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

/**
 * This configuration class defines the security setup for the application.
 * It configures the HTTP security rules, creates an in-memory user, and sets up password encoding.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class ProjectSecurityConfig {
    private final UserRepository userRepository;


    CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();
    /**
     * Defines the security filter chain, specifying authorization rules for HTTP requests.

     * - Paths "/myAccount", "/myBalance", "/myLoans", "/myCards" require authentication.
     * - Paths "/notices", "/contact", "/error" are publicly accessible.
     * - Disables form-based login.
     * - Enables HTTP Basic authentication.
     *
     * @param http HttpSecurity object to configure HTTP security.
     * @return the configured SecurityFilterChain bean.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    @Order
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .cors(corsConfig -> corsConfig.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Collections.singletonList("http://127.0.0.1:5500")); // origin واضح
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // السماح بكل الطرق المهمة
                    config.setAllowedHeaders(Arrays.asList("*")); // السماح بأي header
                    config.setExposedHeaders(Arrays.asList("Authorization")); // إظهار Authorization للمتصفح
                    config.setAllowCredentials(true); // السماح بالكوكيز و Authorization header
                    config.setMaxAge(3600L); // مدة صلاحية preflight
                    return config;
                }))




                .csrf(csrfConfig -> csrfConfig.disable())
//                .csrf(csrfConfig -> csrfConfig
//                        .ignoringRequestMatchers("/auth/login")
//                        .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
//                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new JwtTokenGeneratorFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new JwtTokenValidatorFilter(userRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new RequestValidationBeforeFilter() , BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterFilter() , BasicAuthenticationFilter.class)



                .requiresChannel(rcc -> rcc.anyRequest().requiresInsecure())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/user/**").authenticated()
                        .anyRequest().authenticated()

        );
        // Disable default Spring Security form login page
        http.formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.disable());
        // Enable HTTP Basic authentication
        http.exceptionHandling(exceptions ->exceptions.accessDeniedHandler(new CustomAccessDeniedHandler()));

        http.httpBasic(httpSecurityHttpBasicConfigurer ->
                httpSecurityHttpBasicConfigurer.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
        return http.build();
    }



    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        EcommerceUserNamePasswordAuthenticationProvider authenticationProvider =
                new EcommerceUserNamePasswordAuthenticationProvider(userDetailsService, passwordEncoder);
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return  providerManager;
    }
//    @Bean
//    public CompromisedPasswordChecker compromisedPasswordChecker() {
//        return new HaveIBeenPwnedRestApiPasswordChecker();
//    }
}
