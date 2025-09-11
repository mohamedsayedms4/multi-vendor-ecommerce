package org.example.ecommerce.domain.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a record of failed login attempts for a specific user.
 * <p>
 * This entity is used to:
 * <ul>
 *   <li>Track the number of consecutive failed login attempts for a user.</li>
 *   <li>Store the timestamp of the last failed login attempt.</li>
 *   <li>Help enforce account lockout policies after exceeding the maximum allowed attempts.</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "failed_login_attempts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailedLoginAttempt {

    /**
     * Primary key (auto-generated).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique email associated with the user.
     * This is used to identify the account experiencing failed login attempts.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Number of failed login attempts for this user.
     */
    @Column(nullable = false)
    private int attempts;

    /**
     * Timestamp of the last failed login attempt.
     * Used for checking lockout duration.
     */
    private LocalDateTime lastAttemptTime;


}
