package org.example.ecommerce.infrastructure.repository;

import org.example.ecommerce.domain.model.cart.Cart;
import org.example.ecommerce.domain.model.cart.CartItem;
import org.example.ecommerce.domain.model.cart.repository.CartItemRepository;
import org.example.ecommerce.domain.model.product.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCartItemRepository extends CrudRepository<CartItem, Long> , CartItemRepository {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    Optional<CartItem> findCartItemById(Long id);
    void deleteCartItemById(Long id);
}
