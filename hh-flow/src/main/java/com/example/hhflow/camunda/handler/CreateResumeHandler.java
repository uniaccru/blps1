package com.example.hhflow.camunda.handler;

import com.example.hhflow.camunda.ExternalTaskVariables;
import com.example.hhflow.camunda.TopicExternalTaskHandler;
import com.example.hhflow.model.Resume;
import com.example.hhflow.service.subprocess.ResumeCreationSubprocessService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CreateResumeHandler implements TopicExternalTaskHandler {

    private final ResumeCreationSubprocessService resumeCreationSubprocessService;

    @Override
    public String getTopic() {
        return "createResume";
    }

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Long applicantUserId = ExternalTaskVariables.getLong(externalTask, "applicantUserId");
        String resumeFullName = ExternalTaskVariables.getString(externalTask, "resumeFullName");
        String resumeSummary = ExternalTaskVariables.getString(externalTask, "resumeSummary");

        Optional<Resume> resumeOptional = resumeCreationSubprocessService.createResume(
                applicantUserId,
                resumeFullName,
                resumeSummary
        );

        if (resumeOptional.isEmpty()) {
            externalTaskService.handleFailure(
                    externalTask,
                    "Resume creation subprocess returned failure.",
                    null,
                    0,
                    0
            );
            return;
        }

        externalTaskService.complete(externalTask, Map.of(
                "resumeId", resumeOptional.get().getId(),
                "resumeExists", true
        ));
    }
}
