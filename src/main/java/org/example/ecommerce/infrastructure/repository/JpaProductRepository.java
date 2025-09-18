package org.example.ecommerce.infrastructure.repository;

import org.example.ecommerce.domain.model.category.Category;
import org.example.ecommerce.domain.model.product.Product;
import org.example.ecommerce.domain.model.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProductRepository extends JpaRepository<Product, Long> ,
        ProductRepository {

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findBySellerId(Long sellerId, Pageable pageable);

    Page<Product> findAllByOrderByViewsCounterDesc(Pageable pageable);
    Page<Product> findAllByOrderBySearchCounterDesc(Pageable pageable);

    Page<Product> findByIsVerified(Boolean isVerified, Pageable pageable);
}

