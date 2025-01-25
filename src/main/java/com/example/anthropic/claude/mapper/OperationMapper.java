package com.example.anthropic.claude.mapper;

import com.example.anthropic.claude.domain.entity.Operation;
import com.example.anthropic.claude.dto.OperationCreateRequest;
import com.example.anthropic.claude.dto.OperationResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OperationMapper {

    public Operation toEntity(OperationCreateRequest request) {
        Operation operation = new Operation();
        operation.setPublicId(UUID.randomUUID().toString());
        operation.setName(request.getName());
        operation.setAmount(request.getAmount());
        operation.setType(request.getType());
        return operation;
    }

    public OperationResponse toDto(Operation operation) {
        OperationResponse response = new OperationResponse();
        response.setPublicId(operation.getPublicId());
        response.setName(operation.getName());
        response.setAmount(operation.getAmount());
        response.setType(operation.getType());
        response.setCategoryId(operation.getCategory().getId());
        return response;
    }
}
