package org.example.ecommerce.application.service.seller.impl;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.application.service.user.impl.GetUserByType;
import org.example.ecommerce.domain.model.seller.repository.SellerRepository;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;
import org.example.ecommerce.infrastructure.mapper.SellerMapper;
import org.example.ecommerce.infrastructure.utils.JwtUtil;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Strategy implementation for retrieving a seller using a JWT token.
 */
@Component(GetSellerByType.JWT_VALUE)
@RequiredArgsConstructor
public class FindUserByJwtTokenStrategy implements GetSellerStrategy {

    /** Utility class for parsing and validating JWT tokens */
    private final JwtUtil jwtUtil;

    /** Mapper to convert Seller entity to DTO */
    private final SellerMapper sellerMapper;

    /** Repository to fetch seller data from database */
    private final SellerRepository sellerRepository;

    /**
     * Retrieves a SellerProfile by extracting the user ID from the JWT.
     *
     * @param input the JWT token
     * @return Optional containing SellerProfile if found
     */
    @Override
    public Optional<SellerProfile> getSeller(String input) {
        Long id = jwtUtil.extractUserIdFromJwt(input);
        return sellerRepository.findByUserId(id)
                .map(sellerMapper::toSellerProfile);
    }
}
