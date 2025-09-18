package org.example.ecommerce.application.service.product;

import org.example.ecommerce.domain.model.product.Product;
import org.example.ecommerce.domain.model.seller.Seller;
import org.example.ecommerce.infrastructure.dto.product.CreateProductDto;
import org.example.ecommerce.infrastructure.dto.product.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Optional<CreateProductDto> insert(Seller seller,
                                      CreateProductDto  createProductDto ,
                                      List<MultipartFile> images);

    Optional<Product> findById(Long id);


    Page<Product> getProducts(Pageable pageable);


    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findBySellerId(Long sellerId, Pageable pageable);

    Page<Product> findByViewsCounter(Pageable pageable);

    Page<Product> findBySearchCounter(Pageable pageable);

    Page<Product> findByIsVerified(Boolean isVerified, Pageable pageable);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    boolean updateProductStatus(Long id , boolean status);


    Optional<CreateProductDto> updateProduct(Long id, CreateProductDto createProductDto);

    void deleteProduct(Long id);

}
