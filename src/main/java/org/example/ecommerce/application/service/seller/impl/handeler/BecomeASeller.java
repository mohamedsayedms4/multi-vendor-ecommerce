package org.example.ecommerce.application.service.seller.impl.handeler;

import org.example.ecommerce.infrastructure.dto.seller.BecomeASellerDto;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;

import java.util.Optional;

/**
 * Handler interface for converting a user into a seller.
 */
public interface BecomeASeller {

    /**
     * Converts a user to a seller and returns the seller profile.
     *
     * @param userId the ID of the user
     * @param dto the DTO containing seller details
     * @return Optional of SellerProfile
     */
    Optional<SellerProfile> becomeASeller(Long userId, BecomeASellerDto dto);
}
