package org.example.ecommerce.infrastructure.dto.product;

import org.example.ecommerce.domain.model.product.Size;

import java.math.BigDecimal;
import java.util.List;

public record ProductDTO(
        Long id,
        String title,
        String description,
        Long priceBeforeDiscount,
        Long priceAfterDiscount,
        Integer discountPercentage,
        Integer quantity,
        String color,
        Size size,
        List<String> images,
        Long viewsCounter,
        Long searchCounter,
        SellerDTO seller,
        CategoryDTO category
) {
    public record SellerDTO(Long id, String name) {}
    public record CategoryDTO(Long id, String title) {}
}
