package org.example.ecommerce.application.service.admin.impl.handeler;

import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.example.ecommerce.infrastructure.dto.user.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Service interface for admin-level operations on users.
 * All methods require the ADMIN role to be accessed.
 */
public interface UserServiceForAdmins {

    /**
     * Finds a user by their email address.
     *
     * @param email the user's email address
     * @return an Optional containing the user profile if found, otherwise empty
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<UserProfile> findByEmail(String email);

    /**
     * Finds a user by their unique ID.
     *
     * @param id the user's unique identifier (as String)
     * @return an Optional containing the user profile if found, otherwise empty
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<UserProfile> findById(String id);

    /**
     * Finds a user by their phone number.
     *
     * @param phoneNumber the user's phone number
     * @return an Optional containing the user profile if found, otherwise empty
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<UserProfile> findByPhoneNumber(String phoneNumber);

    /**
     * Updates a user profile based on their email.
     *
     * @param userDTO the user update data transfer object
     * @param email   the email of the user to be updated
     * @return an Optional containing the updated user profile if successful, otherwise empty
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    Optional<UserProfile> updateUserByEmail(UserUpdateDto userDTO, String email);

    /**
     * Updates a user profile based on their phone number.
     *
     * @param userDTO     the user update data transfer object
     * @param phoneNumber the phone number of the user to be updated
     * @return an Optional containing the updated user profile if successful, otherwise empty
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<UserProfile> updateUserByPhone(UserUpdateDto userDTO, String phoneNumber);

    /**
     * Updates a user profile based on their ID.
     *
     * @param userDTO the user update data transfer object
     * @param id      the ID of the user to be updated
     * @return an Optional containing the updated user profile if successful, otherwise empty
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<UserProfile> updateUserById(UserUpdateDto userDTO, Long id);

    /**
     * Retrieves a paginated and sorted list of all users.
     *
     * @param page      the page number to retrieve
     * @param size      the number of records per page
     * @param sortBy    the field to sort by
     * @param direction the sort direction ("asc" or "desc")
     * @return a paginated list of user profiles
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Page<UserProfile> findAllUsersSorted(int page, int size, String sortBy, String direction);
}
