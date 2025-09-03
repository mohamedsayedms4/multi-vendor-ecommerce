package org.example.ecommerce.domain.common;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

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


}
