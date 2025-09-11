package org.example.ecommerce.application.service.seller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.user.impl.GetUserByType;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;

import java.util.Optional;

/**
 * Context class for fetching seller profiles using different strategies.
 * Uses the Strategy design pattern to select the appropriate retrieval method
 * based on the input type (ID, EMAIL, PHONE, JWT).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetSellerContext {

    /** Spring ApplicationContext used to retrieve beans dynamically */
    private final ApplicationContext applicationContext;

    /**
     * Fetches a seller profile using the given input and retrieval type.
     *
     * @param input the identifier (ID, email, phone, or JWT)
     * @param type the type of input used to retrieve the seller
     * @return Optional containing SellerProfile if found
     */
    public Optional<SellerProfile> getSeller(String input, GetUserByType type) {

        // Convert enum type to the corresponding registered bean name
        String beanName = switch (type) {
            case ID -> GetSellerByType.ID_VALUE;
            case EMAIL -> GetSellerByType.EMAIL_VALUE;
            case PHONE -> GetSellerByType.PHONE_VALUE;
            case JWT -> GetSellerByType.JWT_VALUE;
        };

        // Retrieve the appropriate strategy bean
        GetSellerStrategy strategy = applicationContext.getBean(beanName, GetSellerStrategy.class);

        // Delegate the call to the selected strategy
        return strategy.getSeller(input);
    }
}
