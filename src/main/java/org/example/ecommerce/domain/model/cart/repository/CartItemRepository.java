package org.example.ecommerce.domain.model.cart.repository;

import org.example.ecommerce.domain.model.cart.Cart;
import org.example.ecommerce.domain.model.cart.CartItem;
import org.example.ecommerce.domain.model.product.Product;

import java.util.Optional;

public interface CartItemRepository {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    Optional<CartItem> findCartItemById(Long id);
    void deleteCartItemById(Long id);
    CartItem save(CartItem cartItem);

}
