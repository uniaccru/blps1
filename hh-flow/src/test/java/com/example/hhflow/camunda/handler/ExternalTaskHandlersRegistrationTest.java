package com.example.hhflow.camunda.handler;

import com.example.hhflow.camunda.TopicExternalTaskHandler;
import com.example.hhflow.service.ApplicationProcessService;
import com.example.hhflow.service.NotificationService;
import com.example.hhflow.service.ResumeService;
import com.example.hhflow.service.VacancyService;
import com.example.hhflow.service.subprocess.ResumeCreationSubprocessService;
import com.example.hhflow.service.subprocess.TestSubprocessService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ExternalTaskHandlersRegistrationTest {

    @Test
    void allHandlersExposeExpectedTopics() {
        List<TopicExternalTaskHandler> handlers = List.of(
                new CheckTokenHandler(),
                new CheckRoleHandler(),
                new CheckVacancyStatusHandler(mock(VacancyService.class)),
                new CheckResumeHandler(mock(ResumeService.class)),
                new CreateResumeHandler(mock(ResumeCreationSubprocessService.class)),
                new CheckTestRequirementsHandler(mock(VacancyService.class)),
                new RunTestHandler(mock(TestSubprocessService.class)),
                new RegisterApplicationTxHandler(mock(ApplicationProcessService.class)),
                new NotifyCandidateErrorHandler(mock(NotificationService.class)),
                new PublishVacancyHandler(mock(VacancyService.class))
        );

        Set<String> topics = handlers.stream()
                .map(TopicExternalTaskHandler::getTopic)
                .collect(Collectors.toSet());

        assertThat(topics).containsExactlyInAnyOrder(
                "checkToken",
                "checkRole",
                "checkVacancyStatus",
                "checkResume",
                "createResume",
                "checkTestRequirements",
                "runTest",
                "registerApplicationTx",
                "notifyCandidateError",
                "publishVacancy"
        );
    }
}
