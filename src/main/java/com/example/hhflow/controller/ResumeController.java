package com.example.hhflow.controller;

import com.example.hhflow.dto.request.CreateResumeRequest;
import com.example.hhflow.dto.response.ResumeDto;
import com.example.hhflow.dto.response.PageResponse;
import com.example.hhflow.mapper.ApiMapper;
import com.example.hhflow.service.ResumeService;
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
@RequestMapping("/api/v1/resumes")
@Validated
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final ApiMapper apiMapper;

    @GetMapping
    public PageResponse<ResumeDto> findAll(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "must be greater than or equal to 0") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "must be at least 1") @Max(value = 100, message = "must be at most 100") int size
    ) {
        return PageResponse.from(resumeService.findAll(PageRequest.of(page, size, Sort.by("id").ascending()))
                .map(apiMapper::toDto));
    }

    @GetMapping("/{id}")
    public ResumeDto getById(@PathVariable @Min(value = 1, message = "must be a positive number") Long id) {
        return apiMapper.toDto(resumeService.getById(id));
    }

    @PostMapping
    public ResumeDto create(@Valid @RequestBody CreateResumeRequest request) {
        return apiMapper.toDto(resumeService.create(request));
    }
}
