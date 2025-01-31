package com.example.anthropic.claude.controller;

import com.example.anthropic.claude.dto.OperationCreateRequest;
import com.example.anthropic.claude.dto.OperationResponse;
import com.example.anthropic.claude.dto.OperationUpdateRequest;
import com.example.anthropic.claude.dto.PageResponse;
import com.example.anthropic.claude.service.OperationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/operations")
@RequiredArgsConstructor
public class OperationController {

    private final OperationService operationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OperationResponse createOperation(@Valid @RequestBody OperationCreateRequest request) {
        return operationService.createOperation(request);
    }

    @PutMapping("/{publicId}")
    public OperationResponse updateOperation(
            @PathVariable String publicId,
            @RequestBody OperationUpdateRequest request) {
        return operationService.updateOperation(publicId, request);
    }

    @GetMapping
    public PageResponse<OperationResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "publicId") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        final var pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortBy)
        );

        return operationService.findAll(pageRequest);
    }

    @GetMapping("/{publicId}")
    public OperationResponse findByPublicId(@PathVariable String publicId) {
        return operationService.findByPublicId(publicId);
    }

    @DeleteMapping("/{publicId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOperation(@PathVariable String publicId) {
        operationService.deleteOperation(publicId);
    }
}
