package com.example.hhflow.service;

import com.example.hhflow.dto.request.SubmitApplicationRequest;
import com.example.hhflow.dto.response.ApplicationDto;
import com.example.hhflow.dto.response.SubmissionResponse;
import com.example.hhflow.mapper.ApiMapper;
import com.example.hhflow.model.JobApplication;
import com.example.hhflow.model.Resume;
import com.example.hhflow.model.SubmissionOutcome;
import com.example.hhflow.model.Vacancy;
import com.example.hhflow.model.VacancyStatus;
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
    private final ApplicantService applicantService;
    private final ResumeCreationSubprocessService resumeCreationSubprocessService;
    private final TestSubprocessService testSubprocessService;
    private final ApiMapper apiMapper;

    @Transactional
    public SubmissionResponse submitApplication(SubmitApplicationRequest request, Long applicantId) {
        if (!applicantService.existsById(applicantId)) {
            return new SubmissionResponse(
                    SubmissionOutcome.REGISTRATION_FAILED,
                    "Applicant profile is missing for this account.",
                    null
            );
        }

        Vacancy vacancy = vacancyService.getById(request.getVacancyId());
        if (vacancy.getStatus() == VacancyStatus.ARCHIVED) {
            return new SubmissionResponse(
                    SubmissionOutcome.VACANCY_ARCHIVED,
                    "Vacancy is archived. Submission declined.",
                    null
            );
        }

        Optional<Resume> resumeOptional = resolveResume(request, applicantId);
        if (resumeOptional.isEmpty()) {
            return new SubmissionResponse(
                SubmissionOutcome.RESUME_CREATION_FAILED,
                "Resume creation subprocess returned failure.",
                null
            );
        }
        Resume resume = resumeOptional.get();

        if (vacancy.isRequiresTest()) {
            boolean testPassed = testSubprocessService.passTest(applicantId, request.getVacancyId());
            if (!testPassed) {
                return new SubmissionResponse(
                        SubmissionOutcome.AUTO_REJECTED,
                        "Test is not passed. Automatic rejection.",
                        null
                );
            }
        }

        JobApplication application = jobApplicationService.createCompleted(
                applicantId,
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

    private Optional<Resume> resolveResume(SubmitApplicationRequest request, Long applicantId) {
        if (request.getResumeId() != null) {
            return Optional.of(resumeService.getByIdAndApplicant(request.getResumeId(), applicantId));
        }

        return resumeService.findByCandidateId(applicantId)
                .map(Optional::of)
                .orElseGet(() -> createResumeViaSubprocess(request, applicantId));
    }

    private Optional<Resume> createResumeViaSubprocess(SubmitApplicationRequest request, Long applicantId) {
        return resumeCreationSubprocessService.createResume(
                applicantId,
                request.getResumeFullName(),
                request.getResumeSummary()
        );
    }
}
