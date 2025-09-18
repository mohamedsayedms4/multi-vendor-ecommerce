package org.example.ecommerce.domain.model.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.ecommerce.domain.common.BaseEntity;
import org.example.ecommerce.domain.model.product.Product;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@AllArgsConstructor
@NoArgsConstructor

public class CartItem extends BaseEntity {

    @ManyToOne
    @JsonIgnore
    private Cart cart;

    private String size;

    private Integer quantity = 0;
    private Long userId;

    private Long maximumRetailPrice = 0l;

    private Long sellingPrice =0l;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
