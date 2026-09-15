package com.example.hhflow.camunda.handler;

import com.example.hhflow.camunda.ExternalTaskVariables;
import com.example.hhflow.camunda.TopicExternalTaskHandler;
import com.example.hhflow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotifyCandidateErrorHandler implements TopicExternalTaskHandler {

    private final NotificationService notificationService;

    @Override
    public String getTopic() {
        return "notifyCandidateError";
    }

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Long applicantUserId = ExternalTaskVariables.getLong(externalTask, "applicantUserId");
        String errorMessage = ExternalTaskVariables.getStringOrDefault(externalTask, "errorMessage", "Unknown error");
        notificationService.notifyCandidateError(applicantUserId, errorMessage);
        externalTaskService.complete(externalTask);
    }
}
