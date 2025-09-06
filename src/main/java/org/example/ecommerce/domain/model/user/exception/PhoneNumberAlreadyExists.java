package org.example.ecommerce.domain.model.user.exception;

public class PhoneNumberAlreadyExists extends RuntimeException {
    public PhoneNumberAlreadyExists(String message) {
        super(message);
    }
}
