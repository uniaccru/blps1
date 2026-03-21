package com.example.hhflow.controller;

import com.example.hhflow.dto.ApplicationDto;
import com.example.hhflow.dto.SubmissionResponse;
import com.example.hhflow.dto.SubmitApplicationRequest;
import com.example.hhflow.mapper.ApiMapper;
import com.example.hhflow.service.ApplicationProcessService;
import com.example.hhflow.service.JobApplicationService;
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
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationProcessService applicationProcessService;
    private final JobApplicationService jobApplicationService;
    private final ApiMapper apiMapper;

    @PostMapping("/submit")
    public SubmissionResponse submit(@Valid @RequestBody SubmitApplicationRequest request) {
        return applicationProcessService.submitApplication(request);
    }

    @GetMapping
    public List<ApplicationDto> findAll() {
        return jobApplicationService.findAll().stream().map(apiMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ApplicationDto getById(@PathVariable Long id) {
        return apiMapper.toDto(jobApplicationService.getById(id));
    }
}
