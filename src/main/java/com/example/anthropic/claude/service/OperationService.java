package com.example.anthropic.claude.service;

import com.example.anthropic.claude.domain.entity.Category;
import com.example.anthropic.claude.domain.entity.Operation;
import com.example.anthropic.claude.dto.OperationCreateRequest;
import com.example.anthropic.claude.dto.OperationResponse;
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
}
