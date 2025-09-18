package org.example.ecommerce.infrastructure.repository;

import org.example.ecommerce.domain.model.cart.Cart;
import org.example.ecommerce.domain.model.cart.repository.CartRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCartRepository extends JpaRepository<Cart, Long> , CartRepository {
}
