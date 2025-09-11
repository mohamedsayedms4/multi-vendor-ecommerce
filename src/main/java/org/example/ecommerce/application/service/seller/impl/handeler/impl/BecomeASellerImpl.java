package org.example.ecommerce.application.service.seller.impl.handeler.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.seller.impl.handeler.BecomeASeller;
import org.example.ecommerce.domain.model.seller.AccountStatus;
import org.example.ecommerce.domain.model.seller.BusinessDetails;
import org.example.ecommerce.domain.model.seller.Seller;
import org.example.ecommerce.domain.model.seller.exception.SellerException;
import org.example.ecommerce.domain.model.seller.repository.SellerRepository;
import org.example.ecommerce.domain.model.user.Authority;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.model.user.UserRole;
import org.example.ecommerce.domain.model.user.repository.UserRepository;
import org.example.ecommerce.infrastructure.dto.seller.BecomeASellerDto;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;
import org.example.ecommerce.infrastructure.mapper.SellerMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of BecomeASeller handler.
 * Responsible for converting a user to a seller or updating existing seller information.
 */
@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BecomeASellerImpl implements BecomeASeller {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final SellerMapper sellerMapper;

    /**
     * Converts a user to a seller or updates existing seller information.
     *
     * @param userId the ID of the user
     * @param dto the DTO containing seller information
     * @return Optional of SellerProfile
     */
    @Override
    public Optional<SellerProfile> becomeASeller(Long userId, BecomeASellerDto dto) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new SellerException("User not found"));

            Authority authority = new Authority();
            authority.setRole(UserRole.ROLE_SELLER);
            authority.setCustomer(user);
            user.getAuthorities().add(authority);

            Seller seller = user.getSeller();

            if (seller != null) {
                log.info("User {} is already a seller, updating business details", userId);
                updateBusinessDetails(seller, dto);
                seller.setAccountStatus(AccountStatus.PENDING_VERIFICATION);
            } else {
                log.info("Creating new seller for user {}", userId);
                seller = new Seller();
                seller.setUser(user);

                // Create BusinessDetails directly from DTO
                BusinessDetails bd = new BusinessDetails();
                bd.setBusinessName(dto.businessName());
                bd.setBusinessEmail(dto.businessEmail());
                bd.setBusinessMobile(dto.businessMobile());
                bd.setBusinessAddress(dto.businessAddress());
                bd.setLogo(dto.logo());
                bd.setBanner(dto.banner());
                seller.setBusinessDetails(bd);

                seller.setAccountStatus(AccountStatus.PENDING_VERIFICATION);
                seller.setIsEmailVerified(false);
                user.setSeller(seller);
            }

            Seller saved = sellerRepository.save(seller);

            return Optional.of(sellerMapper.toSellerProfile(saved));

        } catch (Exception e) {
            log.error("Error in becomeASeller for user {}: {}", userId, e.getMessage(), e);
            throw new SellerException("Failed to convert user to seller: " + e.getMessage());
        }
    }

    /**
     * Updates the business details of an existing seller.
     *
     * @param seller the seller to update
     * @param dto the DTO containing updated business details
     */
    private void updateBusinessDetails(Seller seller, BecomeASellerDto dto) {
        if (seller.getBusinessDetails() == null) {
            seller.setBusinessDetails(new BusinessDetails());
        }

        BusinessDetails bd = seller.getBusinessDetails();
        bd.setBusinessName(dto.businessName());
        bd.setBusinessEmail(dto.businessEmail());
        bd.setBusinessMobile(dto.businessMobile());
        bd.setBusinessAddress(dto.businessAddress());
    }
}
