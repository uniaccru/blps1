package com.example.hhflow.camunda.handler;

import com.example.hhflow.service.ApplicationProcessService;
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
class RegisterApplicationTxHandlerTest {

    @Mock
    private ApplicationProcessService applicationProcessService;

    @Mock
    private ExternalTask externalTask;

    @Mock
    private ExternalTaskService externalTaskService;

    @InjectMocks
    private RegisterApplicationTxHandler handler;

    @Test
    void completesTaskWithApplicationIdOnSuccess() {
        when(externalTask.getVariable("applicantUserId")).thenReturn(4);
        when(externalTask.getVariable("vacancyId")).thenReturn(1);
        when(externalTask.getVariable("resumeId")).thenReturn(1);
        when(applicationProcessService.registerApplicationTx(4L, 1L, 1L)).thenReturn(100L);

        handler.execute(externalTask, externalTaskService);

        ArgumentCaptor<Map<String, Object>> variablesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(externalTaskService).complete(eq(externalTask), variablesCaptor.capture());
        assertThat(variablesCaptor.getValue()).containsEntry("applicationId", 100L);
    }

    @Test
    void handlesBpmnErrorOnFailure() {
        when(externalTask.getVariable("applicantUserId")).thenReturn(4);
        when(externalTask.getVariable("vacancyId")).thenReturn(2);
        when(externalTask.getVariable("resumeId")).thenReturn(1);
        when(applicationProcessService.registerApplicationTx(4L, 2L, 1L))
                .thenThrow(new RuntimeException("Demo rollback triggered"));

        handler.execute(externalTask, externalTaskService);

        verify(externalTaskService).handleBpmnError(
                externalTask,
                "APPLICATION_TX_FAILED",
                "Demo rollback triggered"
        );
    }
}
