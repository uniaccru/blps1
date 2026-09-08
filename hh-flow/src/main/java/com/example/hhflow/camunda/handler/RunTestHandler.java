package com.example.hhflow.camunda.handler;

import com.example.hhflow.camunda.ExternalTaskVariables;
import com.example.hhflow.camunda.TopicExternalTaskHandler;
import com.example.hhflow.service.subprocess.TestSubprocessService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RunTestHandler implements TopicExternalTaskHandler {

    private final TestSubprocessService testSubprocessService;

    @Override
    public String getTopic() {
        return "runTest";
    }

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Long applicantUserId = ExternalTaskVariables.getLong(externalTask, "applicantUserId");
        Long vacancyId = ExternalTaskVariables.getLong(externalTask, "vacancyId");
        boolean testPassed = testSubprocessService.passTest(applicantUserId, vacancyId);
        externalTaskService.complete(externalTask, Map.of("testPassed", testPassed));
    }
}
