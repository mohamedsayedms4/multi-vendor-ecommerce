package org.example.ecommerce.infrastructure.repository;

import org.example.ecommerce.domain.model.user.FailedLoginAttempt;
import org.example.ecommerce.domain.model.user.repository.FailedLoginAttemptRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaFailedLoginAttemptRepository
        extends JpaRepository<FailedLoginAttempt, Long>, FailedLoginAttemptRepository {

    Optional<FailedLoginAttempt> findByEmail(String email); // Spring Data JPA سيطبقها تلقائياً
}
