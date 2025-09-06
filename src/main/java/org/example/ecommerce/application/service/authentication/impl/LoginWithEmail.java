package org.example.ecommerce.application.service.authentication.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.jwt.JwtService;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.model.user.exception.UserNotFoundException;
import org.example.ecommerce.domain.model.user.repository.UserRepository;
import org.example.ecommerce.infrastructure.dto.user.LoginRequestWithEmail;
import org.example.ecommerce.infrastructure.response.ApiResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component(LoginTypes.LOGIN_WITH_EMAIL_VALUE)
public class LoginWithEmail extends AbstractLoginStrategy<LoginRequestWithEmail> {

    private final UserRepository userRepository;

    public LoginWithEmail(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          MessageSource messageSource) {
        super(passwordEncoder, jwtService, messageSource);
        this.userRepository = userRepository;
    }

    @Override
    public ApiResponse login(LoginRequestWithEmail request) {
        log.info("Login attempt with email {}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    String msg = messageSource.getMessage("user.notfound.email",
                            new Object[]{request.email()}, LocaleContextHolder.getLocale());
                    return new UserNotFoundException(msg);
                });

        validatePassword(request.password(), user.getPassword());
        return buildResponse(user, request.email());
    }
}
