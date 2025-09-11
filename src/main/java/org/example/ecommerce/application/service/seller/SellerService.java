package org.example.ecommerce.application.service.seller;

import org.example.ecommerce.domain.model.seller.exception.SellerException;
import org.example.ecommerce.infrastructure.dto.seller.BecomeASellerDto;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;
import org.example.ecommerce.infrastructure.dto.seller.UpdateSellerDto;

import java.util.Optional;

/**
 * Service interface for seller operations.
 * Includes methods to become a seller, get seller profile,
 * and update seller profile information.
 */
public interface SellerService {

    /**
     * Converts a user into a seller.
     *
     * @param id the user ID
     * @param becomeASellerDto DTO containing seller information
     * @return Optional of SellerProfile
     * @throws SellerException if the operation fails
     */
    Optional<SellerProfile> becomeASeller(Long id, BecomeASellerDto becomeASellerDto) throws SellerException;

    /**
     * Fetches the seller profile based on a JWT token.
     *
     * @param jwt JWT token
     * @return Optional of SellerProfile
     * @throws SellerException if seller not found
     */
    Optional<SellerProfile> getSellerProfile(String jwt) throws SellerException;

    /**
     * Updates seller profile information.
     *
     * @param id seller ID
     * @param updateSellerDto DTO with updated seller info
     * @return true if update was successful
     * @throws SellerException if update fails
     */
    Boolean updateSellerProfile(Long id, UpdateSellerDto updateSellerDto) throws SellerException;
}
