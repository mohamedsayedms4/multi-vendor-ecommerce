package org.example.ecommerce.domain.model.product.repository;

import org.example.ecommerce.domain.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    Page<Product> findAll(Pageable pageable);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findBySellerId(Long sellerId, Pageable pageable);

    Page<Product> findAllByOrderByViewsCounterDesc(Pageable pageable);
    Page<Product> findAllByOrderBySearchCounterDesc(Pageable pageable);
    void deleteById(Long id);

    Page<Product> findByIsVerified(Boolean isVerified, Pageable pageable);
}
