package com.example.hhflow.controller;

import com.example.hhflow.dto.request.CreateVacancyRequest;
import com.example.hhflow.dto.request.VacancyStatusUpdateRequest;
import com.example.hhflow.dto.response.VacancyDto;
import com.example.hhflow.dto.response.PageResponse;
import com.example.hhflow.mapper.ApiMapper;
import com.example.hhflow.service.VacancyService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.Valid;
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
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "must be greater than or equal to 0") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "must be at least 1") @Max(value = 100, message = "must be at most 100") int size
    ) {
        return PageResponse.from(vacancyService.findAll(PageRequest.of(page, size, Sort.by("id").ascending()))
                .map(apiMapper::toDto));
    }

    @GetMapping("/{id}")
    public VacancyDto getById(@PathVariable @Min(value = 1, message = "must be a positive number") Long id) {
        return apiMapper.toDto(vacancyService.getById(id));
    }

    @PostMapping
    public VacancyDto create(@Valid @RequestBody CreateVacancyRequest request) {
        return apiMapper.toDto(vacancyService.create(request));
    }

    @PatchMapping("/{id}/status")
    public VacancyDto updateStatus(@PathVariable @Min(value = 1, message = "must be a positive number") Long id, @Valid @RequestBody VacancyStatusUpdateRequest request) {
        return apiMapper.toDto(vacancyService.updateStatus(id, request.getStatus()));
    }
}
