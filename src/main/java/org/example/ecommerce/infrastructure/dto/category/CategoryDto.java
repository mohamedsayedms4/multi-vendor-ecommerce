package org.example.ecommerce.infrastructure.dto.category;

import lombok.Builder;
import java.util.List;

/**
 * Data Transfer Object representing a Category.
 * Used for returning category information in API responses.
 */
@Builder
public record CategoryDto(
        /**
         * Unique identifier of the category (primary key).
         */
        Long id,

        /**
         * English name of the category.
         */
        String nameEn,

        /**
         * Arabic name of the category.
         */
        String nameAr,

        /**
         * Custom string identifier for the category.
         */
        String categoryId,

        /**
         * Level of the category in the hierarchy.
         */
        Integer level,

        /**
         * URL of the category's image/icon.
         */
        String imageUrl,

        /**
         * Parent category information if this category has a parent.
         */
        ParentCategory parentCategory,

        /**
         * List of child categories, represented as CategoryDto.
         */
        List<CategoryDto> children
) {

    /**
     * Data Transfer Object representing the parent category information.
     */
    public record ParentCategory(
            /**
             * Unique identifier of the parent category.
             */
            Long id,

            /**
             * English name of the parent category.
             */
            String nameEn,

            /**
             * Arabic name of the parent category.
             */
            String nameAr,

            /**
             * Custom string identifier of the parent category.
             */
            String categoryId
    ) {}
}
