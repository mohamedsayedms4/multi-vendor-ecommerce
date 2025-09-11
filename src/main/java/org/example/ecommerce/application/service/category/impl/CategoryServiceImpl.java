package org.example.ecommerce.application.service.category.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.category.CategoryService;
import org.example.ecommerce.domain.model.category.Category;
import org.example.ecommerce.domain.model.category.exception.CategoryAlreadyExistsException;
import org.example.ecommerce.domain.model.category.exception.CategoryNotFoundException;
import org.example.ecommerce.domain.model.category.reppository.CategoryRepository;
import org.example.ecommerce.infrastructure.dto.category.CategoryAdminDto;
import org.example.ecommerce.infrastructure.dto.category.CategoryUserDtoEn;
import org.example.ecommerce.infrastructure.dto.category.CreateCategoryDto;
import org.example.ecommerce.infrastructure.dto.category.CreateCategoryWithoutIconDto;
import org.example.ecommerce.infrastructure.mapper.CategoryMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;




    @Override
    @Transactional
    public Optional<CreateCategoryDto> createCategory(CreateCategoryDto categoryDto) {
        log.info("Starting category creation: name={}, categoryId={}, parentCategoryId={}, level={}",
                categoryDto.nameEn(),
                categoryDto.nameAr(),
                categoryDto.categoryId(),
                categoryDto.parentCategoryId(),
                categoryDto.level()
        );

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
        category.setImageUrl(categoryDto.imageUrl());

        if (categoryDto.parentCategoryId() != null) {
            log.debug("Fetching parent category with id={}", categoryDto.parentCategoryId());
            Category parent = categoryRepository.findById(categoryDto.parentCategoryId())
                    .orElseThrow(() -> {
                        log.error("Parent category not found for id={}", categoryDto.parentCategoryId());
                        return new CategoryNotFoundException("Parent category not found");
                    });
            category.setParentCategory(parent);
            log.debug("Parent category set successfully: {}", parent.getNameEn());
        }

        Category saved = categoryRepository.save(category);
        log.info("Category saved successfully with id={} and name={}", saved.getId(), saved.getNameEn());

        CreateCategoryDto responseDto = new CreateCategoryDto(
                saved.getNameEn(),
                saved.getNameAr(),
                saved.getCategoryId(),
                saved.getParentCategory() != null ? saved.getParentCategory().getId() : null,
                saved.getLevel(),
                saved.getImageUrl()
        );

        log.info("Returning response DTO for categoryId={}", responseDto.categoryId());
        return Optional.of(responseDto);
    }

    @Override
    public Optional<CreateCategoryDto> updateCategory(Long id, CreateCategoryDto createCategoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category with id " + id + " not found"));

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

        if (createCategoryDto.nameEn() != null) {
            category.setNameEn(createCategoryDto.nameEn());
        }
        if (createCategoryDto.nameAr() != null) {
            category.setNameAr(createCategoryDto.nameAr());
        }
        if (createCategoryDto.categoryId() != null) {
            category.setCategoryId(createCategoryDto.categoryId());
        }
        if (createCategoryDto.level() != null) {
            category.setLevel(createCategoryDto.level());
        }
        if (createCategoryDto.imageUrl() != null) {
            category.setImageUrl(createCategoryDto.imageUrl());
        }

        if (createCategoryDto.parentCategoryId() != null) {
            Category parent = categoryRepository.findById(createCategoryDto.parentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(
                            "Parent category with id " + createCategoryDto.parentCategoryId() + " not found"));
            category.setParentCategory(parent);
        }

        Category updatedCategory = categoryRepository.save(category);

        CreateCategoryDto dto = new CreateCategoryDto(
                updatedCategory.getNameEn(),
                updatedCategory.getNameAr(),
                updatedCategory.getCategoryId(),
                updatedCategory.getParentCategory() != null ? updatedCategory.getParentCategory().getId() : null,
                updatedCategory.getLevel(),
                updatedCategory.getImageUrl()
        );

        return Optional.of(dto);
    }


    @Override
    @Transactional
    public Boolean deleteCategory(Long id) {
        log.info("Attempting to delete category with id={}", id);

        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isEmpty()) {
            log.error("Category not found with id={}", id);
            throw new CategoryNotFoundException("Category with id " + id + " not found");
        }

        Category category = categoryOpt.get();

        List<Category> childCategories = categoryRepository.findByParentCategory(category);
        if (!childCategories.isEmpty()) {
            log.warn("Category id={} has {} child categories. Deleting them as well.", id, childCategories.size());
            categoryRepository.deleteAll(childCategories);
        }

        categoryRepository.delete(category);
        log.info("Category deleted successfully: id={}", id);
        return true;
    }

//    todo
    @Override
    public Optional<CategoryAdminDto> getCategoryByAdmin(Long id) {
        return Optional.empty();
    }
//  todo
    @Override
    public Optional<CategoryUserDtoEn> getCategoryByUser(Long id) {
        return Optional.empty();
    }
//  todo
    @Override
    public Page<CategoryAdminDto> getCategories(Pageable pageable) {
        return null;
    }
}
