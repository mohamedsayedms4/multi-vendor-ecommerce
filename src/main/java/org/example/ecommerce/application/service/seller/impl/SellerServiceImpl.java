package org.example.ecommerce.application.service.seller.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.seller.SellerService;
import org.example.ecommerce.application.service.seller.impl.handeler.BecomeASeller;
import org.example.ecommerce.application.service.seller.impl.handeler.UpdateSellerProfile;
import org.example.ecommerce.application.service.user.impl.GetUserByType;
import org.example.ecommerce.domain.model.seller.exception.SellerException;
import org.example.ecommerce.infrastructure.dto.seller.BecomeASellerDto;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;
import org.example.ecommerce.infrastructure.dto.seller.UpdateSellerDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the SellerService interface.
 * Handles seller-related operations such as creating a seller,
 * updating seller profile, and fetching seller profile.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    /** Handler for updating seller profile details */
    private final UpdateSellerProfile updateSellerProfile;

    /** Context for fetching seller information */
    private final GetSellerContext getSellerContext;

    /** Handler for becoming a seller */
    private final BecomeASeller becomeASellerClass;

    /** JPA EntityManager for persistence operations */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Converts a user into a seller or updates existing seller information.
     *
     * @param userId the ID of the user to become a seller
     * @param dto the DTO containing seller information
     * @return Optional of SellerProfile after conversion
     */
    @Transactional
    @Override
    public Optional<SellerProfile> becomeASeller(Long userId, BecomeASellerDto dto) {
        return becomeASellerClass.becomeASeller(userId, dto);
    }

    /**
     * Retrieves the seller profile based on a JWT token.
     *
     * @param jwt the JWT token of the user
     * @return Optional of SellerProfile
     * @throws SellerException if seller cannot be found or JWT is invalid
     */
    @Override
    public Optional<SellerProfile> getSellerProfile(String jwt) throws SellerException {
        return getSellerContext.getSeller(jwt, GetUserByType.JWT);
    }

    /**
     * Updates an existing seller's profile.
     *
     * @param id the ID of the seller to update
     * @param updateSellerDto DTO containing updated seller information
     * @return true if update was successful
     */
    @Override
    @Transactional
    public Boolean updateSellerProfile(Long id, UpdateSellerDto updateSellerDto) {
        return updateSellerProfile.updateSellerProfile(id, updateSellerDto);
    }
}
