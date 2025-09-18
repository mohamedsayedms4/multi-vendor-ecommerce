package org.example.ecommerce.domain.model.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.ecommerce.domain.common.BaseEntity;
import org.example.ecommerce.domain.model.product.Product;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)

public class OrderItem extends BaseEntity {

    /**
     * Reference to the parent order this item belongs to.
     * Ignored during JSON serialization to prevent circular references.
     */
    @JsonIgnore
    @ManyToOne
    private Order order;

    /**
     * The product associated with this order item.
     */
    @ManyToOne
    private Product product;

    /**
     * Size of the product ordered (e.g., S, M, L).
     */
    private String size;

    /**
     * Quantity of the product ordered.
     */
    private Integer quantity;

    /**
     * Original price (MRP) of the product.
     */
    private Double mrpPrice;

    /**
     * Final selling price of the product after any discounts.
     */
    private Double sellingPrice;

    /**
     * ID of the user who placed the order (duplicated for reference).
     */
    private Long userId;
}
