package org.example.ecommerce.infrastructure.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryFullDto {
    private Long id;
    private String nameEn;
    private String nameAr;
    private String categoryId;
    private Integer level;
    private String imageUrl;
    private CategoryFullDto parentCategory; // parent
    private List<CategoryFullDto> childCategories; // children
}
