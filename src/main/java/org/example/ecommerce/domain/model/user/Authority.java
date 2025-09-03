package org.example.ecommerce.domain.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "authorities")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    // يخزن القيمة كـ نص في الجدول
    private UserRole role;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="customer_id")
    private User customer;
}
