package org.example.ecommerce.application.service.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.domain.model.user.exception.UserNotFoundException;
import org.example.ecommerce.domain.model.user.repository.UserRepository;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.example.ecommerce.infrastructure.mapper.UserMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component(GetUserByType.EMAIL_VALUE)
@RequiredArgsConstructor
@Slf4j
public class FindUserByEmailStrategy implements GetUserStrategy {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MessageSource messageSource;
    @Override
    public Optional<UserProfile> getUser(String input) throws UserNotFoundException {
        log.info("Searching for user with user Email: {}", input);
        Optional<UserProfile> user = userRepository.findByEmail(input)
                .map(userMapper::toUserProfile);
        if (user.isEmpty()) {
            log.error("User not found with user Email: {}", input);
            String errorMessage = messageSource.getMessage("user.notfound.email",
                    new Object[]{input},
                    LocaleContextHolder.getLocale());
            throw new UserNotFoundException(errorMessage);
        }
        log.debug("User found: {}", user);
        return user;
    }
}
