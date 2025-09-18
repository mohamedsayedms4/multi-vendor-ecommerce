package org.example.ecommerce.infrastructure.mapper;

import org.example.ecommerce.domain.model.product.Product;
import org.example.ecommerce.domain.model.category.Category;
import org.example.ecommerce.infrastructure.dto.product.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "seller", source = ".", qualifiedByName = "mapSeller")
    @Mapping(target = "category", source = ".", qualifiedByName = "mapCategoryParent")
    ProductDTO toDTO(Product product);

    @Named("mapSeller")
    default ProductDTO.SellerDTO mapSeller(Product product) {
        if (product.getSeller() == null) return null;
        return new ProductDTO.SellerDTO(
                product.getSeller().getId(),
                product.getSeller().getBusinessDetails().getBusinessName()
        );
    }

    @Named("mapCategoryParent")
    default ProductDTO.CategoryDTO mapCategoryParent(Product product) {
        if (product.getCategory() == null) return null;
        Category cat = product.getCategory();
        while (cat.getParentCategory() != null) {
            cat = cat.getParentCategory(); // نطلع للأب الأعلى
        }
        return new ProductDTO.CategoryDTO(
                cat.getId(),
                cat.getNameEn()
        );
    }
}
