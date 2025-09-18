package org.example.ecommerce.infrastructure.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.domain.model.cart.Cart;
import org.example.ecommerce.domain.model.order.Order;
import org.example.ecommerce.domain.model.user.User;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class OrderEvent {

    private final Set<Order> orders;  // بدل Order واحد
    private final User customer;
    private final Type type;
    private final Cart cart;
}
