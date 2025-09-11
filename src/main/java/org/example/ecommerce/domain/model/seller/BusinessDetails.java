package org.example.ecommerce.domain.model.seller;


import jakarta.persistence.Column;
import lombok.Data;

@Data
public class BusinessDetails {

    /**
     * The name of the business.
     */
    @Column(unique = true)
    private String businessName;

    /**
     * The official customerEmail address of the business.
     */
    @Column(unique = true)
    private String businessEmail;

    /**
     * The mobile phone number for the business.
     */
    @Column(unique = true)

    private String businessMobile;

    /**
     * The physical address of the business.
     */
    private String businessAddress;

    /**
     * The URL or path to the business logo image.
     */
    private String logo;

    /**
     * The URL or path to the business banner image.
     */
    private String banner;
}
