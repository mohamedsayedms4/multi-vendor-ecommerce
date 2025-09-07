package org.example.ecommerce.application.service.user;

import org.example.ecommerce.infrastructure.dto.UserChangeUserPWDDto;
import org.example.ecommerce.infrastructure.dto.UserUpdateImageProfile;
import org.example.ecommerce.infrastructure.dto.user.UserUpdateDto;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Service interface for managing user-related operations.
 * Defines both admin-level and user-level methods such as
 * finding users, updating profiles, changing passwords,
 * and deleting accounts.
 */
public interface UserService {

    // ================== For Admin ==================

    /**
     * Find a user by email (Admin only).
     *
     * @param email the email of the user
     * @return an Optional containing {@link UserProfile} if found
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<UserProfile> findByEmail(String email);

    /**
     * Find a user by ID (Admin only).
     *
     * @param id the ID of the user
     * @return an Optional containing {@link UserProfile} if found
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<UserProfile> findById(String id);

    /**
     * Find a user by phone number (Admin only).
     *
     * @param phoneNumber the phone number of the user
     * @return an Optional containing {@link UserProfile} if found
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<UserProfile> findByPhoneNumber(String phoneNumber);

    // ================== For User ==================

    /**
     * Find a user profile using JWT token.
     *
     * @param jwt the JWT token
     * @return an Optional containing {@link UserProfile}
     */
    Optional<UserProfile> findByJwt(String jwt);

    /**
     * Update user information using JWT token.
     *
     * @param userDTO the DTO containing updated user info
     * @param jwt     the JWT token
     * @return an Optional containing the updated {@link UserProfile}
     */
    Optional<UserProfile> updateUserByJwt(UserUpdateDto userDTO, String jwt);

    // ================== For Admin only ==================

    /**
     * Update user information by email (Admin only).
     *
     * @param userDTO the DTO containing updated user info
     * @param email   the email of the user
     * @return an Optional containing the updated {@link UserProfile}
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    Optional<UserProfile> updateUserByEmail(UserUpdateDto userDTO, String email);

    /**
     * Update user information by phone number (Admin only).
     *
     * @param userDTO     the DTO containing updated user info
     * @param phoneNumber the phone number of the user
     * @return an Optional containing the updated {@link UserProfile}
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<UserProfile> updateUserByPhone(UserUpdateDto userDTO, String phoneNumber);

    /**
     * Update user information by ID (Admin only).
     *
     * @param userDTO the DTO containing updated user info
     * @param id      the ID of the user
     * @return an Optional containing the updated {@link UserProfile}
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<UserProfile> updateUserById(UserUpdateDto userDTO, Long id);

    // ================== Common operations ==================

    /**
     * Delete the profile image of a user.
     *
     * @param email the email of the user
     * @return true if image was deleted, false otherwise
     */
    Boolean deleteImageProfile(String email);

    /**
     * Update the profile image of a user.
     *
     * @param imageUrl the new image URL
     * @param email    the email of the user
     * @return an Optional containing updated {@link UserUpdateImageProfile}
     */
    Optional<UserUpdateImageProfile> updateProfileImage(String imageUrl, String email);

    /**
     * Update the password of a user.
     *
     * @param changeUserPWD DTO containing email, old password, and new password
     * @return true if password updated successfully
     */
    Boolean updatePassword(UserChangeUserPWDDto changeUserPWD);

    /**
     * Delete a user by ID.
     *
     * @param id the ID of the user
     */
    void deleteUser(Long id);

    /**
     * Get the user ID from email.
     *
     * @param email the email of the user
     * @return the ID of the user
     */
    Long userId(String email);
}
