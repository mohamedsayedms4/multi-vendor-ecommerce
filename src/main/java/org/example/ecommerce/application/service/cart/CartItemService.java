package org.example.ecommerce.application.service.cart;

import org.example.ecommerce.domain.model.cart.CartItem;

public interface CartItemService {


    CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws Exception;
    void deleteCartItem(Long userId, Long cartItemId) throws Exception;

    CartItem getCartItem(Long cartItemId) throws Exception;
}
