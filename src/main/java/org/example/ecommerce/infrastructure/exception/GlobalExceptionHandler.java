package org.example.ecommerce.infrastructure.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.domain.common.exception.FailedLoginAttempt;
import org.example.ecommerce.domain.common.exception.INTERNAL_SERVER_ERROR;
import org.example.ecommerce.domain.common.exception.ImageIsRequired;
import org.example.ecommerce.domain.common.exception.UnauthorizedException;
import org.example.ecommerce.domain.model.category.exception.CategoryAlreadyExistsException;
import org.example.ecommerce.domain.model.category.exception.CategoryNotFoundException;
import org.example.ecommerce.domain.model.product.exception.ProductNotFoundException;
import org.example.ecommerce.domain.model.user.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // validation errors from @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .reduce((m1, m2) -> m1 + "; " + m2)
                .orElse("Validation failed");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorDetails(request, HttpStatus.BAD_REQUEST,
                        errorMessage));
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetails> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        String errorMessage = ex.getConstraintViolations().stream()
                .map(cv -> cv.getMessage())
                .reduce((m1, m2) -> m1 + "; " + m2)
                .orElse("Validation failed");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorDetails(request, HttpStatus.BAD_REQUEST, errorMessage));
    }
    @ExceptionHandler({
            UserAlreadyExistsException.class,
            PhoneNumberAlreadyExists.class,
            EmailAlreadyExists.class,
            CategoryAlreadyExistsException.class
    })
    public ResponseEntity<ErrorDetails> handleConflictExceptions(
            RuntimeException ex,
            HttpServletRequest request) {

        log.warn("Conflict Exception: {}", ex.getMessage());


        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildErrorDetails(request, HttpStatus.CONFLICT, ex.getMessage()));
    }
    @ExceptionHandler({
            UserNotFoundException.class,
            CategoryNotFoundException.class,
            ProductNotFoundException.class
    })
    public ResponseEntity<ErrorDetails> handleNotFoundExceptions(
            RuntimeException ex,
            HttpServletRequest request) {

        log.warn("Conflict Exception: {}", ex.getMessage());


        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorDetails(request, HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler({
            EmailIsNotValid.class,
            PhoneNumberIsNotValid.class,
            NameIsNotVlild.class,
            ImageIsRequired.class,
            InvalidPWD.class

    })
    public ResponseEntity<ErrorDetails> handleBadRequestExceptions(
            RuntimeException ex,
            HttpServletRequest request
    ){
        log.warn("Bad Request: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorDetails(request, HttpStatus.BAD_REQUEST, ex.getMessage()));
    }



    @ExceptionHandler({
            UnauthorizedException.class
    })
    public ResponseEntity<ErrorDetails> handleUnauthorized(
            RuntimeException ex ,
            HttpServletRequest request
    ){
        log.warn(" Unauthorized: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildErrorDetails(request, HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }
    @ExceptionHandler({
            FailedLoginAttempt.class
    })
    public ResponseEntity<ErrorDetails> handleTooManyExceptions(
            RuntimeException ex ,
            HttpServletRequest request
    ){
        log.warn("Too MAny Requests: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(buildErrorDetails(request, HttpStatus.TOO_MANY_REQUESTS, ex.getMessage()));
    }
    @ExceptionHandler({
            INTERNAL_SERVER_ERROR.class
    })
    public ResponseEntity<ErrorDetails> handleInternalServerErrors(
            RuntimeException ex ,
            HttpServletRequest request
    ){
        log.warn("INTERNAL_SERVER_ERROR: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorDetails(request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleAllExceptions(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorDetails(request, HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong"));
    }

    // unified error response - النسخة الأساسية
    private ErrorDetails buildErrorDetails(HttpServletRequest request,
                                           HttpStatus status,
                                           String message) {
        return buildErrorDetails(request, status, message, message);
    }

    // unified error response - النسخة مع التفاصيل
    private ErrorDetails buildErrorDetails(HttpServletRequest request,
                                           HttpStatus status,
                                           String message,
                                           String details) {
        ErrorDetails errorResponse = new ErrorDetails();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(status.value());
        errorResponse.setError(status.getReasonPhrase());
        errorResponse.setMessage(message);
        errorResponse.setPath(request.getRequestURI());
        return errorResponse;
    }
}
