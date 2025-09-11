package org.example.ecommerce.application.service.category;

import org.example.ecommerce.infrastructure.dto.category.CategoryAdminDto;
import org.example.ecommerce.infrastructure.dto.category.CategoryUserDtoEn;
import org.example.ecommerce.infrastructure.dto.category.CreateCategoryDto;
import org.example.ecommerce.infrastructure.dto.category.CreateCategoryWithoutIconDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

public interface CategoryService {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<CreateCategoryDto> createCategory(CreateCategoryDto categoryDto);
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<CreateCategoryDto> updateCategory(Long id ,CreateCategoryDto createCategoryDto);
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Boolean deleteCategory(Long id);


    Optional<CategoryAdminDto> getCategoryByAdmin(Long id);
    Optional<CategoryUserDtoEn> getCategoryByUser(Long id);

    Page<CategoryAdminDto> getCategories(Pageable pageable);



}
