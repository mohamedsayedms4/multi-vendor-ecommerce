package org.example.ecommerce.infrastructure.dto.user;

import org.example.ecommerce.infrastructure.dto.address.AddressDTO;
import org.example.ecommerce.infrastructure.dto.authority.AuthorityDTO;

import java.util.Set;

public record UserProfile (
        Long id,
        String email,
        String fullName,
        String phoneNumber,
        Set<AuthorityDTO> authorities,
        Set<AddressDTO> pickupAddress,
        String imageUrl
){
}
