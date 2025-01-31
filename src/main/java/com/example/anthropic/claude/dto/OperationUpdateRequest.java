package com.example.anthropic.claude.dto;

import com.example.anthropic.claude.domain.entity.OperationType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OperationUpdateRequest {
    private String name;
    private BigDecimal amount;
    private OperationType type;
    private Long categoryId;
}
