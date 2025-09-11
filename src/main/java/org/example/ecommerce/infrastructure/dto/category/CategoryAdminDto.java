package org.example.ecommerce.infrastructure.dto.category;


import java.time.LocalDateTime;

public record CategoryAdminDto(
        Long id,
        String nameEn,
        String nameAr,
        String categoryId,
        Long parentCategoryId,
        Integer level,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
