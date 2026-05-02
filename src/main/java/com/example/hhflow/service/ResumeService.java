package com.example.hhflow.service;

import com.example.hhflow.dto.request.CreateResumeRequest;
import com.example.hhflow.exception.BusinessException;
import com.example.hhflow.exception.NotFoundException;
import com.example.hhflow.model.Resume;
import com.example.hhflow.model.Role;
import com.example.hhflow.model.User;
import com.example.hhflow.repository.ResumeRepository;
import com.example.hhflow.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final Clock clock;

    public Page<Resume> findAllForApplicant(Long applicantUserId, Pageable pageable) {
        return resumeRepository.findByOwner_Id(applicantUserId, pageable);
    }

    public Resume getByIdAndApplicant(Long id, Long applicantUserId) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resume not found: " + id));
        if (!resume.getOwner().getId().equals(applicantUserId)) {
            throw new BusinessException("Resume does not belong to candidate: " + applicantUserId);
        }
        return resume;
    }

    public Optional<Resume> findByCandidateId(Long candidateUserId) {
        return resumeRepository.findByOwner_Id(candidateUserId);
    }

    @Transactional
    public Resume createForApplicant(Long applicantUserId, String fullName, String summary) {
        if (resumeRepository.findByOwner_Id(applicantUserId).isPresent()) {
            throw new BusinessException("Resume already exists for candidate: " + applicantUserId);
        }
        User owner = userRepository.findById(applicantUserId)
                .filter(u -> u.getRole() == Role.APPLICANT)
                .orElseThrow(() -> new NotFoundException("Applicant not found: " + applicantUserId));

        Resume resume = new Resume();
        resume.setOwner(owner);
        resume.setFullName(fullName);
        resume.setSummary(summary);
        resume.setCreatedAt(OffsetDateTime.now(clock));
        return resumeRepository.save(resume);
    }

    @Transactional
    public Resume create(CreateResumeRequest request, Long applicantUserId) {
        return createForApplicant(applicantUserId, request.getFullName(), request.getSummary());
    }
}
