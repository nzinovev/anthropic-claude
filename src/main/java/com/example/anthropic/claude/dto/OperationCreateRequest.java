package com.example.anthropic.claude.dto;

import com.example.anthropic.claude.domain.entity.OperationType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OperationCreateRequest {
    @NotBlank(message = "Operation name is required")
    @Size(max = 255, message = "Operation name must not exceed 255 characters")
    private String name;
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 20, fraction = 2, message = "Amount must have at most 20 digits and 2 decimal places")
    private BigDecimal amount;
    @NotNull(message = "Operation type is required")
    private OperationType type;
    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;
}
