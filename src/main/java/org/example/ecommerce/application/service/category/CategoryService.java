package org.example.ecommerce.application.service.category;

import org.example.ecommerce.domain.model.category.Category;
import org.example.ecommerce.infrastructure.dto.category.CategoryAdminDto;
import org.example.ecommerce.infrastructure.dto.category.CategoryUserDtoEn;
import org.example.ecommerce.infrastructure.dto.category.CreateCategoryDto;
import org.example.ecommerce.infrastructure.dto.category.CreateCategoryWithoutIconDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface CategoryService {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<CreateCategoryDto> createCategory(CreateCategoryWithoutIconDto categoryDto , MultipartFile icon);
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<CreateCategoryDto> updateCategory(Long id ,CreateCategoryWithoutIconDto createCategoryDto,MultipartFile icon);
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Boolean deleteCategory(Long id);

    Optional<Category> getCategory(Long id);

    Optional<CategoryAdminDto> getCategoryByAdmin(Long id);
    Optional<CategoryUserDtoEn> getCategoryByUser(Long id);

    Page<CategoryAdminDto> getCategories(Pageable pageable);



}
