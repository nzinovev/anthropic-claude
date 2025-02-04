package com.example.anthropic.claude.service;

import com.example.anthropic.claude.domain.entity.Category;
import com.example.anthropic.claude.domain.entity.Operation;
import com.example.anthropic.claude.domain.entity.OperationType;
import com.example.anthropic.claude.dto.OperationCreateRequest;
import com.example.anthropic.claude.dto.OperationResponse;
import com.example.anthropic.claude.dto.OperationUpdateRequest;
import com.example.anthropic.claude.dto.PageResponse;
import com.example.anthropic.claude.exception.CategoryNotFoundException;
import com.example.anthropic.claude.mapper.OperationMapper;
import com.example.anthropic.claude.repository.CategoryRepository;
import com.example.anthropic.claude.repository.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationServiceTest {

    @Mock
    private OperationRepository operationRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private OperationMapper operationMapper;
    @InjectMocks
    private OperationService sut;

    private Operation operation;
    private Category category;
    private OperationResponse operationResponse;
    private OperationCreateRequest createRequest;
    private OperationUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);

        operation = new Operation();
        operation.setId(1L);
        operation.setPublicId("test-public-id");
        operation.setName("Test Operation");
        operation.setAmount(BigDecimal.valueOf(100));
        operation.setType(OperationType.DEPOSIT);
        operation.setCategory(category);

        operationResponse = new OperationResponse();
        operationResponse.setPublicId("test-public-id");
        operationResponse.setName("Test Operation");
        operationResponse.setAmount(BigDecimal.valueOf(100));
        operationResponse.setType(OperationType.DEPOSIT);
        operationResponse.setCategoryId(1L);

        createRequest = new OperationCreateRequest();
        createRequest.setName("Test Operation");
        createRequest.setAmount(BigDecimal.valueOf(100));
        createRequest.setType(OperationType.DEPOSIT);
        createRequest.setCategoryId(1L);

        updateRequest = new OperationUpdateRequest();
        updateRequest.setName("Updated Operation");
        updateRequest.setAmount(BigDecimal.valueOf(200));
        updateRequest.setType(OperationType.WITHDRAW);
        updateRequest.setCategoryId(1L);
    }

    @Test
    void createOperation_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(operationMapper.toEntity(createRequest)).thenReturn(operation);
        when(operationRepository.save(any(Operation.class))).thenReturn(operation);
        when(operationMapper.toDto(operation)).thenReturn(operationResponse);

        OperationResponse result = sut.createOperation(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo("test-public-id");
    }

    @Test
    void createOperation_CategoryNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> sut.createOperation(createRequest));
        verify(operationRepository, never()).save(any(Operation.class));
    }

    @Test
    void updateOperation_Success() {
        when(operationRepository.findByPublicId("test-public-id")).thenReturn(Optional.of(operation));
        when(categoryRepository.getReferenceById(1L)).thenReturn(category);
        when(operationRepository.save(any(Operation.class))).thenReturn(operation);
        when(operationMapper.toDto(operation)).thenReturn(operationResponse);

        OperationResponse result = sut.updateOperation("test-public-id", updateRequest);

        assertThat(result).isNotNull();
        verify(operationRepository).save(any(Operation.class));
    }

    @Test
    void updateOperation_NotFound() {
        when(operationRepository.findByPublicId("test-public-id")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> sut.updateOperation("test-public-id", updateRequest));
        verify(operationRepository, never()).save(any(Operation.class));
    }

    @Test
    void findAll_Success() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Operation> operations = List.of(operation);
        Page<Operation> operationPage = new PageImpl<>(operations, pageRequest, 1);

        when(operationRepository.findAll(pageRequest)).thenReturn(operationPage);
        when(operationMapper.toDto(operation)).thenReturn(operationResponse);

        PageResponse<OperationResponse> result = sut.findAll(pageRequest);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findByPublicId_Success() {
        when(operationRepository.findByPublicId("test-public-id")).thenReturn(Optional.of(operation));
        when(operationMapper.toDto(operation)).thenReturn(operationResponse);

        OperationResponse result = sut.findByPublicId("test-public-id");

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo("test-public-id");
    }

    @Test
    void findByPublicId_NotFound() {
        when(operationRepository.findByPublicId("test-public-id")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> sut.findByPublicId("test-public-id"));
    }

    @Test
    void deleteOperation_Success() {
        when(operationRepository.findByPublicId("test-public-id")).thenReturn(Optional.of(operation));

        sut.deleteOperation("test-public-id");

        verify(operationRepository).delete(operation);
    }

    @Test
    void deleteOperation_NotFound() {
        when(operationRepository.findByPublicId("test-public-id")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> sut.deleteOperation("test-public-id"));
        verify(operationRepository, never()).delete(any(Operation.class));
    }

    @Test
    void updateOperation_PartialUpdate() {
        OperationUpdateRequest partialRequest = new OperationUpdateRequest();
        partialRequest.setName("Updated Name");  // Only update name

        when(operationRepository.findByPublicId("test-public-id")).thenReturn(Optional.of(operation));
        when(operationRepository.save(any(Operation.class))).thenReturn(operation);
        when(operationMapper.toDto(operation)).thenReturn(operationResponse);

        OperationResponse result = sut.updateOperation("test-public-id", partialRequest);

        assertThat(result).isNotNull();
        verify(operationRepository).save(any(Operation.class));
        verify(categoryRepository, never()).getReferenceById(any());  // Category should not be updated
    }
}