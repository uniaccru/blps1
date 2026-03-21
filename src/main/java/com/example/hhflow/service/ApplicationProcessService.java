package com.example.hhflow.service;

import com.example.hhflow.domain.JobApplication;
import com.example.hhflow.domain.Resume;
import com.example.hhflow.domain.SubmissionOutcome;
import com.example.hhflow.domain.Vacancy;
import com.example.hhflow.domain.VacancyStatus;
import com.example.hhflow.dto.request.SubmitApplicationRequest;
import com.example.hhflow.dto.response.ApplicationDto;
import com.example.hhflow.dto.response.SubmissionResponse;
import com.example.hhflow.exception.BusinessException;
import com.example.hhflow.mapper.ApiMapper;
import com.example.hhflow.service.subprocess.ResumeCreationSubprocessService;
import com.example.hhflow.service.subprocess.TestSubprocessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationProcessService {

    private final VacancyService vacancyService;
    private final ResumeService resumeService;
    private final JobApplicationService jobApplicationService;
    private final NotificationService notificationService;
    private final ResumeCreationSubprocessService resumeCreationSubprocessService;
    private final TestSubprocessService testSubprocessService;
    private final ApiMapper apiMapper;

    @Transactional
    public SubmissionResponse submitApplication(SubmitApplicationRequest request) {
        Vacancy vacancy = vacancyService.getById(request.getVacancyId());
        if (vacancy.getStatus() == VacancyStatus.ARCHIVED) {
            return new SubmissionResponse(
                    SubmissionOutcome.VACANCY_ARCHIVED,
                    "Vacancy is archived. Submission declined.",
                    null
            );
        }

        Optional<Resume> resumeOptional = resolveResume(request);
        if (resumeOptional.isEmpty()) {
            return new SubmissionResponse(
                SubmissionOutcome.RESUME_CREATION_FAILED,
                "Resume creation subprocess returned failure.",
                null
            );
        }
        Resume resume = resumeOptional.get();

        if (vacancy.isRequiresTest()) {
            boolean testPassed = testSubprocessService.passTest(
                    request.getCandidateId(),
                    request.getVacancyId(),
                    request.getSimulateTestPassed()
            );
            if (!testPassed) {
                return new SubmissionResponse(
                        SubmissionOutcome.AUTO_REJECTED,
                        "Test is not passed. Automatic rejection.",
                        null
                );
            }
        }

        JobApplication application = jobApplicationService.createCompleted(
            request.getCandidateId(),
                vacancy,
                resume
        );
        notificationService.notifyEmployer(application);
        application = jobApplicationService.markEmployerNotified(application);
        ApplicationDto applicationDto = apiMapper.toDto(application);

        return new SubmissionResponse(
                SubmissionOutcome.COMPLETED,
                "Application successfully completed.",
                applicationDto
        );
        
    }

    private Optional<Resume> resolveResume(SubmitApplicationRequest request) {
        if (request.getResumeId() != null) {
            Resume resume = resumeService.getById(request.getResumeId());
            if (!resume.getCandidateId().equals(request.getCandidateId())) {
                throw new BusinessException("Resume does not belong to candidate: " + request.getCandidateId());
            }
            return Optional.of(resume);
        }

        return resumeService.findByCandidateId(request.getCandidateId())
                .map(Optional::of)
                .orElseGet(() -> createResumeViaSubprocess(request));
    }

    private Optional<Resume> createResumeViaSubprocess(SubmitApplicationRequest request) {
        return resumeCreationSubprocessService.createResume(
                request.getCandidateId(),
                request.getResumeFullName(),
                request.getResumeSummary(),
                request.getSimulateResumeCreationSuccess()
        );
    }
}
