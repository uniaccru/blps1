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
        Object existingApplicantId = externalTask.getVariable("applicantUserId");
        if (existingApplicantId != null) {
            externalTaskService.complete(externalTask, Map.of(
                    "applicantCamundaUserId", externalTask.getVariable("applicantCamundaUserId")
            ));
            return;
        }

        String starterUserId = String.valueOf(externalTask.getVariable("starterUserId"));
        User applicant = userRepository.findAll().stream()
                .filter(user -> CamundaIdentitySyncService.camundaUserId(user).equals(starterUserId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Camunda starter is not a known applicant: " + starterUserId));

        externalTaskService.complete(externalTask, Map.of(
                "applicantUserId", applicant.getId(),
                "applicantCamundaUserId", CamundaIdentitySyncService.camundaUserId(applicant)
        ));
    }
}