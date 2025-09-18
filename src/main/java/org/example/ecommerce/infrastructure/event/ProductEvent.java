package org.example.ecommerce.infrastructure.event;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.domain.model.product.Product;
import org.springframework.stereotype.Service;

@Getter
@RequiredArgsConstructor
public class ProductEvent {

    private final Product product;
    private final Type eventType;
    private final String performedBy;
    private final Long performedById;


}
