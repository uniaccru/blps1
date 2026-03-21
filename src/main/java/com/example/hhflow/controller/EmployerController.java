package com.example.hhflow.controller;

import com.example.hhflow.dto.request.CreateEmployerRequest;
import com.example.hhflow.dto.response.EmployerDto;
import com.example.hhflow.mapper.ApiMapper;
import com.example.hhflow.service.EmployerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/employers")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;
    private final ApiMapper apiMapper;

    @GetMapping
    public List<EmployerDto> findAll() {
        return employerService.findAll().stream()
                .map(apiMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EmployerDto getById(@PathVariable Long id) {
        return apiMapper.toDto(employerService.getById(id));
    }

    @PostMapping
    public EmployerDto create(@Valid @RequestBody CreateEmployerRequest request) {
        return apiMapper.toDto(employerService.create(request));
    }
}
