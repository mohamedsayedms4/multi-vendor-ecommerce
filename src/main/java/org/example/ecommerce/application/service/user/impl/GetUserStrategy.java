package org.example.ecommerce.application.service.user.impl;

import org.example.ecommerce.domain.model.user.exception.UserNotFoundException;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;

import java.util.Optional;

public interface GetUserStrategy {
    Optional<UserProfile> getUser(String input) throws UserNotFoundException;

}
