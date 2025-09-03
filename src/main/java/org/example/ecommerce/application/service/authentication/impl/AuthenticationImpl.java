package org.example.ecommerce.application.service.authentication.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.authentication.Authentication;
import org.example.ecommerce.application.service.jwt.JwtService;
import org.example.ecommerce.domain.model.user.Authority;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.model.user.UserRole;
import org.example.ecommerce.domain.model.user.exception.UserAlreadyExistsException;
import org.example.ecommerce.domain.model.user.repository.UserRepository;
import org.example.ecommerce.infrastructure.api.ApiResponse;
import org.example.ecommerce.infrastructure.dto.user.LoginRequest;
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



    @Override
    public Optional<ApiResponse> createCustomer(SignUpRequest request) {
        log.info("Create Customer Request ");
        SignUpRequest signUpRequest =new SignUpRequest(
                request.userEmail(),
                request.userPassword(),
                request.userFullName(),
                request.userPhoneNumber(),
                request.userImageUrl()
        );

        if (userRepository.findByEmail(signUpRequest.userEmail()).isPresent()) {
            log.error("User with email already exists :{}", signUpRequest.userEmail());
            String msg = messageSource.getMessage("user.exists.email", null, LocaleContextHolder.getLocale());
            throw new UserAlreadyExistsException(msg);        }
        if(userRepository.findByPhoneNumber( signUpRequest.userPhoneNumber()).isPresent()){
            log.error("User phone number already exists :{}", signUpRequest.userPhoneNumber());
            String msg = messageSource.getMessage("user.exists.phone", null, LocaleContextHolder.getLocale());
            throw new UserAlreadyExistsException(msg);
        }
        User user = new User();
        user.setEmail(signUpRequest.userEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.userPassword()));
        user.setFullName(signUpRequest.userFullName());
        user.setPhoneNumber(signUpRequest.userPhoneNumber());
        user.setImageUrl(signUpRequest.userImageUrl());
        Authority authority = new Authority();
        authority.setRole(UserRole.ROLE_CUSTOMER);
        user.getAuthorities().add(authority);

        userRepository.save(user);


        return Optional.of(new ApiResponse("true","User registered successfully", jwtService.generateToken(user)));

    }

//TODO
    @Override
    public Optional<ApiResponse> login(LoginRequest request) {
        return Optional.empty();
    }
//TODO

    @Override
    public Optional<ApiResponse> loginWithOtp(LoginRequest request) {
        return Optional.empty();
    }
}
