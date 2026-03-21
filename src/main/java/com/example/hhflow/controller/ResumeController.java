package com.example.hhflow.controller;

import com.example.hhflow.dto.CreateResumeRequest;
import com.example.hhflow.dto.ResumeDto;
import com.example.hhflow.mapper.ApiMapper;
import com.example.hhflow.service.ResumeService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final ApiMapper apiMapper;

    @GetMapping
    public List<ResumeDto> findAll() {
        return resumeService.findAll().stream().map(apiMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResumeDto getById(@PathVariable Long id) {
        return apiMapper.toDto(resumeService.getById(id));
    }

    @PostMapping
    public ResumeDto create(@Valid @RequestBody CreateResumeRequest request) {
        return apiMapper.toDto(resumeService.create(request));
    }
}
