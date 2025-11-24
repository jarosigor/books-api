package com.morotech.bookApi.exception;

import com.morotech.bookApi.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(com.morotech.bookApi.exception.ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(com.morotech.bookApi.exception.ResourceNotFoundException ex,
                                                                HttpServletRequest request) {
        log.error("Resource not found: {}", ex.getMessage(), ex);
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(com.morotech.bookApi.exception.GutendexApiException.class)
    public ResponseEntity<ErrorResponse> handleGutendexApiException(com.morotech.bookApi.exception.GutendexApiException ex,
                                                                    HttpServletRequest request) {
        log.error("Gutendex API exception occurred: {}", ex.getMessage(), ex);
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(InvalidPageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPageException(InvalidPageException ex,
                                                                    HttpServletRequest request) {
        log.error("Provided page is not valid: {}", ex.getMessage(), ex);
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ObjectMapperException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessingException(ObjectMapperException ex,
                                                                       HttpServletRequest request) {
        log.error("Json processing exception: {}", ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "Validation failed: " + ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "Invalid request parameter: " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "Type mismatch occurred: " + ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse(status.value(), message, OffsetDateTime.now());
        return ResponseEntity.status(status).body(error);
    }

}

