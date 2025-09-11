package org.example.ecommerce.application.service.seller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.domain.model.seller.repository.SellerRepository;
import org.example.ecommerce.domain.model.user.exception.UserNotFoundException;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;
import org.example.ecommerce.infrastructure.mapper.SellerMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Strategy implementation for retrieving a seller using a JWT token.
 */
@Component(GetSellerByType.ID_VALUE)
@RequiredArgsConstructor
@Slf4j
public class FindUserByIdStrategy implements GetSellerStrategy {
    private final SellerRepository sellerRepository;
    private final SellerMapper sellerMapper;
    private final MessageSource messageSource;

    @Override
    public Optional<SellerProfile> getSeller(String input) {
        Long id = Long.valueOf(input);
        log.info("Searching for seller with id: {}", id);
        Optional<SellerProfile> seller = sellerRepository.findById(id).map(sellerMapper::toSellerProfile);

        if (seller.isEmpty()) {
            log.error("seller not found for id: {}", id);
            String errorMessage = messageSource.getMessage(
                    "error.user.notfound.id",
                    new Object[]{id},
                    LocaleContextHolder.getLocale()
            );
            throw new UserNotFoundException(errorMessage);
        }
        log.debug("seller found: {}", seller);


        return seller;
    }
}
