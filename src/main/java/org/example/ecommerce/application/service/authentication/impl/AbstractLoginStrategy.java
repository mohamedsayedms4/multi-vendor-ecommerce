package org.example.ecommerce.application.service.authentication.impl;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.application.service.jwt.JwtService;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.model.user.UserRole;
import org.example.ecommerce.domain.model.user.exception.InvalidPWD;
import org.example.ecommerce.infrastructure.response.ApiResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public abstract class AbstractLoginStrategy<T> implements LoginStrategy<T> {

    protected final PasswordEncoder passwordEncoder;
    protected final JwtService jwtService;
    protected final MessageSource messageSource;

    protected void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            String msg = messageSource.getMessage("user.invalid.password", null, LocaleContextHolder.getLocale());
            throw new InvalidPWD(msg);
        }
    }

    protected ApiResponse buildResponse(User user, String identifier) {
        UserRole role = user.getAuthorities().iterator().next().getRole();
        String msg = messageSource.getMessage("user.login.success",
                new Object[]{role.name()}, LocaleContextHolder.getLocale());

        return new ApiResponse("true", msg, jwtService.generateToken(user));
    }
}
