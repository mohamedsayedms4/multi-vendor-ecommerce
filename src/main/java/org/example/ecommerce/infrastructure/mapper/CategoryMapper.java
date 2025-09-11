package org.example.ecommerce.infrastructure.mapper;

import org.example.ecommerce.domain.model.category.Category;
import org.example.ecommerce.infrastructure.dto.category.CreateCategoryWithoutIconDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CreateCategoryWithoutIconDto toCreateCategoryDto(Category category);
}
