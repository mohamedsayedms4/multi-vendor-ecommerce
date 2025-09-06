package org.example.ecommerce.domain.model.user.exception;

public class PhoneNumberIsNotValid extends RuntimeException {
    public PhoneNumberIsNotValid(String message) {
        super(message);
    }
}
