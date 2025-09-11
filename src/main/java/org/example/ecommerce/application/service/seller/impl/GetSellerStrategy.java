package org.example.ecommerce.application.service.seller.impl;

import org.example.ecommerce.domain.model.seller.exception.SellerException;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;

import java.util.Optional;

/**
 * Strategy interface for retrieving seller profiles.
 * Different implementations can fetch seller by ID, email, phone, or JWT.
 */
public interface GetSellerStrategy {

    /**
     * Fetches a seller profile based on a specific input.
     *
     * @param input the identifier (could be ID, email, phone, or JWT)
     * @return Optional containing SellerProfile if found
     */
    Optional<SellerProfile> getSeller(String input);
}
