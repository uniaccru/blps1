package com.example.hhflow.camunda.handler;

import com.example.hhflow.camunda.ExternalTaskVariables;
import com.example.hhflow.camunda.TopicExternalTaskHandler;
import com.example.hhflow.model.Resume;
import com.example.hhflow.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CheckResumeHandler implements TopicExternalTaskHandler {

    private final ResumeService resumeService;

    @Override
    public String getTopic() {
        return "checkResume";
    }

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Long applicantUserId = ExternalTaskVariables.getLong(externalTask, "applicantUserId");
        Optional<Resume> resumeOptional = resumeService.findByCandidateId(applicantUserId);

        Map<String, Object> variables = new HashMap<>();
        if (resumeOptional.isPresent()) {
            variables.put("resumeExists", true);
            variables.put("resumeId", resumeOptional.get().getId());
        } else {
            variables.put("resumeExists", false);
        }
        externalTaskService.complete(externalTask, variables);
    }
}
