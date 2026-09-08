package com.example.hhflow.camunda.handler;

import com.example.hhflow.camunda.ExternalTaskVariables;
import com.example.hhflow.camunda.TopicExternalTaskHandler;
import com.example.hhflow.service.ApplicationProcessService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RegisterApplicationTxHandler implements TopicExternalTaskHandler {

    private static final String APPLICATION_TX_FAILED = "APPLICATION_TX_FAILED";

    private final ApplicationProcessService applicationProcessService;

    @Override
    public String getTopic() {
        return "registerApplicationTx";
    }

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Long applicantUserId = ExternalTaskVariables.getLong(externalTask, "applicantUserId");
        Long vacancyId = ExternalTaskVariables.getLong(externalTask, "vacancyId");
        Long resumeId = ExternalTaskVariables.getLong(externalTask, "resumeId");

        try {
            Long applicationId = applicationProcessService.registerApplicationTx(
                    applicantUserId,
                    vacancyId,
                    resumeId
            );
            externalTaskService.complete(externalTask, Map.of("applicationId", applicationId));
        } catch (Exception ex) {
            externalTaskService.handleBpmnError(externalTask, APPLICATION_TX_FAILED, ex.getMessage());
        }
    }
}
