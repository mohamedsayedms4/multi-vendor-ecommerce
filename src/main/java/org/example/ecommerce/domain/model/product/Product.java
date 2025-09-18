package org.example.ecommerce.domain.model.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.ecommerce.domain.common.BaseEntity;
import org.example.ecommerce.domain.model.category.Category;
import org.example.ecommerce.domain.model.seller.Seller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@Entity
public class Product extends BaseEntity {
    @Column(nullable = false,columnDefinition= "NVARCHAR(100)")
    private String title;

    @Column(nullable = false,columnDefinition= "NVARCHAR(100)")
    private String description;

    private Long maximumRetailPrice;
    private Long sellingPrice;

    @Min(0)
    @Max(100)
    private Integer discountPercentage;

    @Min(0)
    private Integer quantity;

    private String color;

    @ManyToOne(optional = false)
    private Category category;

    @ManyToOne(optional = false)
    private Seller seller;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    private Long viewsCounter = 0L;

    private Long searchCounter = 0L; // صححت الاسم

    @Enumerated(EnumType.STRING)
    private Size size;

    @ElementCollection
    @CollectionTable(
            name = "product_images",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();

    private Boolean isVerified = false;


    @Override
    protected void onPrePersist() {
        updateDiscountPercentage();
    }

    @Override
    protected void onPreUpdate() {
        updateDiscountPercentage();
           }
    private void updateDiscountPercentage() {
        if (maximumRetailPrice != null && sellingPrice != null && maximumRetailPrice > 0) {
            BigDecimal mrp = BigDecimal.valueOf(maximumRetailPrice);
            BigDecimal sp = BigDecimal.valueOf(sellingPrice);

            BigDecimal discount = mrp.subtract(sp)
                    .divide(mrp, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            this.discountPercentage = discount.intValue();
        } else {
            this.discountPercentage = 0;
        }


    }
}
