package org.example.ecommerce.infrastructure.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.category.CategoryService;
import org.example.ecommerce.domain.common.exception.LogoIsRequired;
import org.example.ecommerce.domain.model.category.exception.CategoryNotFoundException;
import org.example.ecommerce.infrastructure.dto.category.CreateCategoryDto;
import org.example.ecommerce.infrastructure.dto.category.CreateCategoryWithoutIconDto;
import org.example.ecommerce.infrastructure.utils.ImageUploadUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/Categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;
    private final ImageUploadUtil  imageUploadUtil;

    @PostMapping("/0")
    @PreAuthorize("hasRole('ROLE_ADMIN')")

    public ResponseEntity<String> response(){
        return ResponseEntity.ok("OK");
    }
    @PostMapping("")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> save(
            @RequestPart("category-details") String categoryDto,
            @RequestPart("icon") MultipartFile icon
    ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        CreateCategoryWithoutIconDto createCategoryDto = objectMapper.readValue(categoryDto, CreateCategoryWithoutIconDto.class);

        log.info("Received request to create category with name={}, categoryId={}, parentCategoryId={}, level={}",
                createCategoryDto.nameEn(),
                createCategoryDto.nameAr(),
                createCategoryDto.categoryId(),
                createCategoryDto.parentCategoryId(),
                createCategoryDto.level()
        );

        if (icon.isEmpty()) {
            throw new LogoIsRequired("logo is required");
        }
        String iconImageUrl = imageUploadUtil.saveImage(icon);

        CreateCategoryDto createCategoryDto1 = new CreateCategoryDto(
                createCategoryDto.nameEn(),
                createCategoryDto.nameAr(),
                createCategoryDto.categoryId(),
                createCategoryDto.parentCategoryId(),
                createCategoryDto.level(),
                iconImageUrl
        );

        return categoryService.createCategory(createCategoryDto1)
                .map(dto -> {
                    log.info("Category created successfully with categoryId={}", dto.categoryId());
                    return ResponseEntity.ok(dto);
                })
                .orElseThrow(() -> {
                    log.error("Failed to create category with name={}", createCategoryDto.nameEn());
                    return new CategoryNotFoundException("Category could not be created");
                });
    }

    @PatchMapping("/update")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> update(
            @RequestPart("category-details") String categoryDto,
            @RequestPart("icon") MultipartFile icon,
            @RequestParam("id") Long id
    ) throws JsonProcessingException {
        log.info("==== Start update category request ====");
        log.info("Received category ID: {}", id);

        ObjectMapper objectMapper = new ObjectMapper();
        CreateCategoryWithoutIconDto createCategoryDto = objectMapper.readValue(categoryDto, CreateCategoryWithoutIconDto.class);
        log.info("Parsed category details: nameEn={}, nameAr={}, categoryId={}, parentCategoryId={}, level={}",
                createCategoryDto.nameEn(),
                createCategoryDto.nameAr(),
                createCategoryDto.categoryId(),
                createCategoryDto.parentCategoryId(),
                createCategoryDto.level()
        );

        if (icon.isEmpty()) {
            log.error("Icon file is missing for category update: categoryId={}", createCategoryDto.categoryId());
            throw new LogoIsRequired("Logo is required");
        }
        log.info("Icon file received: originalFilename={}, size={} bytes", icon.getOriginalFilename(), icon.getSize());
        String iconImageUrl = imageUploadUtil.saveImage(icon);
        log.info("Icon image saved successfully: {}", iconImageUrl);

        CreateCategoryDto createCategoryDto1 = new CreateCategoryDto(
                createCategoryDto.nameEn(),
                createCategoryDto.nameAr(),
                createCategoryDto.categoryId(),
                createCategoryDto.parentCategoryId(),
                createCategoryDto.level(),
                iconImageUrl
        );

        log.info("Calling categoryService.updateCategory for ID: {}", id);
        return categoryService.updateCategory(id, createCategoryDto1)
                .map(dto -> {
                    log.info("Category updated successfully: categoryId={}, nameEn={}", dto.categoryId(), dto.nameEn());
                    log.info("==== End update category request ====");
                    return ResponseEntity.ok(dto);
                })
                .orElseThrow(() -> {
                    log.error("Failed to update category: nameEn={}", createCategoryDto.nameEn());
                    log.info("==== End update category request with error ====");
                    return new CategoryNotFoundException("Category could not be updated");
                });
    }


    @DeleteMapping("/delete")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteCategory(@RequestParam("id") Long id) {
        log.info("Received request to delete category with id={}", id);

        boolean isDeleted = categoryService.deleteCategory(id);

        if (isDeleted) {
            log.info("Category with id={} deleted successfully", id);
            return ResponseEntity.ok("Category deleted successfully");
        } else {
            throw new  CategoryNotFoundException("Category with id=" + id);
        }
    }


}
