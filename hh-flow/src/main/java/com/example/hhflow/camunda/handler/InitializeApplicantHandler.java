package com.example.hhflow.camunda.handler;

import com.example.hhflow.camunda.CamundaIdentitySyncService;
import com.example.hhflow.camunda.TopicExternalTaskHandler;
import com.example.hhflow.model.User;
import com.example.hhflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class InitializeApplicantHandler implements TopicExternalTaskHandler {

    private final UserRepository userRepository;

    @Override
    public String getTopic() {
        return "initializeApplicant";
    }

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        try {
            Object existingApplicantId = externalTask.getVariable("applicantUserId");
            if (existingApplicantId != null) {
                Object camundaUserId = externalTask.getVariable("applicantCamundaUserId");
                externalTaskService.complete(externalTask, Map.of(
                        "applicantCamundaUserId", camundaUserId != null ? camundaUserId : ""
                ));
                return;
            }

            Object starterVar = externalTask.getVariable("starterUserId");
            if (starterVar == null) {
                externalTaskService.handleFailure(
                        externalTask,
                        "Missing starterUserId",
                        "Process was started without initiator. Log in as synced applicant (e.g. ivanpetrovexamplecom) in Tasklist.",
                        0,
                        0L
                );
                return;
            }

            String starterUserId = String.valueOf(starterVar);
            User applicant = userRepository.findAll().stream()
                    .filter(user -> CamundaIdentitySyncService.camundaUserId(user).equals(starterUserId))
                    .findFirst()
                    .orElse(null);

            if (applicant == null) {
                externalTaskService.handleFailure(
                        externalTask,
                        "Unknown Camunda starter: " + starterUserId,
                        "Start the process as a synced APPLICANT user (seed: ivan.petrov@example.com → ivanpetrovexamplecom), not demo.",
                        0,
                        0L
                );
                return;
            }

            externalTaskService.complete(externalTask, Map.of(
                    "applicantUserId", applicant.getId(),
                    "applicantCamundaUserId", CamundaIdentitySyncService.camundaUserId(applicant)
            ));
        } catch (RuntimeException exception) {
            externalTaskService.handleFailure(
                    externalTask,
                    exception.getMessage(),
                    exception.toString(),
                    0,
                    0L
            );
        }
    }
}