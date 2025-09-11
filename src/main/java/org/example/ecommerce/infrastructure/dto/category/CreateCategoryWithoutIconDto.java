package org.example.ecommerce.infrastructure.dto.category;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateCategoryWithoutIconDto(
        @NotBlank(message = "Category name is required")
        String nameEn,
        @NotBlank(message = "Category name is required")
        String nameAr,
        @NotBlank(message = "Category ID is required")
        @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Category ID must be alphanumeric with - or _")
        String categoryId,

        Long parentCategoryId,
        Integer level
) {
}
