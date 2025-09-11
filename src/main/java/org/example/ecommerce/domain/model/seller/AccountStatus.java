package org.example.ecommerce.domain.model.seller;

public enum AccountStatus {

    /** Account is created but waiting for customerEmail or phone verification. */
    PENDING_VERIFICATION,

    /** Account is active and can use the system without restriction. */
    ACTIVE,

    /** Account is banned due to serious violations (e.g., fraud). */
    BANNED,

    /** Account is permanently closed and cannot be reactivated. */
    CLOSED
}