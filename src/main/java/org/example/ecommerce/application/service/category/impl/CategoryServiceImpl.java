package org.example.ecommerce.application.service.category.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.category.CategoryService;
import org.example.ecommerce.domain.common.exception.ImageIsRequired;
import org.example.ecommerce.domain.model.category.Category;
import org.example.ecommerce.domain.model.category.exception.CategoryAlreadyExistsException;
import org.example.ecommerce.domain.model.category.exception.CategoryNotFoundException;
import org.example.ecommerce.domain.model.category.reppository.CategoryRepository;
import org.example.ecommerce.infrastructure.dto.category.*;
import org.example.ecommerce.infrastructure.event.CategoryEvent;
import org.example.ecommerce.infrastructure.event.Type;
import org.example.ecommerce.infrastructure.mapper.CategoryMapper;
import org.example.ecommerce.infrastructure.utils.ImageUploadUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service implementation for handling CRUD operations on categories.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ImageUploadUtil imageUploadUtil;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Creates a new category with the provided details and icon.
     *
     * @param categoryDto the DTO containing category data without icon
     * @param icon        the icon file for the category
     * @return Optional containing the created category DTO
     * @throws ImageIsRequired                  if the icon file is missing
     * @throws CategoryAlreadyExistsException   if a category with the same name or ID already exists
     * @throws CategoryNotFoundException        if the parent category does not exist
     */
    @Override
    @Transactional
    public Optional<CreateCategoryDto> createCategory(CreateCategoryWithoutIconDto categoryDto, MultipartFile icon) {
        log.info("Starting category creation: nameEn={}, nameAr={}, categoryId={}, parentCategoryId={}, level={}",
                categoryDto.nameEn(), categoryDto.nameAr(), categoryDto.categoryId(), categoryDto.parentCategoryId(), categoryDto.level());

        if (icon.isEmpty()) {
            throw new ImageIsRequired("Logo is required");
        }

        String iconImageUrl = imageUploadUtil.saveImage(icon);

        if(categoryRepository.findByNameEn(categoryDto.nameEn()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name " + categoryDto.nameEn() + " already exists");
        }
        if(categoryRepository.findByNameAr(categoryDto.nameAr()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name " + categoryDto.nameAr() + " already exists");
        }
        if(categoryRepository.findByCategoryId(categoryDto.categoryId()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with id " + categoryDto.categoryId() + " already exists");
        }

        Category category = new Category();
        category.setNameEn(categoryDto.nameEn());
        category.setNameAr(categoryDto.nameAr());
        category.setCategoryId(categoryDto.categoryId());
        category.setLevel(categoryDto.level());
        category.setImageUrl(iconImageUrl);

        if (categoryDto.parentCategoryId() != null) {
            Category parent = categoryRepository.findById(categoryDto.parentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Parent category not found"));
            category.setParentCategory(parent);
        }

        Category saved = categoryRepository.save(category);

        String username = getCurrentUsername();

        eventPublisher.publishEvent(new CategoryEvent(saved, Type.CREATED, username));
        log.info("Category saved successfully with id={} and name={}", saved.getId(), saved.getNameEn());

        CreateCategoryDto responseDto = new CreateCategoryDto(
                saved.getNameEn(),
                saved.getNameAr(),
                saved.getCategoryId(),
                saved.getParentCategory() != null ? saved.getParentCategory().getId() : null,
                saved.getLevel(),
                saved.getImageUrl()
        );

        return Optional.of(responseDto);
    }

    /**
     * Updates an existing category with new details and icon.
     *
     * @param id                the ID of the category to update
     * @param createCategoryDto the DTO containing updated data
     * @param icon              the new icon file
     * @return Optional containing the updated category DTO
     * @throws ImageIsRequired                  if the icon file is missing
     * @throws CategoryAlreadyExistsException   if a category with the same name or ID already exists
     * @throws CategoryNotFoundException        if the category or parent category does not exist
     */
    @Override
    @Transactional
    public Optional<CreateCategoryDto> updateCategory(Long id, CreateCategoryWithoutIconDto createCategoryDto, MultipartFile icon) {
        log.info("Starting update for category ID: {}", id);

        if (icon.isEmpty()) {
            throw new ImageIsRequired("Logo is required");
        }

        String iconImageUrl = imageUploadUtil.saveImage(icon);
        log.info("Icon image saved successfully: {}", iconImageUrl);

        if (createCategoryDto.nameEn() != null
                && categoryRepository.findByNameEn(createCategoryDto.nameEn())
                .filter(c -> !c.getId().equals(id))
                .isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name " + createCategoryDto.nameEn() + " already exists");
        }

        if (createCategoryDto.nameAr() != null
                && categoryRepository.findByNameAr(createCategoryDto.nameAr())
                .filter(c -> !c.getId().equals(id))
                .isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name " + createCategoryDto.nameAr() + " already exists");
        }

        if (createCategoryDto.categoryId() != null
                && categoryRepository.findByCategoryId(createCategoryDto.categoryId())
                .filter(c -> !c.getId().equals(id))
                .isPresent()) {
            throw new CategoryAlreadyExistsException("Category with id " + createCategoryDto.categoryId() + " already exists");
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id " + id + " not found"));

        if (createCategoryDto.nameEn() != null) category.setNameEn(createCategoryDto.nameEn());
        if (createCategoryDto.nameAr() != null) category.setNameAr(createCategoryDto.nameAr());
        if (createCategoryDto.categoryId() != null) category.setCategoryId(createCategoryDto.categoryId());
        if (createCategoryDto.level() != null) category.setLevel(createCategoryDto.level());
        category.setImageUrl(iconImageUrl);

        if (createCategoryDto.parentCategoryId() != null) {
            Category parent = categoryRepository.findById(createCategoryDto.parentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Parent category with id " + createCategoryDto.parentCategoryId() + " not found"));
            category.setParentCategory(parent);
        }

        Category updatedCategory = categoryRepository.save(category);

        String username = getCurrentUsername();
        eventPublisher.publishEvent(new CategoryEvent(updatedCategory, Type.UPDATED, username));

        CreateCategoryDto dto = new CreateCategoryDto(
                updatedCategory.getNameEn(),
                updatedCategory.getNameAr(),
                updatedCategory.getCategoryId(),
                updatedCategory.getParentCategory() != null ? updatedCategory.getParentCategory().getId() : null,
                updatedCategory.getLevel(),
                updatedCategory.getImageUrl()
        );

        log.info("Category updated successfully: id={}, name={}", updatedCategory.getId(), updatedCategory.getNameEn());
        return Optional.of(dto);
    }

    /**
     * Deletes a category by ID, including all child categories.
     *
     * @param id the ID of the category to delete
     * @return true if deletion was successful
     * @throws CategoryNotFoundException if the category does not exist
     */
    @Override
    @Transactional
    public Boolean deleteCategory(Long id) {
        log.info("Attempting to delete category with id={}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id " + id + " not found"));

        List<Category> childCategories = categoryRepository.findByParentCategory(category);
        if (!childCategories.isEmpty()) {
            categoryRepository.deleteAll(childCategories);
        }

        categoryRepository.delete(category);

        String username = getCurrentUsername();
        eventPublisher.publishEvent(new CategoryEvent(category, Type.DELETED, username));

        log.info("Category deleted successfully: id={}", id);
        return true;
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the category
     * @return Optional containing the category if found
     */
    @Override
    public Optional<Category> getCategory(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Retrieves all categories in a hierarchical structure.
     *
     * @return list of root categories with children populated
     */
    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        Map<Long, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        for (Category category : categories) {
            if (category.getParentCategory() != null) {
                Category parent = categoryMap.get(category.getParentCategory().getId());
                if (parent != null) {
                    parent.getChildren().add(category);
                }
            }
        }

        return categories.stream()
                .filter(c -> c.getParentCategory() == null)
                .toList();
    }

    /**
     * Helper method to get the current authenticated username.
     *
     * @return the username of the authenticated user
     */
    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return principal.toString();
    }
}
