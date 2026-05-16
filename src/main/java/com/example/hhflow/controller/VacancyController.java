package com.example.hhflow.controller;

import com.example.hhflow.dto.request.CreateVacancyRequest;
import com.example.hhflow.dto.request.VacancyStatusUpdateRequest;
import com.example.hhflow.dto.response.VacancyDto;
import com.example.hhflow.dto.response.PageResponse;
import com.example.hhflow.mapper.ApiMapper;
import com.example.hhflow.security.SecurityUtils;
import com.example.hhflow.service.VacancyService;
import com.example.hhflow.validation.ValidationConstraints;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/v1/vacancies")
@Validated
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;
    private final ApiMapper apiMapper;

    @GetMapping
    public PageResponse<VacancyDto> findAll(
            @RequestParam(defaultValue = ValidationConstraints.PAGE_DEFAULT_VALUE) @Min(value = ValidationConstraints.PAGE_MIN, message = "must be greater than or equal to {value}") int page,
            @RequestParam(defaultValue = ValidationConstraints.SIZE_DEFAULT_VALUE) @Min(value = ValidationConstraints.SIZE_MIN, message = "must be at least {value}") @Max(value = ValidationConstraints.SIZE_MAX, message = "must be at most {value}") int size
    ) {
        return PageResponse.from(vacancyService.findAll(PageRequest.of(page, size, Sort.by("id").ascending()))
                .map(apiMapper::toDto));
    }

    @GetMapping("/{id}")
        public VacancyDto getById(@PathVariable @Min(value = ValidationConstraints.ID_MIN, message = "must be a positive number") Long id) {
        return apiMapper.toDto(vacancyService.getById(id));
    }

    @PostMapping
    public VacancyDto create(@Valid @RequestBody CreateVacancyRequest request) {
        Long employerUserId = SecurityUtils.requireEmployer().getUserId();
        return apiMapper.toDto(vacancyService.create(request, employerUserId));
    }

    @PatchMapping("/{id}/status")
    public VacancyDto updateStatus(@PathVariable @Min(value = ValidationConstraints.ID_MIN, message = "must be a positive number") Long id, @Valid @RequestBody VacancyStatusUpdateRequest request) {
        Long employerUserId = SecurityUtils.requireEmployer().getUserId();
        return apiMapper.toDto(vacancyService.updateStatus(id, request.getStatus(), employerUserId));
    }
}
