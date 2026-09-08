package com.example.hhflow.camunda.handler;

import com.example.hhflow.camunda.ExternalTaskVariables;
import com.example.hhflow.camunda.TopicExternalTaskHandler;
import com.example.hhflow.model.Vacancy;
import com.example.hhflow.model.VacancyStatus;
import com.example.hhflow.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CheckVacancyStatusHandler implements TopicExternalTaskHandler {

    private final VacancyService vacancyService;

    @Override
    public String getTopic() {
        return "checkVacancyStatus";
    }

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Long vacancyId = ExternalTaskVariables.getLong(externalTask, "vacancyId");
        Vacancy vacancy = vacancyService.getById(vacancyId);
        boolean vacancyActive = vacancy.getStatus() != VacancyStatus.ARCHIVED;
        externalTaskService.complete(externalTask, Map.of("vacancyActive", vacancyActive));
    }
}
