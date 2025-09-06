package org.example.ecommerce.infrastructure.repository;

import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.model.user.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {
    @Override
    Optional<User> findByEmail(String email);

    @Override
    Optional<User> findByPhoneNumber(String phoneNumber);


}