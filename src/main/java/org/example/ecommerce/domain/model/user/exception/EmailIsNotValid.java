package org.example.ecommerce.domain.model.user.exception;

public class EmailIsNotValid extends RuntimeException {
    public EmailIsNotValid(String message) {
        super(message);
    }
}
