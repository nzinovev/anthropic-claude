package com.example.anthropic.claude.service;

import com.example.anthropic.claude.domain.entity.Operation;
import com.example.anthropic.claude.dto.OperationCreateRequest;
import com.example.anthropic.claude.dto.OperationResponse;
import com.example.anthropic.claude.dto.OperationUpdateRequest;
import com.example.anthropic.claude.dto.PageResponse;
import com.example.anthropic.claude.exception.NotFoundException;
import com.example.anthropic.claude.mapper.OperationMapper;
import com.example.anthropic.claude.repository.CategoryRepository;
import com.example.anthropic.claude.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperationService {

    public static final String OPERATION_WITH_ID_NOT_FOUND = "Operation with id %s not found";
    private final OperationRepository operationRepository;
    private final CategoryRepository categoryRepository;
    private final OperationMapper operationMapper;

    @Transactional
    public OperationResponse createOperation(OperationCreateRequest request) {
        final var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Category with id %d not found", request.getCategoryId())));

        final var operation = operationMapper.toEntity(request);
        operation.setCategory(category);

        final var savedOperation = operationRepository.save(operation);
        return operationMapper.toDto(savedOperation);
    }

    @Transactional
    public OperationResponse updateOperation(String publicId, OperationUpdateRequest request) {
        return operationRepository.findByPublicId(publicId)
                .map(operation -> updateOperation(operation, request))
                .orElseThrow(() -> new NotFoundException(String.format(OPERATION_WITH_ID_NOT_FOUND, publicId)));
    }

    @Transactional(readOnly = true)
    public PageResponse<OperationResponse> findAll(Pageable pageable) {
        final var operationPage = operationRepository.findAll(pageable);
        final var responsePage = operationPage.map(operationMapper::toDto);
        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public OperationResponse findByPublicId(String publicId) {
        final var operation = operationRepository.findByPublicId(publicId)
                .orElseThrow(() -> new NotFoundException(String.format(OPERATION_WITH_ID_NOT_FOUND, publicId)));
        return operationMapper.toDto(operation);
    }

    @Transactional
    public void deleteOperation(String publicId) {
        final var operation = operationRepository.findByPublicId(publicId)
                .orElseThrow(() -> new NotFoundException(String.format(OPERATION_WITH_ID_NOT_FOUND, publicId)));

        operationRepository.delete(operation);
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
