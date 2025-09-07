package org.example.ecommerce.domain.common.repository;

import org.example.ecommerce.domain.common.FailedLoginAttempt;

import java.util.Optional;

/**
 * Repository interface for managing {@link FailedLoginAttempt} entities.
 *
 * <p>This interface defines methods to interact with the persistence layer
 * for storing and retrieving failed login attempt records.</p>
 *
 * <p>Typically, an implementation of this interface will extend
 * Spring Data JPA's {@code JpaRepository} or a similar persistence abstraction.</p>
 */
public interface FailedLoginAttemptRepository {

    /**
     * Finds a {@link FailedLoginAttempt} entity by the associated email.
     *
     * @param email the email of the user
     * @return an {@link Optional} containing the {@link FailedLoginAttempt}
     *         if found, or {@link Optional#empty()} if no record exists
     */
    Optional<FailedLoginAttempt> findByEmail(String email);

    /**
     * Saves the given {@link FailedLoginAttempt} entity and flushes the changes immediately.
     *
     * <p>Flushing ensures that the data is persisted to the database
     * within the current transaction.</p>
     *
     * @param failedLoginAttempt the entity to save
     * @return the saved {@link FailedLoginAttempt} entity
     */
    FailedLoginAttempt saveAndFlush(FailedLoginAttempt failedLoginAttempt);
}
