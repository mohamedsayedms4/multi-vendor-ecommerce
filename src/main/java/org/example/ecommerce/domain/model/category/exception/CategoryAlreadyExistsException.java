package org.example.ecommerce.domain.model.category.exception;

/**
 * Exception thrown when attempting to create or update a category
 * that already exists with the same name or ID.
 */
public class CategoryAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new CategoryAlreadyExistsException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}
