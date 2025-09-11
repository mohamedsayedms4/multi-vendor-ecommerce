package org.example.ecommerce.domain.common.exception;

public class INTERNAL_SERVER_ERROR extends RuntimeException {
    public INTERNAL_SERVER_ERROR(String message) {
        super(message);
    }
}
