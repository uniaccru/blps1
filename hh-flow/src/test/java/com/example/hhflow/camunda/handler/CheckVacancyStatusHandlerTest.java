package com.example.hhflow.camunda.handler;

import com.example.hhflow.model.Vacancy;
import com.example.hhflow.model.VacancyStatus;
import com.example.hhflow.service.VacancyService;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckVacancyStatusHandlerTest {

    @Mock
    private VacancyService vacancyService;

    @Mock
    private ExternalTask externalTask;

    @Mock
    private ExternalTaskService externalTaskService;

    @InjectMocks
    private CheckVacancyStatusHandler handler;

    @Test
    void setsVacancyActiveTrueForActiveVacancy() {
        Vacancy vacancy = new Vacancy();
        vacancy.setStatus(VacancyStatus.ACTIVE);

        when(externalTask.getVariable("vacancyId")).thenReturn(1);
        when(vacancyService.getById(1L)).thenReturn(vacancy);

        handler.execute(externalTask, externalTaskService);

        ArgumentCaptor<Map<String, Object>> variablesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(externalTaskService).complete(eq(externalTask), variablesCaptor.capture());
        assertThat(variablesCaptor.getValue()).containsEntry("vacancyActive", true);
    }

    @Test
    void setsVacancyActiveFalseForArchivedVacancy() {
        Vacancy vacancy = new Vacancy();
        vacancy.setStatus(VacancyStatus.ARCHIVED);

        when(externalTask.getVariable("vacancyId")).thenReturn(3);
        when(vacancyService.getById(3L)).thenReturn(vacancy);

        handler.execute(externalTask, externalTaskService);

        ArgumentCaptor<Map<String, Object>> variablesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(externalTaskService).complete(eq(externalTask), variablesCaptor.capture());
        assertThat(variablesCaptor.getValue()).containsEntry("vacancyActive", false);
    }
}
