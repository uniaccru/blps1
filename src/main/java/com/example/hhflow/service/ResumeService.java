package com.example.hhflow.service;

import com.example.hhflow.dto.request.CreateResumeRequest;
import com.example.hhflow.exception.BusinessException;
import com.example.hhflow.exception.NotFoundException;
import com.example.hhflow.model.Applicant;
import com.example.hhflow.model.Resume;
import com.example.hhflow.repository.ApplicantRepository;
import com.example.hhflow.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ApplicantRepository applicantRepository;
    private final Clock clock;

    public Page<Resume> findAllForApplicant(Long applicantId, Pageable pageable) {
        return resumeRepository.findByApplicant_Id(applicantId, pageable);
    }

    public Resume getByIdAndApplicant(Long id, Long applicantId) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resume not found: " + id));
        if (!resume.getApplicant().getId().equals(applicantId)) {
            throw new BusinessException("Resume does not belong to candidate: " + applicantId);
        }
        return resume;
    }

    public Optional<Resume> findByCandidateId(Long candidateId) {
        return resumeRepository.findByApplicantId(candidateId);
    }

    @Transactional
    public Resume createForApplicant(Long applicantId, String fullName, String summary) {
        if (resumeRepository.findByApplicantId(applicantId).isPresent()) {
            throw new BusinessException("Resume already exists for candidate: " + applicantId);
        }
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new NotFoundException("Applicant not found: " + applicantId));

        Resume resume = new Resume();
        resume.setApplicant(applicant);
        resume.setFullName(fullName);
        resume.setSummary(summary);
        resume.setCreatedAt(OffsetDateTime.now(clock));
        return resumeRepository.save(resume);
    }

    @Transactional
    public Resume create(CreateResumeRequest request, Long applicantId) {
        return createForApplicant(applicantId, request.getFullName(), request.getSummary());
    }
}
