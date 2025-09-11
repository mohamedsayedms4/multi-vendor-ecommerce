package org.example.ecommerce.infrastructure.dto.seller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateSellerDto(

        String businessName,

        String businessEmail,

        String businessMobile,

        String businessAddress ,

        String logo ,
        String banner
) {
}
