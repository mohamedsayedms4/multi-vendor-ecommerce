package org.example.ecommerce.application.service.FailedLogin;

/**
 * Service interface for managing user login attempts.
 * <p>
 * This service is responsible for tracking failed login attempts,
 * blocking users after exceeding the allowed limit, and resetting attempts
 * after successful login or manual reset.
 * </p>
 */
public interface LoginAttemptService {

    /**
     * Records a failed login attempt for the given email.
     *
     * @param email the email of the user who failed to log in
     */
    void loginFailed(String email);

    /**
     * Checks if the user associated with the given email
     * is currently blocked due to too many failed login attempts.
     *
     * @param email the email of the user
     * @return true if the user is blocked, false otherwise
     */
    boolean isBlocked(String email);

    /**
     * Resets the failed login attempts for the given email.
     * Typically called after a successful login or admin action.
     *
     * @param email the email of the user
     */
    void resetAttempts(String email);
}
