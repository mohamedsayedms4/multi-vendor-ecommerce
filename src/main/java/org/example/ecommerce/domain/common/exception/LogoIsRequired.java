package org.example.ecommerce.domain.common.exception;

public class LogoIsRequired extends RuntimeException {
    public LogoIsRequired(String message) {
        super(message);
    }
}
