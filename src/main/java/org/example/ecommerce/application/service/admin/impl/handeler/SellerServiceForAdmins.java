package org.example.ecommerce.application.service.admin.impl.handeler;

import org.example.ecommerce.domain.model.seller.AccountStatus;
import org.example.ecommerce.domain.model.seller.Seller;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Service interface for admin-level operations on sellers.
 * All methods require the ADMIN role to be accessed.
 */
@PreAuthorize("hasRole('ROLE_ADMIN')")
public interface SellerServiceForAdmins {

    /**
     * Retrieves a paginated and sorted list of all sellers.
     *
     * @param page      the page number to retrieve
     * @param size      the number of records per page
     * @param sortBy    the field to sort by
     * @param direction the sort direction ("asc" or "desc")
     * @return a paginated list of seller profiles
     */
    Page<SellerProfile> findAllSellersSorted(int page, int size, String sortBy, String direction);

    /**
     * Finds a seller by their email address.
     *
     * @param email the seller's email address
     * @return an Optional containing the seller profile if found, otherwise empty
     */
    Optional<SellerProfile> findSellerByEmail(String email);

    /**
     * Finds a seller by their unique ID.
     *
     * @param id the seller's unique identifier (as String)
     * @return an Optional containing the seller profile if found, otherwise empty
     */
    Optional<SellerProfile> findSellerById(String id);

    /**
     * Finds a seller by their phone number.
     *
     * @param phoneNumber the seller's phone number
     * @return an Optional containing the seller profile if found, otherwise empty
     */
    Optional<SellerProfile> findBySellerPhoneNumber(String phoneNumber);

    /**
     * Updates the verification status of a seller.
     *
     * @param id            the seller's unique identifier
     * @param emailVerified the new email verification status (true/false)
     * @return true if verification status was updated successfully, false otherwise
     */
    Boolean verifySeller(Long id, Boolean emailVerified);

    /**
     * Deletes a seller by their unique ID.
     *
     * @param id the seller's unique identifier
     */
    void deleteSellerById(Long id);

    /**
     * Updates the account status of a seller (e.g., ACTIVE, SUSPENDED, BANNED).
     *
     * @param id     the seller's unique identifier
     * @param status the new account status to be applied
     * @return an Optional containing the updated Seller entity if successful, otherwise empty
     */
    Optional<Seller> updateSellerAccountStatus(Long id, AccountStatus status);
}
