package org.example.ecommerce.infrastructure.dto.address;


public record AddressDTO(
        String name,
        String locality,
        String state,
        String city,
        String address
) {}
