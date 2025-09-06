package org.example.ecommerce.application.service.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.model.user.exception.UserNotFoundException;
import org.example.ecommerce.domain.model.user.repository.UserRepository;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.example.ecommerce.infrastructure.mapper.UserMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component(GetUserByType.ID_VALUE)
@Slf4j
@RequiredArgsConstructor
public class FindUserByIdStrategy implements GetUserStrategy {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MessageSource messageSource;

    @Override
    public Optional<UserProfile> getUser(String input) throws UserNotFoundException {
        Long id = Long.valueOf(input);
        log.info("Searching for user with id: {}", id);
        Optional<UserProfile> user = userRepository.findById(id).map(userMapper::toUserProfile);

        if (user.isEmpty()) {
            log.error("User not found for id: {}", id);
            String errorMessage = messageSource.getMessage(
                    "error.user.notfound.id",
                    new Object[]{id},
                    LocaleContextHolder.getLocale()
            );
            throw new UserNotFoundException(errorMessage);
        }
        log.debug("User found: {}", user);


        return user;
    }
}
