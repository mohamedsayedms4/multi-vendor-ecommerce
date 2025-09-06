package org.example.ecommerce.domain.model.user.repository;

import org.example.ecommerce.domain.model.user.FailedLoginAttempt;

import java.util.Optional;

public interface FailedLoginAttemptRepository {
    Optional<FailedLoginAttempt> findByEmail(String email);
    FailedLoginAttempt save(FailedLoginAttempt failedLoginAttempt);
}
