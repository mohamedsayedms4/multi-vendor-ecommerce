package org.example.ecommerce.infrastructure.dto.seller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record BecomeASellerDto(

        @NotBlank(message = "Business name is required")
        String businessName,

        @Email(message = "Invalid business email")
        String businessEmail,

        @NotBlank(message = "Business mobile is required")
        String businessMobile,

        @NotBlank(message = "Business address is required")
        String businessAddress ,

        String logo ,
        String banner
) {
}
