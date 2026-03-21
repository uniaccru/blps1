package com.example.hhflow.service;

import com.example.hhflow.model.Applicant;
import com.example.hhflow.repository.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicantService {

    private final ApplicantRepository applicantRepository;

    @Transactional(readOnly = true)
    public boolean existsById(Long applicantId) {
        return applicantRepository.existsById(applicantId);
    }

    @Transactional
    public Applicant create(Long applicantId) {
        Applicant applicant = new Applicant();
        applicant.setId(applicantId);
        return applicantRepository.save(applicant);
    }
}
