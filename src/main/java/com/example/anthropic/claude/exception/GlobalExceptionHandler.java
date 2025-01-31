package com.example.anthropic.claude.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        ValidationErrorResponse response = new ValidationErrorResponse();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                response.getErrors().add(new ValidationErrorResponse.ValidationError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
        );

        return response;
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ValidationErrorResponse handleCategoryNotFoundException(CategoryNotFoundException ex) {
        ValidationErrorResponse response = new ValidationErrorResponse();
        response.getErrors().add(new ValidationErrorResponse.ValidationError(
                "categoryId",
                ex.getMessage()
        ));
        return response;
    }
}
