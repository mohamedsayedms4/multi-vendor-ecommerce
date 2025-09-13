package org.example.ecommerce.infrastructure.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.category.CategoryService;
import org.example.ecommerce.domain.common.exception.INTERNAL_SERVER_ERROR;
import org.example.ecommerce.domain.model.category.Category;
import org.example.ecommerce.domain.model.category.exception.CategoryNotFoundException;
import org.example.ecommerce.infrastructure.dto.category.CategoryDto;
import org.example.ecommerce.infrastructure.dto.category.CreateCategoryWithoutIconDto;
import org.example.ecommerce.infrastructure.mapper.CategoryMapper;
import org.example.ecommerce.infrastructure.utils.ImageUploadUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing categories in the e-commerce system.
 * Provides endpoints for CRUD operations and retrieval of categories.
 */
@RestController
@RequestMapping("/api/v1/Categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;
    private final ImageUploadUtil imageUploadUtil;
    private final CategoryMapper categoryMapper;

    /**
     * Creates a new category with an optional icon.
     * Access restricted to users with ROLE_ADMIN.
     *
     * @param createCategoryDto the category details without icon
     * @param icon              the icon file for the category (optional)
     * @return ResponseEntity containing the created category DTO
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> save(
            @Valid @RequestPart("category-details") CreateCategoryWithoutIconDto createCategoryDto,
            @RequestPart(value = "icon", required = false) MultipartFile icon
    ) {
        return categoryService.createCategory(createCategoryDto, icon)
                .map(dto -> {
                    log.info("Category created successfully with categoryId={}", dto.categoryId());
                    return ResponseEntity.ok(dto);
                })
                .orElseThrow(() -> {
                    log.error("Failed to create category with name={}", createCategoryDto.nameEn());
                    return new INTERNAL_SERVER_ERROR("Category could not be created");
                });
    }

    /**
     * Updates an existing category with new details and icon.
     * Access restricted to users with ROLE_ADMIN.
     *
     * @param categoryDto the updated category details
     * @param icon        the new icon file for the category
     * @param id          the ID of the category to update
     * @return ResponseEntity containing the updated category DTO
     */
    @PatchMapping()
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> update(
            @RequestPart("category-details") CreateCategoryWithoutIconDto categoryDto,
            @RequestPart("icon") MultipartFile icon,
            @RequestParam("id") Long id
    ) {
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

    /**
     * Deletes a category by its ID.
     * Access restricted to users with ROLE_ADMIN.
     *
     * @param id the ID of the category to delete
     * @return ResponseEntity with success message if deletion succeeds
     */
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
            throw new CategoryNotFoundException("Category with id=" + id);
        }
    }

    /**
     * Retrieves all categories in DTO format.
     *
     * @return ResponseEntity containing list of all category DTOs
     */
    @GetMapping()
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    /**
     * Retrieves a category by its ID in DTO format.
     *
     * @param id the ID of the category
     * @return ResponseEntity containing the category DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable("id") Long id) {
        CategoryDto category = categoryService.getCategory(id)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        return ResponseEntity.ok(category);
    }

    /**
     * Retrieves a category by its ID (admin view).
     * Access restricted to users with ROLE_ADMIN.
     *
     * @param id the ID of the category
     * @return ResponseEntity containing the category entity
     */
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Category> getCategoryAdmin(@PathVariable("id") Long id) {
        Category category = categoryService.getCategory(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        return ResponseEntity.ok(category);
    }

    /**
     * Retrieves all categories (admin view).
     * Access restricted to users with ROLE_ADMIN.
     *
     * @return ResponseEntity containing list of all categories
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Category>> getAllCategoriesAdmin() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}
