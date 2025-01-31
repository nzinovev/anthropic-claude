package com.example.anthropic.claude.exception;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationErrorResponse {
    private List<ValidationError> errors = new ArrayList<>();

    public record ValidationError(String field, String message) {
    }
}
