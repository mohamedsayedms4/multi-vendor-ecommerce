package org.example.ecommerce.infrastructure.dto.product;


import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import org.example.ecommerce.domain.model.product.Size;

public record CreateProductDto(
        @NotBlank(message = "{product.title.notBlank}")
        String title,

        @NotBlank(message = "{product.description.notBlank}")
        String description,

        @DecimalMin(value = "0.0", inclusive = true, message = "{product.priceBeforeDiscount.min}")
        Long priceBeforeDiscount,

        @DecimalMin(value = "0.0", inclusive = true, message = "{product.priceAfterDiscount.min}")
        Long priceAfterDiscount,
        @Min(value = 0, message = "{product.quantity.min}")
        Integer quantity,

        String color,

        @NotNull(message = "{product.category.notNull}")
        Long categoryId,


        Size size
) {}
