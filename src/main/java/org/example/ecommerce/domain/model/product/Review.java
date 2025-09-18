package org.example.ecommerce.domain.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.ecommerce.domain.common.BaseEntity;
import org.example.ecommerce.domain.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Review extends BaseEntity {

    /**
     * Text content of the review.
     */
    @Column(nullable = false)
    private String reviewTxt;

    /**
     * Rating score given by the user (e.g., from 1.0 to 5.0).
     */
    @Column(nullable = false)
    private Double rating;

    /**
     * Optional list of image URLs uploaded by the user as part of the review.
     */
    @ElementCollection
    private List<String> productImages;

    /**
     * Product that is being reviewed.
     * Ignored in JSON serialization to avoid circular references.
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * User who wrote the review.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Timestamp when the review was created.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
