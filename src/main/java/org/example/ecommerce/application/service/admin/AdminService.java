package org.example.ecommerce.application.service.admin;

import org.example.ecommerce.domain.model.seller.AccountStatus;
import org.example.ecommerce.domain.model.seller.Seller;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.example.ecommerce.infrastructure.dto.user.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * AdminService defines operations that can only be performed by users
 * with the ADMIN role. It provides management features for both
 * User and Seller entities, such as searching, updating, verifying,
 * and deleting accounts.
 *
 * Security: All methods require ROLE_ADMIN.
 */
@PreAuthorize("hasRole('ROLE_ADMIN')")
public interface AdminService {

    /**
     * Find a user profile by email.
     *
     * @param email the user's email
     * @return an Optional containing the UserProfile if found
     */
    Optional<UserProfile> findUserByEmail(String email);

    /**
     * Find a user profile by ID.
     *
     * @param id the user's ID (as a String)
     * @return an Optional containing the UserProfile if found
     */
    Optional<UserProfile> findUserById(String id);

    /**
     * Find a user profile by phone number.
     *
     * @param phoneNumber the user's phone number
     * @return an Optional containing the UserProfile if found
     */
    Optional<UserProfile> findUserByPhoneNumber(String phoneNumber);

    /**
     * Update a user's profile by email.
     *
     * @param userDTO DTO containing updated user details
     * @param email   the user's email
     * @return an Optional containing the updated UserProfile if successful
     */
    Optional<UserProfile> updateUserByEmail(UserUpdateDto userDTO, String email);

    /**
     * Update a user's profile by phone number.
     *
     * @param userDTO     DTO containing updated user details
     * @param phoneNumber the user's phone number
     * @return an Optional containing the updated UserProfile if successful
     */
    Optional<UserProfile> updateUserByPhone(UserUpdateDto userDTO, String phoneNumber);

    /**
     * Update a user's profile by ID.
     *
     * @param userDTO DTO containing updated user details
     * @param id      the user's ID
     * @return an Optional containing the updated UserProfile if successful
     */
    Optional<UserProfile> updateUserById(UserUpdateDto userDTO, Long id);

    /**
     * Retrieve a paginated and sorted list of users.
     *
     * @param page      page number (0-based)
     * @param size      number of records per page
     * @param sortBy    field to sort by
     * @param direction sort direction (ASC or DESC)
     * @return a Page of UserProfiles
     */
    Page<UserProfile> findAllUsersSorted(int page, int size, String sortBy, String direction);

    /**
     * Find a seller profile by email.
     *
     * @param email the seller's email
     * @return an Optional containing the SellerProfile if found
     */
    Optional<SellerProfile> findSellerByEmail(String email);

    /**
     * Find a seller profile by ID.
     *
     * @param id the seller's ID (as a String)
     * @return an Optional containing the SellerProfile if found
     */
    Optional<SellerProfile> findSellerById(String id);

    /**
     * Find a seller profile by phone number.
     *
     * @param phoneNumber the seller's phone number
     * @return an Optional containing the SellerProfile if found
     */
    Optional<SellerProfile> findBySellerPhoneNumber(String phoneNumber);

    /**
     * Retrieve a paginated and sorted list of sellers.
     *
     * @param page      page number (0-based)
     * @param size      number of records per page
     * @param sortBy    field to sort by
     * @param direction sort direction (ASC or DESC)
     * @return a Page of SellerProfiles
     */
    Page<SellerProfile> findAllSellersSorted(int page, int size, String sortBy, String direction);

    /**
     * Verify a seller's email address.
     *
     * @param id            the seller's ID
     * @param emailVerified true if the email is verified, false otherwise
     * @return true if the verification status was updated
     */
    Boolean verifySeller(Long id, Boolean emailVerified);

    /**
     * Delete a seller by ID.
     *
     * @param id the seller's ID
     */
    void deleteSellerById(Long id);

    /**
     * Update the account status of a seller (e.g., ACTIVE, SUSPENDED).
     *
     * @param id     the seller's ID
     * @param status the new account status
     * @return an Optional containing the updated Seller if successful
     */
    Optional<Seller> updateSellerAccountStatus(Long id, AccountStatus status);
}
