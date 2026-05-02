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
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final Clock clock;

    public JobApplication createCompleted(Long candidateUserId, Vacancy vacancy, Resume resume) {
        if (!resume.getOwner().getId().equals(candidateUserId)) {
            throw new BusinessException("Resume does not belong to candidate: " + candidateUserId);
        }

        JobApplication application = new JobApplication();
        application.setApplicant(resume.getOwner());
        application.setVacancy(vacancy);
        application.setResume(resume);
        application.setStatus(ApplicationStatus.COMPLETED);
        application.setCreatedAt(OffsetDateTime.now(clock));
        return jobApplicationRepository.save(application);
    }

    public Page<JobApplication> findAll(Pageable pageable) {
        return jobApplicationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<JobApplication> findForEmployer(Long employerUserId, Pageable pageable) {
        return jobApplicationRepository.findByVacancy_Employer_Id(employerUserId, pageable);
    }

    public JobApplication getById(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application not found: " + id));
    }

    @Transactional(readOnly = true)
    public JobApplication getForEmployer(Long applicationId, Long employerUserId) {
        JobApplication application = getById(applicationId);
        if (!application.getVacancy().getEmployer().getId().equals(employerUserId)) {
            throw new BusinessException("Application does not belong to this employer's vacancies");
        }
        return application;
    }

    public JobApplication markEmployerNotified(JobApplication application) {
        application.setEmployerNotifiedAt(OffsetDateTime.now(clock));
        return jobApplicationRepository.save(application);
    }
}
