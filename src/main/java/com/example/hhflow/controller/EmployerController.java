package com.example.hhflow.controller;

import com.example.hhflow.dto.response.EmployerDto;
import com.example.hhflow.dto.response.PageResponse;
import com.example.hhflow.mapper.ApiMapper;
import com.example.hhflow.model.Employer;
import com.example.hhflow.security.SecurityUtils;
import com.example.hhflow.service.EmployerService;
import com.example.hhflow.validation.ValidationConstraints;
import java.util.Collections;
import org.springframework.security.access.AccessDeniedException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/v1/employers")
@Validated
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;
    private final ApiMapper apiMapper;

    @GetMapping
    public PageResponse<EmployerDto> findAll(
            @RequestParam(defaultValue = ValidationConstraints.PAGE_DEFAULT_VALUE) @Min(value = ValidationConstraints.PAGE_MIN, message = "must be greater than or equal to {value}") int page,
            @RequestParam(defaultValue = ValidationConstraints.SIZE_DEFAULT_VALUE) @Min(value = ValidationConstraints.SIZE_MIN, message = "must be at least {value}") @Max(value = ValidationConstraints.SIZE_MAX, message = "must be at most {value}") int size
    ) {
        Long employerId = SecurityUtils.requireEmployer().getEmployerId();
        Employer employer = employerService.getById(employerId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Employer> single = new PageImpl<>(Collections.singletonList(employer), pageable, 1);
        return PageResponse.from(single.map(apiMapper::toDto));
    }

    @GetMapping("/{id}")
    public EmployerDto getById(@PathVariable @Min(value = ValidationConstraints.ID_MIN, message = "must be a positive number") Long id) {
        Long employerId = SecurityUtils.requireEmployer().getEmployerId();
        if (!employerId.equals(id)) {
            throw new AccessDeniedException("Access to this employer profile is denied");
        }
        return apiMapper.toDto(employerService.getById(id));
    }
}
