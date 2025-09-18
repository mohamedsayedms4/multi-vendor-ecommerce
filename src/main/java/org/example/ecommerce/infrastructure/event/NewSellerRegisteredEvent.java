package org.example.ecommerce.infrastructure.event;

import org.example.ecommerce.domain.model.seller.Seller;
import org.springframework.context.ApplicationEvent;

public class NewSellerRegisteredEvent extends ApplicationEvent {
    private final Seller seller;

    public NewSellerRegisteredEvent(Object source, Seller seller) {
        super(source);
        this.seller = seller;
    }

    public Seller getSeller() {
        return seller;
    }
}
