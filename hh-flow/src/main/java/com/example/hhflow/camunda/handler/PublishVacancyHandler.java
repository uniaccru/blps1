package com.example.hhflow.camunda.handler;

import com.example.hhflow.camunda.ExternalTaskVariables;
import com.example.hhflow.camunda.TopicExternalTaskHandler;
import com.example.hhflow.dto.request.CreateVacancyRequest;
import com.example.hhflow.model.Vacancy;
import com.example.hhflow.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PublishVacancyHandler implements TopicExternalTaskHandler {

    private final VacancyService vacancyService;

    @Override
    public String getTopic() {
        return "publishVacancy";
    }

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Long employerUserId = ExternalTaskVariables.getLong(externalTask, "employerUserId");
        String title = ExternalTaskVariables.getString(externalTask, "title");
        String description = ExternalTaskVariables.getString(externalTask, "description");
        Boolean requiresTest = ExternalTaskVariables.getBoolean(externalTask, "requiresTest");

        CreateVacancyRequest request = new CreateVacancyRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setRequiresTest(requiresTest);

        Vacancy vacancy = vacancyService.create(request, employerUserId);
        externalTaskService.complete(externalTask, Map.of("vacancyId", vacancy.getId()));
    }
}
