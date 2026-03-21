package com.example.hhflow.controller;

import com.example.hhflow.dto.CreateVacancyRequest;
import com.example.hhflow.dto.VacancyDto;
import com.example.hhflow.dto.VacancyStatusUpdateRequest;
import com.example.hhflow.mapper.ApiMapper;
import com.example.hhflow.service.VacancyService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;
    private final ApiMapper apiMapper;

    @GetMapping
    public List<VacancyDto> findAll() {
        return vacancyService.findAll().stream()
                .map(apiMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public VacancyDto getById(@PathVariable Long id) {
        return apiMapper.toDto(vacancyService.getById(id));
    }

    @PostMapping
    public VacancyDto create(@Valid @RequestBody CreateVacancyRequest request) {
        return apiMapper.toDto(vacancyService.create(request));
    }

    @PatchMapping("/{id}/status")
    public VacancyDto updateStatus(@PathVariable Long id, @Valid @RequestBody VacancyStatusUpdateRequest request) {
        return apiMapper.toDto(vacancyService.updateStatus(id, request.getStatus()));
    }
}
