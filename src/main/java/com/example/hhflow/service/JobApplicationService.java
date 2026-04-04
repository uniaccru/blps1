package com.example.hhflow.service;

import com.example.hhflow.exception.BusinessException;
import com.example.hhflow.exception.NotFoundException;
import com.example.hhflow.model.ApplicationStatus;
import com.example.hhflow.model.JobApplication;
import com.example.hhflow.model.Resume;
import com.example.hhflow.model.Vacancy;
import com.example.hhflow.repository.JobApplicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final Clock clock;

    public JobApplication createCompleted(Long candidateId, Vacancy vacancy, Resume resume) {
        if (!resume.getApplicant().getId().equals(candidateId)) {
            throw new BusinessException("Resume does not belong to candidate: " + candidateId);
        }

        JobApplication application = new JobApplication();
        application.setApplicant(resume.getApplicant());
        application.setVacancy(vacancy);
        application.setResume(resume);
        application.setStatus(ApplicationStatus.COMPLETED);
        application.setCreatedAt(OffsetDateTime.now(clock));
        return jobApplicationRepository.save(application);
    }

    public Page<JobApplication> findAll(Pageable pageable) {
        return jobApplicationRepository.findAll(pageable);
    }

    public JobApplication getById(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application not found: " + id));
    }

    public JobApplication markEmployerNotified(JobApplication application) {
        application.setEmployerNotifiedAt(OffsetDateTime.now(clock));
        return jobApplicationRepository.save(application);
    }
}
