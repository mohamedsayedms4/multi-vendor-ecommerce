package org.example.ecommerce.application.service.user.impl;


import lombok.RequiredArgsConstructor;
import org.example.ecommerce.application.service.user.UserService;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final GetUserContext getUserContext ;
    @Override
    public Optional<UserProfile> findByEmail(String email) {
        return getUserContext.getUser(email,GetUserByType.EMAIL);
    }

    @Override
    public Optional<UserProfile> findById(String id) {
        return getUserContext.getUser(id,GetUserByType.ID);
    }

    @Override
    public Optional<UserProfile> findByPhoneNumber(String phoneNumber) {
        return getUserContext.getUser(phoneNumber,GetUserByType.PHONE);
    }

    @Override
    public Optional<UserProfile> findByJwt(String jwt) {
        return getUserContext.getUser(jwt,GetUserByType.JWT);
    }
}
