package org.example.ecommerce.domain.model.user.repository;

import org.example.ecommerce.domain.model.user.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    void deleteById(Long id);
    Optional<User> findByPhoneNumber(String phone);
}