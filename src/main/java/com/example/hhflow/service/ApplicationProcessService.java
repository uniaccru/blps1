package com.example.hhflow.service;

import com.example.hhflow.dto.request.SubmitApplicationRequest;
import com.example.hhflow.dto.response.ApplicationDto;
import com.example.hhflow.dto.response.SubmissionResponse;
import com.example.hhflow.mapper.ApiMapper;
import com.example.hhflow.model.JobApplication;
import com.example.hhflow.model.Resume;
import com.example.hhflow.model.Role;
import com.example.hhflow.model.SubmissionOutcome;
import com.example.hhflow.model.Vacancy;
import com.example.hhflow.model.VacancyStatus;
import com.example.hhflow.repository.UserRepository;
import com.example.hhflow.service.subprocess.ResumeCreationSubprocessService;
import com.example.hhflow.service.subprocess.TestSubprocessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationProcessService {

    private final VacancyService vacancyService;
    private final ResumeService resumeService;
    private final JobApplicationService jobApplicationService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final ResumeCreationSubprocessService resumeCreationSubprocessService;
    private final TestSubprocessService testSubprocessService;
    private final ApiMapper apiMapper;
    private final PlatformTransactionManager transactionManager;

    public SubmissionResponse submitApplication(SubmitApplicationRequest request, Long applicantUserId) {
        if (userRepository.findById(applicantUserId).filter(u -> u.getRole() == Role.APPLICANT).isEmpty()) {
            return new SubmissionResponse(
                    SubmissionOutcome.REGISTRATION_FAILED,
                    "Applicant account not found or invalid role.",
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

        Optional<Resume> resumeOptional = resolveResume(request, applicantUserId);
        if (resumeOptional.isEmpty()) {
            return new SubmissionResponse(
                SubmissionOutcome.RESUME_CREATION_FAILED,
                "Resume creation subprocess returned failure.",
                null
            );
        }
        Resume resume = resumeOptional.get();

        if (vacancy.isRequiresTest()) {
            boolean testPassed = testSubprocessService.passTest(applicantUserId, request.getVacancyId());
            if (!testPassed) {
                return new SubmissionResponse(
                        SubmissionOutcome.AUTO_REJECTED,
                        "Test is not passed. Automatic rejection.",
                        null
                );
            }
        }

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("submitApplicationTx");
        def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus txStatus = transactionManager.getTransaction(def);
        JobApplication application;
        try {
            JobApplication app = jobApplicationService.createCompleted(
                    applicantUserId,
                    vacancy,
                    resume
            );
            notificationService.notifyEmployer(app);
            application = jobApplicationService.markEmployerNotified(app);
            transactionManager.commit(txStatus);
        } catch (RuntimeException | Error ex) {
            transactionManager.rollback(txStatus);
            throw ex;
        }

        ApplicationDto applicationDto = apiMapper.toDto(application);

        return new SubmissionResponse(
            SubmissionOutcome.COMPLETED,
            "Application successfully completed.",
            applicationDto
        );

    }

    private Optional<Resume> resolveResume(SubmitApplicationRequest request, Long applicantUserId) {
        if (request.getResumeId() != null) {
            return Optional.of(resumeService.getByIdAndApplicant(request.getResumeId(), applicantUserId));
        }

        return resumeService.findByCandidateId(applicantUserId)
                .map(Optional::of)
                .orElseGet(() -> createResumeViaSubprocess(request, applicantUserId));
    }

    private Optional<Resume> createResumeViaSubprocess(SubmitApplicationRequest request, Long applicantUserId) {
        return resumeCreationSubprocessService.createResume(
                applicantUserId,
                request.getResumeFullName(),
                request.getResumeSummary()
        );
    }
}
