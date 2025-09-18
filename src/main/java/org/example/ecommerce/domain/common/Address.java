package org.example.ecommerce.domain.common;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.example.ecommerce.domain.model.user.User;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address extends BaseEntity {

    private String name;

    private String locality;


    private String state;


    private String city;


    private String address;

    @ManyToOne
    @JsonBackReference("address-customer")
    @JoinColumn(name="customer_id")
    private User customer;
}
