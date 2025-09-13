package org.example.ecommerce.application.service.authentication.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.authentication.Authentication;
import org.example.ecommerce.application.service.jwt.JwtService;
import org.example.ecommerce.domain.model.user.Authority;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.model.user.UserRole;
import org.example.ecommerce.domain.model.user.exception.UserAlreadyExistsException;
import org.example.ecommerce.domain.model.user.repository.UserRepository;
import org.example.ecommerce.infrastructure.dto.user.LoginRequestWithPhoneNumber;
import org.example.ecommerce.infrastructure.response.ApiResponse;
import org.example.ecommerce.infrastructure.dto.user.LoginRequestWithEmail;
import org.example.ecommerce.infrastructure.dto.user.SignUpRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationImpl implements Authentication {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MessageSource messageSource;
    private final LoginContext loginContext;

    @Override
    @Transactional
    public Optional<ApiResponse> createCustomer(SignUpRequest request ) {
        log.info("Create Customer Request");

        SignUpRequest signUpRequest = new SignUpRequest(
                request.email(),
                request.password(),
                request.fullName(),
                request.phoneNumber(),
                request.imageUrl()
        );

        if (userRepository.findByEmail(signUpRequest.email()).isPresent()) {
            log.error("User with email already exists: {}", signUpRequest.email());
            String msg = messageSource.getMessage("user.exists.email", null, LocaleContextHolder.getLocale());
            throw new UserAlreadyExistsException(msg);
        }
        if (userRepository.findByPhoneNumber(signUpRequest.phoneNumber()).isPresent()) {
            log.error("User phone number already exists: {}", signUpRequest.phoneNumber());
            String msg = messageSource.getMessage("user.exists.phone", null, LocaleContextHolder.getLocale());
            throw new UserAlreadyExistsException(msg);
        }

        User user = new User();
        log.info("Initializing User entity...");
        user.setEmail(signUpRequest.email());
        log.debug("User Email: {}", signUpRequest.email());
        user.setPassword(passwordEncoder.encode(signUpRequest.password()));
        user.setFullName(signUpRequest.fullName());
        log.debug("User FullName: {}", signUpRequest.fullName());
        user.setPhoneNumber(signUpRequest.phoneNumber());
        log.debug("User Phone: {}", signUpRequest.phoneNumber());
        user.setImageUrl(request.imageUrl());
        log.debug("User Image URL: {}", request.imageUrl());

        Authority authority = new Authority();
        authority.setRole(UserRole.ROLE_CUSTOMER);
        authority.setCustomer(user);
        user.getAuthorities().add(authority);

        userRepository.save(user);


        userRepository.save(user);
        log.info("User saved successfully: {}", user.getEmail());

        return Optional.of(new ApiResponse("true", "User registered successfully", jwtService.generateToken(user)));
    }

    // login with email
    @Override
    public Optional<ApiResponse> loginWithEmail(LoginRequestWithEmail request) {
        log.info("Login attempt with email: {}", request.email());
        return Optional.of(loginContext.login(request.email(), request.password()));
    }

    // login with phone number
    @Override
    public Optional<ApiResponse> loginWithPhoneNumber(LoginRequestWithPhoneNumber request) {
        log.info("Login attempt with phone: {}", request.userPhoneNumber());
        return Optional.of(loginContext.login(request.userPhoneNumber(), request.password()));
    }

    @Override
    public Optional<ApiResponse> loginWithOtp(LoginRequestWithEmail request) {
        log.info("Login with OTP is not implemented yet for email: {}", request.email());
        return Optional.empty();
    }

    @Override
    public String getFcmTokenByPhone(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .map(User::getFcmToken)
                .orElse(null);
    }
}
