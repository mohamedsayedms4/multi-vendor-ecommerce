package org.example.ecommerce.application.service.user.impl;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.domain.model.user.exception.UserNotFoundException;
import org.example.ecommerce.domain.model.user.repository.UserRepository;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.example.ecommerce.infrastructure.mapper.UserMapper;
import org.example.ecommerce.infrastructure.utils.JwtUtil;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component(GetUserByType.JWT_VALUE)
@RequiredArgsConstructor
public class FindUserByJwtTokenStrategy implements GetUserStrategy {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public Optional<UserProfile> getUser(String input) throws UserNotFoundException {
        Long id = jwtUtil.extractUserIdFromJwt(input);
        return  userRepository.findById(id)
                .map(userMapper::toUserProfile);
    }
}
