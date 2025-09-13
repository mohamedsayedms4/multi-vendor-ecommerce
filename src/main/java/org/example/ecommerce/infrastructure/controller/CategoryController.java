package org.example.ecommerce.infrastructure.controller;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.category.CategoryService;
import org.example.ecommerce.domain.common.exception.INTERNAL_SERVER_ERROR;
import org.example.ecommerce.domain.model.category.exception.CategoryNotFoundException;
import org.example.ecommerce.infrastructure.dto.category.CreateCategoryWithoutIconDto;
import org.example.ecommerce.infrastructure.utils.ImageUploadUtil;
import org.springframework.http.MediaType;
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



    @PostMapping( consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> save(
            @Valid @RequestPart("category-details") CreateCategoryWithoutIconDto createCategoryDto,
            @RequestPart(value = "icon" ,required = false) MultipartFile icon
    ) {

        return categoryService.createCategory(createCategoryDto,icon)
                .map(dto -> {
                    log.info("Category created successfully with categoryId={}", dto.categoryId());
                    return ResponseEntity.ok(dto);
                })
                .orElseThrow(() -> {
                    log.error("Failed to create category with name={}", createCategoryDto.nameEn());
                    return new INTERNAL_SERVER_ERROR("Category could not be created");
                });
    }

    @PatchMapping()
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> update(
            @RequestPart("category-details") CreateCategoryWithoutIconDto categoryDto,
            @RequestPart("icon") MultipartFile icon,
            @RequestParam("id") Long id
    )  {


        log.info("Calling categoryService.updateCategory for ID: {}", id);
        return categoryService.updateCategory(id, categoryDto, icon)
                .map(dto -> {
                    log.info("Category updated successfully: categoryId={}, nameEn={}", dto.categoryId(), dto.nameEn());
                    log.info("==== End update category request ====");
                    return ResponseEntity.ok(dto);
                })
                .orElseThrow(() -> {
                    log.error("Failed to update category: nameEn={}", categoryDto.nameEn());
                    log.info("==== End update category request with error ====");
                    return new INTERNAL_SERVER_ERROR("Category could not be updated");
                });
    }


    @DeleteMapping("")
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
