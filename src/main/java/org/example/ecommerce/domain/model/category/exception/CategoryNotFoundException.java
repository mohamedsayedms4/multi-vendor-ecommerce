package org.example.ecommerce.domain.model.category.exception;

/**
 * Exception thrown when a requested category is not found in the system.
 */
public class CategoryNotFoundException extends RuntimeException {

    /**
     * Constructs a new CategoryNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
