package com.example.hhflow.camunda.handler;

import com.example.hhflow.model.Resume;
import com.example.hhflow.model.User;
import com.example.hhflow.service.ResumeService;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckResumeHandlerTest {

    @Mock
    private ResumeService resumeService;

    @Mock
    private ExternalTask externalTask;

    @Mock
    private ExternalTaskService externalTaskService;

    @InjectMocks
    private CheckResumeHandler handler;

    @Test
    void completesWithResumeExistsAndResumeIdWhenResumeFound() {
        User owner = new User();
        owner.setId(4L);

        Resume resume = new Resume();
        resume.setId(1L);
        resume.setOwner(owner);

        when(externalTask.getVariable("applicantUserId")).thenReturn(4);
        when(resumeService.findByCandidateId(4L)).thenReturn(Optional.of(resume));

        handler.execute(externalTask, externalTaskService);

        ArgumentCaptor<Map<String, Object>> variablesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(externalTaskService).complete(eq(externalTask), variablesCaptor.capture());
        assertThat(variablesCaptor.getValue())
                .containsEntry("resumeExists", true)
                .containsEntry("resumeId", 1L);
    }

    @Test
    void completesWithResumeExistsFalseWhenResumeNotFound() {
        when(externalTask.getVariable("applicantUserId")).thenReturn(99);
        when(resumeService.findByCandidateId(99L)).thenReturn(Optional.empty());

        handler.execute(externalTask, externalTaskService);

        ArgumentCaptor<Map<String, Object>> variablesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(externalTaskService).complete(eq(externalTask), variablesCaptor.capture());
        assertThat(variablesCaptor.getValue())
                .containsEntry("resumeExists", false)
                .doesNotContainKey("resumeId");
    }
}
