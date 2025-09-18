package org.example.ecommerce.application.service.product.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.category.CategoryService;
import org.example.ecommerce.application.service.product.ProductService;
import org.example.ecommerce.domain.common.exception.ImageIsRequired;
import org.example.ecommerce.domain.model.category.Category;
import org.example.ecommerce.domain.model.category.exception.CategoryNotFoundException;
import org.example.ecommerce.domain.model.product.Product;
import org.example.ecommerce.domain.model.product.exception.ProductNotFoundException;
import org.example.ecommerce.domain.model.product.repository.ProductRepository;
import org.example.ecommerce.domain.model.seller.Seller;
import org.example.ecommerce.domain.model.user.repository.UserRepository;
import org.example.ecommerce.infrastructure.dto.product.CreateProductDto;
import org.example.ecommerce.infrastructure.event.ProductEvent;
import org.example.ecommerce.infrastructure.event.Type;
import org.example.ecommerce.infrastructure.utils.ImageUploadUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ImageUploadUtil  imageUploadUtil;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public Optional<CreateProductDto> insert(Seller seller,
                                             CreateProductDto dto, List<MultipartFile> images) {
        log.info("Received request to insert product : {} --> images : {}", dto, images);

        Product savedProduct = new Product();
        savedProduct.setTitle(dto.title());
        savedProduct.setDescription(dto.description());
        savedProduct.setMaximumRetailPrice(dto.priceBeforeDiscount());
        savedProduct.setSellingPrice(dto.priceAfterDiscount());
        savedProduct.setQuantity(dto.quantity());
        savedProduct.setColor(dto.color());
        log.info("Mapped DTO to Product entity: {}", savedProduct);

        Optional<Category> category = categoryService.getCategory(dto.categoryId());
        if (category.isEmpty()) {
            throw new CategoryNotFoundException("Category not found");
        }
        savedProduct.setCategory(category.get());
        savedProduct.setSeller(seller);

        if (images != null && !images.isEmpty()) {
            List<String> imagesString = new ArrayList<>();

            log.info("Uploading {} images for product", images.size());
            List<String> uploadedImages = imageUploadUtil.saveImages(images.toArray(new MultipartFile[0]));
            imagesString.addAll(uploadedImages);
            savedProduct.setImages(imagesString);
            log.info("Uploaded images URLs: {}", uploadedImages);
        } else {
            throw new ImageIsRequired("Image is required");
        }

        Product productSavedInDb = productRepository.save(savedProduct);
//        String email;
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (principal instanceof UserDetails userDetails) {
//            email = userDetails.getUsername();
//        } else {
//            email = principal.toString();
//        }


// نشر الحدث
        eventPublisher.publishEvent(new ProductEvent(productSavedInDb, Type.CREATED, seller.getBusinessDetails().getBusinessEmail(),seller.getId()));
        log.info("Product saved in database: {}", productSavedInDb);

        // تحويل الـProduct للـDTO للـResponse
        CreateProductDto responseDto = new CreateProductDto(
                productSavedInDb.getTitle(),
                productSavedInDb.getDescription(),
                productSavedInDb.getMaximumRetailPrice(),
                productSavedInDb.getSellingPrice(),
                productSavedInDb.getQuantity(),
                productSavedInDb.getColor(),
                productSavedInDb.getCategory().getId() ,
                productSavedInDb.getSize()// assuming your DTO has categoryId
        );

        log.info("Returning response DTO: {}", responseDto);

        return Optional.of(responseDto);
    }

    @Override
    @Transactional
    public Optional<Product> findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        product.setViewsCounter(product.getViewsCounter() + 1);
        product.setSearchCounter(product.getSearchCounter() + 1);
//        productRepository.save(product);

        return Optional.of(product);
    }


    @Override
    @Transactional
    public Page<Product> getProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        List<Product> filtered = products.stream()
                .filter(product -> Boolean.TRUE.equals(product.getIsVerified()))
                .peek(product -> product.setViewsCounter(product.getViewsCounter() + 1)) // زيادة views
                .toList();
        return  new PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    @Transactional
    public Page<Product> findByCategoryId(Long categoryId, Pageable pageable) {
        Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
        List<Product> filtered = products.stream()
                .filter(product -> Boolean.TRUE.equals(product.getIsVerified()))
                .peek(product -> product.setViewsCounter(product.getViewsCounter() + 1)) // زيادة views
                .toList();
        return  new PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    @Transactional
    public Page<Product> findBySellerId(Long sellerId, Pageable pageable) {
        Page<Product> products = productRepository.findBySellerId(sellerId, pageable);
        List<Product> filtered = products.stream()
                .filter(product -> Boolean.TRUE.equals(product.getIsVerified()))
                .peek(product -> product.setViewsCounter(product.getViewsCounter() + 1)) // زيادة views
                .toList();
        return  new PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    @Transactional
    public Page<Product> findByViewsCounter(Pageable pageable) {

        Page<Product> products = productRepository.findAllByOrderByViewsCounterDesc(pageable);
        List<Product> filtered = products.stream()
                .filter(product -> Boolean.TRUE.equals(product.getIsVerified()))
                .peek(product -> product.setViewsCounter(product.getViewsCounter() + 1)) // زيادة views
                .toList();
        return  new PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    @Transactional
    public Page<Product> findBySearchCounter(Pageable pageable) {

        Page<Product> products = productRepository.findAllByOrderBySearchCounterDesc(pageable);

        List<Product> filtered = products.stream()
                .filter(product -> Boolean.TRUE.equals(product.getIsVerified()))
                .peek(product -> product.setViewsCounter(product.getViewsCounter() + 1)) // زيادة views
                .toList();
        return  new PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    public Page<Product> findByIsVerified(Boolean isVerified, Pageable pageable) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").ascending()
        );
        Page<Product> products = productRepository.findByIsVerified(isVerified, sorted);

        List<Product> filtered = products.stream()
                .filter(product -> Boolean.TRUE.equals(product.getIsVerified()))
                .peek(product -> product.setViewsCounter(product.getViewsCounter() + 1)) // زيادة views
                .toList();
        return  new PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    @Transactional
    public boolean updateProductStatus(Long id, boolean status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        product.setIsVerified(status);
        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            email = principal.toString();
        }

        Long userId = userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
// نشر الحدث
        eventPublisher.publishEvent(new ProductEvent(product, Type.UPDATED, email,userId));
        log.info("Product saved in database: {}", product);
        return true;
    }

    @Override
    @Transactional
    public Optional<CreateProductDto> updateProduct(Long id, CreateProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // تحديث الحقول فقط إذا مش null
        if (dto.title() != null) product.setTitle(dto.title());
        if (dto.description() != null) product.setDescription(dto.description());
        if (dto.priceBeforeDiscount() != null) product.setMaximumRetailPrice(dto.priceBeforeDiscount());
        if (dto.priceAfterDiscount() != null) product.setSellingPrice(dto.priceAfterDiscount());
        if (dto.quantity() != null) product.setQuantity(dto.quantity());
        if (dto.color() != null) product.setColor(dto.color());
        if (dto.size() != null) product.setSize(dto.size());

        // تحديث التصنيف فقط لو موجود
        if (dto.categoryId() != null) {
            Category category = categoryService.getCategory(dto.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
            product.setCategory(category);
        }
        product.setIsVerified(Boolean.FALSE);
        // ممكن لو عايز ترجع DTO محدث
        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            email = principal.toString();
        }
        Long userId = userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

// نشر الحدث
        eventPublisher.publishEvent(new ProductEvent(product, Type.UPDATED, email,userId) );
        log.info("Product saved in database: {}", product);
        return Optional.of(mapToDto(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        productRepository.deleteById(id);
        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            email = principal.toString();
        }
        Long userId = userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

// نشر الحدث
        eventPublisher.publishEvent(new ProductEvent(product, Type.DELETED, email,userId) );
        log.info("Product saved in database: {}", product);
    }

    // تحويل المنتج لـ DTO
    private CreateProductDto mapToDto(Product product) {
        return new CreateProductDto(
                product.getTitle(),
                product.getDescription(),
                product.getMaximumRetailPrice(),
                product.getSellingPrice(),
                product.getQuantity(),
                product.getColor(),
                product.getCategory().getId(),
                product.getSize()
        );
    }



}
