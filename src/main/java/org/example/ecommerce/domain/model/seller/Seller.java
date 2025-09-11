package org.example.ecommerce.domain.model.seller;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.ecommerce.domain.common.BaseEntity;
import org.example.ecommerce.domain.model.user.User;

@Entity
@Table(name = "sellers")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Seller extends BaseEntity {

    @Embedded
    private BusinessDetails businessDetails = new BusinessDetails();

    private Boolean isEmailVerified = false;


    /*
    * unique id to map
    * */

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus ;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonBackReference

    private User user;


}
