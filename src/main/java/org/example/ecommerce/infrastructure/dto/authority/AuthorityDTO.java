package org.example.ecommerce.infrastructure.dto.authority;


import org.example.ecommerce.domain.model.user.UserRole;

public record AuthorityDTO(
        UserRole role
) {}
