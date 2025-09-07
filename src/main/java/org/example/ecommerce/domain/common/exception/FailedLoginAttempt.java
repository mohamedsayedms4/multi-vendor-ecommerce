package org.example.ecommerce.domain.common.exception;

/**
 * Custom exception thrown when a user exceeds the maximum allowed
 * number of failed login attempts and becomes temporarily blocked.
 *
 * <p>This exception is typically used by the authentication
 * and login attempt services to enforce account lockout policies.</p>
 */
public class FailedLoginAttempt extends RuntimeException {

    /**
     * Constructs a new {@code FailedLoginAttempt} exception with the specified detail message.
     *
     * @param message the detail message that provides information about the lockout reason
     */
    public FailedLoginAttempt(String message) {
        super(message);
    }
}
