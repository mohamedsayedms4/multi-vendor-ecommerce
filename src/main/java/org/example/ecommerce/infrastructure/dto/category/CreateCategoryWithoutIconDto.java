package org.example.ecommerce.infrastructure.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object for creating a Category without providing an icon.
 * Used in API requests for category creation where the icon may be uploaded separately.
 */
public record CreateCategoryWithoutIconDto(

        /**
         * English name of the category.
         * Must not be blank.
         */
        @NotBlank(message = "Category name is required")
        String nameEn,

        /**
         * Arabic name of the category.
         * Must not be blank.
         */
        @NotBlank(message = "Category name is required")
        String nameAr,

        /**
         * Custom string identifier for the category.
         * Must not be blank and only contain alphanumeric characters, hyphen, or underscore.
         */
        @NotBlank(message = "Category ID is required")
        @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Category ID must be alphanumeric with - or _")
        String categoryId,

        /**
         * ID of the parent category, if any.
         */
        Long parentCategoryId,

        /**
         * Level of the category in the hierarchy.
         */
        Integer level
) {}
