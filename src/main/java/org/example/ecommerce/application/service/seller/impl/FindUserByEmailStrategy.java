package org.example.ecommerce.application.service.seller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.domain.model.seller.repository.SellerRepository;
import org.example.ecommerce.domain.model.user.exception.UserNotFoundException;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;
import org.example.ecommerce.infrastructure.mapper.SellerMapper;
import org.example.ecommerce.infrastructure.utils.JwtUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Strategy implementation for retrieving a seller using a JWT token.
 */
@Component(GetSellerByType.EMAIL_VALUE)
@RequiredArgsConstructor
@Slf4j
public class FindUserByEmailStrategy implements GetSellerStrategy {
    private final SellerRepository sellerRepository;
    private final SellerMapper sellerMapper;
    private final MessageSource messageSource;


    @Override
    public Optional<SellerProfile> getSeller(String input) {
        log.info("Searching for seller with user Email: {}", input);
        Optional<SellerProfile> seller = sellerRepository.
                findByBusinessDetails_BusinessEmail(input)
                .map(sellerMapper::toSellerProfile);
        if (seller.isEmpty()) {
            log.error("seller not found with user Email: {}", input);
            String errorMessage = messageSource.getMessage("user.notfound.email",
                    new Object[]{input},
                    LocaleContextHolder.getLocale());
            throw new UserNotFoundException(errorMessage);
        }
        log.debug("User found: {}", seller.toString());
        return seller;    }
}
