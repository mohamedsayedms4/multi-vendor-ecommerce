package org.example.ecommerce.infrastructure.repository;

import org.example.ecommerce.domain.model.seller.Seller;
import org.example.ecommerce.domain.model.seller.repository.SellerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaSellerRepository extends JpaRepository<Seller,Long> , SellerRepository {
    Optional<Seller> findByUserId(Long id);
}
