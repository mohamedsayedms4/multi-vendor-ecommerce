package org.example.ecommerce.domain.common.exception;

public class ImageIsRequired extends RuntimeException {
    public ImageIsRequired(String message) {
        super(message);
    }
}
