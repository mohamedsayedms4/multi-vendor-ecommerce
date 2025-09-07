package org.example.ecommerce.infrastructure.repository;

import org.example.ecommerce.domain.common.FailedLoginAttempt;
import org.example.ecommerce.domain.common.repository.FailedLoginAttemptRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaFailedLoginAttemptRepository
        extends JpaRepository<FailedLoginAttempt, Long>, FailedLoginAttemptRepository

    {
    Optional<FailedLoginAttempt> findByEmail(String email);


}
