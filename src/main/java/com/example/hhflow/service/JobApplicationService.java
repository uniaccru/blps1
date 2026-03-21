package com.example.hhflow.service;

import com.example.hhflow.exception.BusinessException;
import com.example.hhflow.exception.NotFoundException;
import com.example.hhflow.model.ApplicationStatus;
import com.example.hhflow.model.JobApplication;
import com.example.hhflow.model.Resume;
import com.example.hhflow.model.Vacancy;
import com.example.hhflow.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final Clock clock;

    @Transactional
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

    @Transactional(readOnly = true)
    public List<JobApplication> findAll() {
        return jobApplicationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public JobApplication getById(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application not found: " + id));
    }

    @Transactional
    public JobApplication markEmployerNotified(JobApplication application) {
        application.setEmployerNotifiedAt(OffsetDateTime.now(clock));
        return jobApplicationRepository.save(application);
    }
}
