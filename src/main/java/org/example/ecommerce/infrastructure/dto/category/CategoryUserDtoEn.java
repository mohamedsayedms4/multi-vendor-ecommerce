package org.example.ecommerce.infrastructure.dto.category;


import java.time.LocalDateTime;

public record CategoryUserDtoEn(
        Long id,
        String nameEn,
        String categoryId,
        Long parentCategoryId,
        Integer level
) {
}
