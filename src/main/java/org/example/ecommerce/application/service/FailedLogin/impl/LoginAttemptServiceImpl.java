package org.example.ecommerce.application.service.FailedLogin.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.FailedLogin.LoginAttemptService;
import org.example.ecommerce.domain.common.FailedLoginAttempt;
import org.example.ecommerce.domain.common.repository.FailedLoginAttemptRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Implementation of {@link LoginAttemptService}.
 * <p>
 * This service is responsible for handling failed login attempts,
 * blocking users after exceeding the maximum allowed attempts,
 * and resetting attempts when required.
 * </p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final FailedLoginAttemptRepository repository;

    /** Maximum number of failed attempts before blocking the user. */
    private static final int MAX_ATTEMPTS = 4;

    /** Duration for which the user will remain blocked after exceeding attempts. */
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(15);

    /**
     * Records a failed login attempt for the given email.
     * <p>
     * - If no previous record exists, a new one is created.
     * - If the block duration has expired, the counter is reset.
     * - If attempts exceed {@code MAX_ATTEMPTS}, the user is blocked.
     * </p>
     *
     * @param email the user’s email
     */
    @Override
    public void loginFailed(String email) {
        if (email == null || email.trim().isEmpty()) {
            log.warn("Cannot register failed login attempt for null or empty email");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        log.debug("Processing failed login attempt for user: {}", email);

        // Fetch or create new attempt
        FailedLoginAttempt attempt = repository.findByEmail(email)
                .orElseGet(() -> {
                    log.debug("Creating new failed login attempt record for user: {}", email);
                    FailedLoginAttempt newAttempt = new FailedLoginAttempt();
                    newAttempt.setEmail(email);
                    newAttempt.setAttempts(0);
                    newAttempt.setLastAttemptTime(now);
                    return newAttempt;
                });

        // Reset counter if block duration expired
        if (attempt.getLastAttemptTime() == null ||
                attempt.getLastAttemptTime().isBefore(now.minus(BLOCK_DURATION))) {
            log.debug("Resetting attempt counter for user [{}] - block period expired", email);
            attempt.setAttempts(1);
        } else {
            attempt.setAttempts(attempt.getAttempts() + 1);
        }

        attempt.setLastAttemptTime(now);

        // Save changes immediately
        try {
            repository.saveAndFlush(attempt);
            log.debug("Saved failed login attempt: ID={}, Email={}, Attempts={}",
                    attempt.getId(), attempt.getEmail(), attempt.getAttempts());
        } catch (Exception e) {
            log.error("Failed to save login attempt for user [{}]: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to record login attempt", e);
        }

        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            log.warn("User [{}] is now blocked until {} (attempts: {})",
                    email, attempt.getLastAttemptTime().plus(BLOCK_DURATION), attempt.getAttempts());
        } else {
            log.info("Failed login attempt #{} for user [{}]", attempt.getAttempts(), email);
        }
    }

    /**
     * Checks whether a user is blocked.
     * <p>
     * A user is considered blocked if the number of failed attempts
     * is greater than or equal to {@code MAX_ATTEMPTS}
     * and the block duration has not yet expired.
     * </p>
     *
     * @param email the user’s email
     * @return true if blocked, false otherwise
     */
    @Override
    public boolean isBlocked(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        boolean blocked = repository.findByEmail(email)
                .map(attempt -> attempt.getAttempts() >= MAX_ATTEMPTS &&
                        attempt.getLastAttemptTime().isAfter(now.minus(BLOCK_DURATION)))
                .orElse(false);

        log.debug("Block status for user [{}]: {}", email, blocked);
        return blocked;
    }

    /**
     * Resets failed login attempts for the given email.
     * <p>
     * Typically called after a successful login or when an admin resets attempts manually.
     * </p>
     *
     * @param email the user’s email
     */
    @Override
    public void resetAttempts(String email) {
        if (email == null || email.trim().isEmpty()) {
            log.warn("Cannot reset attempts for null or empty email");
            return;
        }

        repository.findByEmail(email).ifPresent(attempt -> {
            log.debug("Resetting attempts for user [{}] - previous attempts: {}",
                    email, attempt.getAttempts());

            attempt.setAttempts(0);
            attempt.setLastAttemptTime(null);

            try {
                repository.saveAndFlush(attempt);
                log.info("Successfully reset login attempts for user [{}]", email);
            } catch (Exception e) {
                log.error("Failed to reset login attempts for user [{}]: {}", email, e.getMessage(), e);
                throw new RuntimeException("Failed to reset login attempts", e);
            }
        });
    }
}
