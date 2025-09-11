package org.example.ecommerce.application.service.admin.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.admin.AdminService;
import org.example.ecommerce.application.service.admin.impl.handeler.SellerServiceForAdmins;
import org.example.ecommerce.application.service.admin.impl.handeler.UserServiceForAdmins;
import org.example.ecommerce.domain.model.seller.AccountStatus;
import org.example.ecommerce.domain.model.seller.Seller;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.example.ecommerce.infrastructure.dto.user.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link AdminService}.
 * <p>
 * This service provides administrative functionalities to manage
 * both {@link UserProfile} and {@link SellerProfile}.
 * It acts as a façade delegating calls to {@link UserServiceForAdmins}
 * and {@link SellerServiceForAdmins}.
 * </p>
 *
 * <ul>
 *     <li>Manage users (find, update, list)</li>
 *     <li>Manage sellers (find, verify, update status, delete)</li>
 *     <li>Supports pagination and sorting for bulk retrieval</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserServiceForAdmins userServiceForAdmins;
    private final SellerServiceForAdmins sellerServiceForAdmins;

    // ------------------------- User Management -------------------------

    /**
     * Find a user by email.
     *
     * @param email the user email
     * @return an optional containing the user profile if found
     */
    @Override
    public Optional<UserProfile> findUserByEmail(String email) {
        return userServiceForAdmins.findByEmail(email);
    }

    /**
     * Find a user by unique ID.
     *
     * @param id the user ID (string representation)
     * @return an optional containing the user profile if found
     */
    @Override
    public Optional<UserProfile> findUserById(String id) {
        return userServiceForAdmins.findById(id);
    }

    /**
     * Find a user by phone number.
     *
     * @param phoneNumber the user phone number
     * @return an optional containing the user profile if found
     */
    @Override
    public Optional<UserProfile> findUserByPhoneNumber(String phoneNumber) {
        return userServiceForAdmins.findByPhoneNumber(phoneNumber);
    }

    /**
     * Update user details using their email as a lookup key.
     *
     * @param userDTO the user update data
     * @param email   the email of the user to update
     * @return an optional containing the updated user profile
     */
    @Override
    public Optional<UserProfile> updateUserByEmail(UserUpdateDto userDTO, String email) {
        return userServiceForAdmins.updateUserByEmail(userDTO, email);
    }

    /**
     * Update user details using their phone number as a lookup key.
     *
     * @param userDTO     the user update data
     * @param phoneNumber the phone number of the user to update
     * @return an optional containing the updated user profile
     */
    @Override
    public Optional<UserProfile> updateUserByPhone(UserUpdateDto userDTO, String phoneNumber) {
        return userServiceForAdmins.updateUserByPhone(userDTO, phoneNumber);
    }

    /**
     * Update user details using their ID as a lookup key.
     *
     * @param userDTO the user update data
     * @param id      the ID of the user to update
     * @return an optional containing the updated user profile
     */
    @Override
    public Optional<UserProfile> updateUserById(UserUpdateDto userDTO, Long id) {
        return userServiceForAdmins.updateUserById(userDTO, id);
    }

    /**
     * Retrieve all users with dynamic sorting and pagination.
     *
     * @param page      page number (zero-based)
     * @param size      number of records per page
     * @param sortBy    field to sort by
     * @param direction sort direction ("asc" or "desc")
     * @return a page of sorted user profiles
     */
    @Override
    public Page<UserProfile> findAllUsersSorted(int page, int size, String sortBy, String direction) {
        return userServiceForAdmins.findAllUsersSorted(page, size, sortBy, direction);
    }

    // ------------------------- Seller Management -------------------------

    /**
     * Find a seller by email.
     *
     * @param email the seller email
     * @return an optional containing the seller profile if found
     */
    @Override
    public Optional<SellerProfile> findSellerByEmail(String email) {
        return sellerServiceForAdmins.findSellerByEmail(email);
    }

    /**
     * Find a seller by unique ID.
     *
     * @param id the seller ID (string representation)
     * @return an optional containing the seller profile if found
     */
    @Override
    public Optional<SellerProfile> findSellerById(String id) {
        return sellerServiceForAdmins.findSellerById(id);
    }

    /**
     * Find a seller by phone number.
     *
     * @param phoneNumber the seller phone number
     * @return an optional containing the seller profile if found
     */
    @Override
    public Optional<SellerProfile> findBySellerPhoneNumber(String phoneNumber) {
        return sellerServiceForAdmins.findBySellerPhoneNumber(phoneNumber);
    }

    /**
     * Retrieve all sellers with dynamic sorting and pagination.
     *
     * @param page      page number (zero-based)
     * @param size      number of records per page
     * @param sortBy    field to sort by
     * @param direction sort direction ("asc" or "desc")
     * @return a page of sorted seller profiles
     */
    @Override
    public Page<SellerProfile> findAllSellersSorted(int page, int size, String sortBy, String direction) {
        return sellerServiceForAdmins.findAllSellersSorted(page, size, sortBy, direction);
    }

    /**
     * Verify a seller's email status.
     *
     * @param id            seller ID
     * @param emailVerified true if email should be marked verified, false otherwise
     * @return true if verification was successful, false otherwise
     */
    @Override
    @Transactional
    public Boolean verifySeller(Long id, Boolean emailVerified) {
        return sellerServiceForAdmins.verifySeller(id, emailVerified);
    }

    /**
     * Delete a seller by ID.
     *
     * @param id the seller ID
     */
    @Override
    @Transactional
    public void deleteSellerById(Long id) {
        sellerServiceForAdmins.deleteSellerById(id);
    }

    /**
     * Update the account status of a seller.
     *
     * @param id     seller ID
     * @param status new account status (e.g., ACTIVE, SUSPENDED)
     * @return an optional containing the updated seller entity
     */
    @Override
    @Transactional
    public Optional<Seller> updateSellerAccountStatus(Long id, AccountStatus status) {
        return sellerServiceForAdmins.updateSellerAccountStatus(id, status);
    }
}
