package com.example.hhflow.service;

import com.example.hhflow.exception.NotFoundException;
import com.example.hhflow.model.Applicant;
import com.example.hhflow.repository.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicantService {

    private final ApplicantRepository applicantRepository;

    public boolean existsById(Long applicantId) {
        return applicantRepository.existsById(applicantId);
    }

    public Applicant getById(Long id) {
        return applicantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Applicant not found: " + id));
    }
}
