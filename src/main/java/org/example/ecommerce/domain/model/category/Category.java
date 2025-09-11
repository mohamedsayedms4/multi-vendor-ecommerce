package org.example.ecommerce.domain.model.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.domain.common.BaseEntity;
import org.example.ecommerce.domain.common.BaseIdEntity;


@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Category extends BaseEntity {

    /**
     * Electronics (level 0)
     *  ├── Mobiles (level 1)
     *  │    └── Accessories (level 2)
     *  └── Laptops (level 1)
     *  */
    @Column(unique = true, nullable = false)

    private String nameEn;

    @Column(unique = true,nullable = false,columnDefinition= "NVARCHAR(100)")
    private String nameAr;


    @NotNull
    @Column(unique = true)
    private String categoryId;


    @ManyToOne
    private Category parentCategory;

    @NotNull
    private Integer level;

    private String imageUrl;
}
