package org.example.ecommerce.infrastructure.dto.seller;

public record UpdateSellerWithoutLogoAndBanerDto(

        String businessName,

        String businessEmail,

        String businessMobile,

        String businessAddress
) {
}
