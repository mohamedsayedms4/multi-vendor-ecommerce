package org.example.ecommerce.domain.model.cart;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.example.ecommerce.domain.common.BaseEntity;
import org.example.ecommerce.domain.model.user.User;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "carts")
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Cart extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;


    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    private Integer quantity = 0;

    private String couponCode;


    private Long totalMaximumRetailPrice =0l;

    private Long discount =0l;

    private Long totalSellingPrice =0l;

    private String tempId; // معرف مؤقت للمستخدم الغير مسجل


}
