package org.example.ecommerce.domain.model.cart.repository;

import org.example.ecommerce.domain.model.cart.Cart;
import org.example.ecommerce.domain.model.user.User;

import java.util.Optional;

public interface CartRepository {

    Optional<Cart> findById(Long id);
    Optional<Cart> findByUserId(Long userId);
    Optional<Cart> findByUser(User user);
    Cart save(Cart cart);
    Optional<Cart> findByTempId(String tempId);
    void delete(Cart cart);
}
