package org.example.ecommerce.domain.model.user.exception;

public class InvalidPWD extends RuntimeException {
    public InvalidPWD(String message) {
        super(message);
    }
}
