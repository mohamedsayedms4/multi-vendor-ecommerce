package org.example.ecommerce.infrastructure.mapper;

import org.example.ecommerce.domain.model.category.Category;
import org.example.ecommerce.infrastructure.dto.category.CategoryDto;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface for converting between Category entity and CategoryDto.
 * Uses MapStruct for Spring integration.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Converts a Category entity to CategoryDto, including its children and parent information.
     *
     * @param category the Category entity to convert
     * @return the corresponding CategoryDto, or null if the entity is null
     */
    default CategoryDto toDto(Category category) {
        if (category == null) return null;

        List<CategoryDto> childrenDtos = category.getChildren().stream()
                .map(this::toDto)
                .toList();

        return CategoryDto.builder()
                .id(category.getId())
                .nameEn(category.getNameEn())
                .nameAr(category.getNameAr())
                .categoryId(category.getCategoryId())
                .level(category.getLevel())
                .imageUrl(category.getImageUrl())
                .parentCategory(category.getParentCategory() != null
                        ? new CategoryDto.ParentCategory(
                        category.getParentCategory().getId(),
                        category.getParentCategory().getNameEn(),
                        category.getParentCategory().getNameAr(),
                        category.getParentCategory().getCategoryId()
                )
                        : null)
                .children(childrenDtos)
                .build();
    }

    /**
     * Converts a CategoryDto to a Category entity, including its children and parent information.
     *
     * @param dto the CategoryDto to convert
     * @return the corresponding Category entity, or null if the DTO is null
     */
    default Category toEntity(CategoryDto dto) {
        if (dto == null) return null;

        Category category = new Category();
        category.setId(dto.id());
        category.setNameEn(dto.nameEn());
        category.setNameAr(dto.nameAr());
        category.setCategoryId(dto.categoryId());
        category.setLevel(dto.level());
        category.setImageUrl(dto.imageUrl());

        if (dto.parentCategory() != null) {
            Category parent = new Category();
            parent.setId(dto.parentCategory().id());
            parent.setNameEn(dto.parentCategory().nameEn());
            parent.setNameAr(dto.parentCategory().nameAr());
            parent.setCategoryId(dto.parentCategory().categoryId());
            category.setParentCategory(parent);
        }

        List<Category> children = dto.children() != null
                ? dto.children().stream().map(this::toEntity).toList()
                : List.of();
        category.setChildren(children);

        return category;
    }
}
