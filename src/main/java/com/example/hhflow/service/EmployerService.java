package com.example.hhflow.service;

import com.example.hhflow.exception.NotFoundException;
import com.example.hhflow.model.Employer;
import com.example.hhflow.repository.EmployerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final EmployerRepository employerRepository;

    public Page<Employer> findAll(Pageable pageable) {
        return employerRepository.findAll(pageable);
    }

    public Employer getById(Long id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employer not found: " + id));
    }
}
