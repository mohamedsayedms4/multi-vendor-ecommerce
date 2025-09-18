package org.example.ecommerce.infrastructure.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.product.ProductService;
import org.example.ecommerce.application.service.seller.SellerService;
import org.example.ecommerce.domain.common.exception.INTERNAL_SERVER_ERROR;
import org.example.ecommerce.domain.model.product.Product;
import org.example.ecommerce.domain.model.product.exception.ProductNotFoundException;
import org.example.ecommerce.domain.model.seller.Seller;
import org.example.ecommerce.infrastructure.dto.product.CreateProductDto;
import org.example.ecommerce.infrastructure.dto.product.ProductDTO;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;
import org.example.ecommerce.infrastructure.mapper.ProductMapper;
import org.example.ecommerce.infrastructure.mapper.SellerMapper;
import org.example.ecommerce.infrastructure.utils.JwtUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/products")
@Slf4j
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final SellerService sellerService;
    private final SellerMapper sellerMapper;
    private final ProductMapper productMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SELLER')")
    public ResponseEntity<?> insertProduct(
            @RequestHeader(value = "Authorization", required = false) String token ,
            @Valid @RequestPart("product") CreateProductDto dto,
            @RequestPart(value = "images", required = true) List<MultipartFile> images
    ) {
        log.info("----------JWT IS______________ : {}", token);

        // تحقق من وجود التوكن
        if (token == null || token.trim().isEmpty()) {
            log.error("JWT is null or empty");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authorization header is missing"));
        }

        log.info("Attempting to fetch seller profile using token...");
        Optional<Seller> sellerProfileOpt = sellerService.getSellerProfile(token)
                .map(sellerMapper::toSeller);


        if (sellerProfileOpt.isPresent()) {
            log.info("Seller profile found: {}", sellerProfileOpt.get().getUser());

            log.info("Attempting to insert product: {}", dto.title());
            Optional<CreateProductDto> productDto = productService.insert(sellerProfileOpt.get(), dto, images);

            if (productDto.isPresent()) {
                log.info("Product created successfully: {}", productDto.get().title());
                return ResponseEntity.status(HttpStatus.CREATED).body(productDto);
            } else {
                log.warn("Product insertion returned empty Optional");
            }
        } else {
            log.warn("Seller profile not found for the provided token");
        }

        log.error("Product not created for product: {}", dto.title());
        throw new INTERNAL_SERVER_ERROR("Product not created");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        log.info("Get product by id: {}", id);
        Optional<ProductDTO> productOpt = productService.findById(id)
                .map(productMapper::toDTO);

        // لو موجود
        if (productOpt.isPresent()) {
            return ResponseEntity.ok(productOpt.get());
        } else {
            // لو مش موجود
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }
    }

    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Product> products = productService.getProducts(PageRequest.of(page, size));
        return ResponseEntity.ok(products);
    }

    // =================== Get products by category ===================
    @GetMapping("/category/{categoryId}")
    public Page<Product> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.findByCategoryId(categoryId, PageRequest.of(page, size));
    }

    // =================== Get products by seller ===================
    @GetMapping("/seller/{sellerId}")
    public Page<Product> getProductsBySeller(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.findBySellerId(sellerId, PageRequest.of(page, size));
    }

    // =================== Get products by viewsCounter ===================
    @GetMapping("/views")
    public Page<ProductDTO> getProductsByViewsCounter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.findByViewsCounter(PageRequest.of(page,size))
                .map(productMapper::toDTO);
    }

    // =================== Get products by searchCounter ===================
    @GetMapping("/search")
    public Page<ProductDTO> getProductsBySearchCounter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
//        Page<ProductDTO> productDTOS = productService.
        return productService.findBySearchCounter(PageRequest.of(page, size))
                .map(productMapper::toDTO);
    }

    // =================== Get products by isVerified ===================
    @GetMapping("/verified")
    public Page<Product> getProductsByIsVerified(
            @RequestParam Boolean isVerified,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.findByIsVerified(isVerified, PageRequest.of(page, size));
    }

    @PutMapping("/status")
    public ResponseEntity<Boolean> updateProductStatus(
            @RequestParam("id") Long id,
            @RequestParam("status") boolean status
    ) {
        Boolean updated = productService.updateProductStatus(id, status);
        return ResponseEntity.ok(updated); // نرجع الـ Boolean داخل ResponseEntity
    }

    @PatchMapping()
    public ResponseEntity<CreateProductDto> updateProduct(
            @Valid @RequestBody CreateProductDto createProductDto,
            @RequestParam("id") Long id
    ){
        CreateProductDto dt = productService.updateProduct(id,createProductDto)
                .orElseThrow(() -> new IllegalArgumentException("Has error"));
        return ResponseEntity.ok(dt);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
