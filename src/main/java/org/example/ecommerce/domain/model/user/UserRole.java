package org.example.ecommerce.domain.model.user;

public enum UserRole {

    /** Full system access and administrative privileges. */
    ROLE_ADMIN,

    /** Access to seller dashboard and product/order management. */
    ROLE_SELLER,

    /** Regular user with purchasing capabilities. */
    ROLE_CUSTOMER
}
