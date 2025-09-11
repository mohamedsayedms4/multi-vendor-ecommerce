package org.example.ecommerce.infrastructure.dto.category;


public record CategoryUserDtoAr(
        Long id,
        String nameAr,
        String categoryId,
        Long parentCategoryId,
        Integer level
) {
}
