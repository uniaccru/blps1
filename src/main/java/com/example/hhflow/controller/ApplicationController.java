package com.example.hhflow.controller;

import com.example.hhflow.dto.request.SubmitApplicationRequest;
import com.example.hhflow.dto.response.ApplicationDto;
import com.example.hhflow.dto.response.SubmissionResponse;
import com.example.hhflow.dto.response.PageResponse;
import com.example.hhflow.mapper.ApiMapper;
import com.example.hhflow.security.SecurityUtils;
import com.example.hhflow.service.ApplicationProcessService;
import com.example.hhflow.service.JobApplicationService;
import com.example.hhflow.validation.ValidationConstraints;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/v1/applications")
@Validated
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationProcessService applicationProcessService;
    private final JobApplicationService jobApplicationService;
    private final ApiMapper apiMapper;

    @PostMapping("/submit")
    public SubmissionResponse submit(@Valid @RequestBody SubmitApplicationRequest request) {
        Long applicantUserId = SecurityUtils.requireApplicant().getUserId();
        return applicationProcessService.submitApplication(request, applicantUserId);
    }

    @GetMapping
    public PageResponse<ApplicationDto> findAll(
            @RequestParam(defaultValue = ValidationConstraints.PAGE_DEFAULT_VALUE) @Min(value = ValidationConstraints.PAGE_MIN, message = "must be greater than or equal to {value}") int page,
            @RequestParam(defaultValue = ValidationConstraints.SIZE_DEFAULT_VALUE) @Min(value = ValidationConstraints.SIZE_MIN, message = "must be at least {value}") @Max(value = ValidationConstraints.SIZE_MAX, message = "must be at most {value}") int size
    ) {
        Long employerUserId = SecurityUtils.requireEmployer().getUserId();
        return PageResponse.from(jobApplicationService.findForEmployer(employerUserId, PageRequest.of(page, size, Sort.by("id").ascending()))
                .map(apiMapper::toDto));
    }

    @GetMapping("/{id}")
    public ApplicationDto getById(@PathVariable @Min(value = ValidationConstraints.ID_MIN, message = "must be a positive number") Long id) {
        Long employerUserId = SecurityUtils.requireEmployer().getUserId();
        return apiMapper.toDto(jobApplicationService.getForEmployer(id, employerUserId));
    }
}
