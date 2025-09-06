package org.example.ecommerce.application.service.user.impl;

import org.example.ecommerce.domain.model.user.exception.UserNotFoundException;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component(GetUserByType.PHONE_VALUE)
public class FindUserByPhoneStrategy implements GetUserStrategy {
    @Override
    public Optional<UserProfile> getUser(String input) throws UserNotFoundException {
        return Optional.empty();
    }
}
