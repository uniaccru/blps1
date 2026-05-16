package com.example.hhflow.controller;

import com.example.hhflow.dto.request.CreateResumeRequest;
import com.example.hhflow.dto.response.ResumeDto;
import com.example.hhflow.dto.response.PageResponse;
import com.example.hhflow.mapper.ApiMapper;
import com.example.hhflow.security.SecurityUtils;
import com.example.hhflow.service.ResumeService;
import com.example.hhflow.validation.ValidationConstraints;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/v1/resumes")
@Validated
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final ApiMapper apiMapper;

    @GetMapping
    public PageResponse<ResumeDto> findAll(
            @RequestParam(defaultValue = ValidationConstraints.PAGE_DEFAULT_VALUE) @Min(value = ValidationConstraints.PAGE_MIN, message = "must be greater than or equal to {value}") int page,
            @RequestParam(defaultValue = ValidationConstraints.SIZE_DEFAULT_VALUE) @Min(value = ValidationConstraints.SIZE_MIN, message = "must be at least {value}") @Max(value = ValidationConstraints.SIZE_MAX, message = "must be at most {value}") int size
    ) {
        Long applicantUserId = SecurityUtils.requireApplicant().getUserId();
        return PageResponse.from(resumeService.findAllForApplicant(applicantUserId, PageRequest.of(page, size, Sort.by("id").ascending()))
                .map(apiMapper::toDto));
    }

    @GetMapping("/{id}")
    public ResumeDto getById(@PathVariable @Min(value = ValidationConstraints.ID_MIN, message = "must be a positive number") Long id) {
        Long applicantUserId = SecurityUtils.requireApplicant().getUserId();
        return apiMapper.toDto(resumeService.getByIdAndApplicant(id, applicantUserId));
    }

    @PostMapping
    public ResumeDto create(@Valid @RequestBody CreateResumeRequest request) {
        Long applicantUserId = SecurityUtils.requireApplicant().getUserId();
        return apiMapper.toDto(resumeService.create(request, applicantUserId));
    }
}
