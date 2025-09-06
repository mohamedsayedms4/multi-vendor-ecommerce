package org.example.ecommerce.application.service.user.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.user.LoginAttemptService;
import org.example.ecommerce.domain.model.user.FailedLoginAttempt;
import org.example.ecommerce.domain.model.user.repository.FailedLoginAttemptRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final FailedLoginAttemptRepository repository;

    private static final int MAX_ATTEMPTS = 3; // عدد المحاولات قبل الحظر
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(15); // مدة الحظر

    @Override
    public void loginFailed(String email) {
        LocalDateTime now = LocalDateTime.now();

        // جلب محاولة الدخول من DB أو إنشاء واحدة جديدة
        FailedLoginAttempt attempt = repository.findByEmail(email)
                .orElseGet(() -> {
                    FailedLoginAttempt newAttempt = new FailedLoginAttempt();
                    newAttempt.setEmail(email);
                    newAttempt.setAttempts(0);
                    newAttempt.setLastAttemptTime(now);
                    return newAttempt;
                });

        // إذا انتهت فترة الحظر، إعادة تعيين العداد
        if (attempt.getLastAttemptTime() == null ||
                attempt.getLastAttemptTime().isBefore(now.minus(BLOCK_DURATION))) {
            attempt.setAttempts(1);
        } else {
            attempt.setAttempts(attempt.getAttempts() + 1);
        }

        attempt.setLastAttemptTime(now);
        repository.save(attempt);

        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            log.warn("User [{}] is blocked until {}", email, attempt.getLastAttemptTime().plus(BLOCK_DURATION));
        } else {
            log.info("Failed login attempt [{}] for user [{}]", attempt.getAttempts(), email);
        }
    }

    @Override
    public boolean isBlocked(String email) {
        LocalDateTime now = LocalDateTime.now();

        return repository.findByEmail(email)
                .map(attempt -> attempt.getAttempts() >= MAX_ATTEMPTS &&
                        attempt.getLastAttemptTime().isAfter(now.minus(BLOCK_DURATION)))
                .orElse(false);
    }

    @Override
    public void resetAttempts(String email) {
        repository.findByEmail(email).ifPresent(attempt -> {
            attempt.setAttempts(0);
            attempt.setLastAttemptTime(null);
            repository.save(attempt);
            log.info("Reset login attempts for user [{}]", email);
        });
    }

   
}
