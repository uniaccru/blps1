package com.example.hhflow.service;

import com.example.hhflow.dto.request.CreateEmployerRequest;
import com.example.hhflow.exception.BusinessException;
import com.example.hhflow.exception.NotFoundException;
import com.example.hhflow.model.Employer;
import com.example.hhflow.repository.EmployerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final EmployerRepository employerRepository;

    @Transactional(readOnly = true)
    public List<Employer> findAll() {
        return employerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Employer getById(Long id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employer not found: " + id));
    }

    @Transactional
    public Employer create(CreateEmployerRequest request) {
        if (employerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Employer already exists with email: " + request.getEmail());
        }

        Employer employer = new Employer();
        employer.setEmail(request.getEmail());
        return employerRepository.save(employer);
    }
}
