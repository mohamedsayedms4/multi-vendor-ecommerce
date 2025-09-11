package org.example.ecommerce.infrastructure.dto.seller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.example.ecommerce.domain.model.seller.AccountStatus;
import org.example.ecommerce.domain.model.seller.BusinessDetails;
import org.example.ecommerce.infrastructure.dto.address.AddressDTO;
import org.example.ecommerce.infrastructure.dto.authority.AuthorityDTO;

import java.util.Set;

public record SellerProfile(
        Long id ,
        String email,
        String fullName,
        String phoneNumber,
        Set<AuthorityDTO> authorities,
        AddressDTO pickupAddress,
        String imageUrl,
        BusinessDetails businessDetails,
        Boolean isEmailVerified,
        AccountStatus accountStatus
) {
}
