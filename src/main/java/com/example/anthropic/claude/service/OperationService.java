package com.example.anthropic.claude.service;

import com.example.anthropic.claude.domain.entity.Category;
import com.example.anthropic.claude.domain.entity.Operation;
import com.example.anthropic.claude.dto.OperationCreateRequest;
import com.example.anthropic.claude.dto.OperationResponse;
import com.example.anthropic.claude.dto.OperationUpdateRequest;
import com.example.anthropic.claude.mapper.OperationMapper;
import com.example.anthropic.claude.repository.CategoryRepository;
import com.example.anthropic.claude.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository operationRepository;
    private final CategoryRepository categoryRepository;
    private final OperationMapper operationMapper;

    @Transactional
    public OperationResponse createOperation(OperationCreateRequest request) {
        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        Operation operation = operationMapper.toEntity(request);
        operation.setCategory(category);

        Operation savedOperation = operationRepository.save(operation);
        return operationMapper.toDto(savedOperation);
    }

    @Transactional
    public OperationResponse updateOperation(String publicId, OperationUpdateRequest request) {
        return operationRepository.findByPublicId(publicId)
                .map(operation -> updateOperation(operation, request))
                .orElseThrow(() -> new RuntimeException("Operation not found"));
    }

    private OperationResponse updateOperation(Operation operation, OperationUpdateRequest request) {
        if (request.getName() != null) {
            operation.setName(request.getName());
        }
        if (request.getAmount() != null) {
            operation.setAmount(request.getAmount());
        }
        if (request.getType() != null) {
            operation.setType(request.getType());
        }
        if (request.getCategoryId() != null) {
            final var category = categoryRepository.getReferenceById(request.getCategoryId());
            operation.setCategory(category);
        }

        final var updatedOperation = operationRepository.save(operation);

        return operationMapper.toDto(updatedOperation);
    }
}
