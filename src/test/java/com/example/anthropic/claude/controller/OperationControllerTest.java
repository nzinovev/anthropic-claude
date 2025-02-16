package com.example.anthropic.claude.controller;

import com.example.anthropic.claude.domain.entity.OperationType;
import com.example.anthropic.claude.dto.OperationCreateRequest;
import com.example.anthropic.claude.dto.OperationResponse;
import com.example.anthropic.claude.dto.OperationUpdateRequest;
import com.example.anthropic.claude.dto.PageResponse;
import com.example.anthropic.claude.exception.NotFoundException;
import com.example.anthropic.claude.service.OperationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OperationController.class)
class OperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OperationService operationService;

    private OperationCreateRequest createRequest;
    private OperationUpdateRequest updateRequest;
    private OperationResponse operationResponse;
    private PageResponse<OperationResponse> pageResponse;

    @BeforeEach
    void setUp() {
        createRequest = new OperationCreateRequest();
        createRequest.setName("Test Operation");
        createRequest.setAmount(BigDecimal.valueOf(100));
        createRequest.setType(OperationType.DEPOSIT);
        createRequest.setCategoryId(1L);

        updateRequest = new OperationUpdateRequest();
        updateRequest.setName("Updated Operation");
        updateRequest.setAmount(BigDecimal.valueOf(200));
        updateRequest.setType(OperationType.WITHDRAW);
        updateRequest.setCategoryId(2L);

        operationResponse = new OperationResponse();
        operationResponse.setPublicId("test-public-id");
        operationResponse.setName("Test Operation");
        operationResponse.setAmount(BigDecimal.valueOf(100));
        operationResponse.setType(OperationType.DEPOSIT);
        operationResponse.setCategoryId(1L);

        pageResponse = new PageResponse<>();
        pageResponse.setContent(List.of(operationResponse));
        pageResponse.setPageNumber(0);
        pageResponse.setPageSize(20);
        pageResponse.setTotalElements(1);
        pageResponse.setTotalPages(1);
        pageResponse.setLast(true);
    }

    @Test
    void createOperation_Success() throws Exception {
        when(operationService.createOperation(any(OperationCreateRequest.class)))
                .thenReturn(operationResponse);

        mockMvc.perform(post("/api/v1/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.publicId").value("test-public-id"))
                .andExpect(jsonPath("$.name").value("Test Operation"))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.categoryId").value(1));

        verify(operationService).createOperation(any(OperationCreateRequest.class));
    }

    @Test
    void createOperation_ValidationFailure() throws Exception {
        createRequest.setName("");
        createRequest.setAmount(BigDecimal.valueOf(-100));
        createRequest.setType(null);
        createRequest.setCategoryId(null);

        mockMvc.perform(post("/api/v1/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(4));

        verify(operationService, never()).createOperation(any());
    }

    @Test
    void createOperation_CategoryNotFound() throws Exception {
        when(operationService.createOperation(any()))
                .thenThrow(new NotFoundException("Category not found"));

        mockMvc.perform(post("/api/v1/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].message").value("Category not found"));
    }

    @Test
    void updateOperation_Success() throws Exception {
        when(operationService.updateOperation(eq("test-public-id"), any(OperationUpdateRequest.class)))
                .thenReturn(operationResponse);

        mockMvc.perform(put("/api/v1/operations/test-public-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.publicId").value("test-public-id"));

        verify(operationService).updateOperation(eq("test-public-id"), any(OperationUpdateRequest.class));
    }

    @Test
    void updateOperation_NotFound() throws Exception {
        when(operationService.updateOperation(eq("non-existent-id"), any()))
                .thenThrow(new NotFoundException("Operation not found"));

        mockMvc.perform(put("/api/v1/operations/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_Success() throws Exception {
        PageRequest expectedPageRequest = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "publicId"));
        when(operationService.findAll(expectedPageRequest)).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/operations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(operationService).findAll(any(PageRequest.class));
    }

    @Test
    void findAll_WithCustomPagination() throws Exception {
        when(operationService.findAll(any(PageRequest.class))).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/operations")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sortBy", "name")
                        .param("direction", "ASC"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(operationService).findAll(any(PageRequest.class));
    }

    @Test
    void findByPublicId_Success() throws Exception {
        when(operationService.findByPublicId("test-public-id")).thenReturn(operationResponse);

        mockMvc.perform(get("/api/v1/operations/test-public-id"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.publicId").value("test-public-id"));

        verify(operationService).findByPublicId("test-public-id");
    }

    @Test
    void findByPublicId_NotFound() throws Exception {
        when(operationService.findByPublicId("non-existent-id"))
                .thenThrow(new NotFoundException("Operation not found"));

        mockMvc.perform(get("/api/v1/operations/non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOperation_Success() throws Exception {
        doNothing().when(operationService).deleteOperation("test-public-id");

        mockMvc.perform(delete("/api/v1/operations/test-public-id"))
                .andExpect(status().isNoContent());

        verify(operationService).deleteOperation("test-public-id");
    }

    @Test
    void deleteOperation_NotFound() throws Exception {
        doThrow(new NotFoundException("Operation not found"))
                .when(operationService).deleteOperation("non-existent-id");

        mockMvc.perform(delete("/api/v1/operations/non-existent-id"))
                .andExpect(status().isNotFound());
    }
}