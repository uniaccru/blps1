package com.example.hhflow.camunda;

import org.camunda.bpm.client.task.ExternalTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalTaskVariablesTest {

    @Mock
    private ExternalTask externalTask;

    @Test
    void getLongFromIntegerValue() {
        when(externalTask.getVariable("vacancyId")).thenReturn(1);

        assertThat(ExternalTaskVariables.getLong(externalTask, "vacancyId")).isEqualTo(1L);
    }

    @Test
    void getLongFromLongValue() {
        when(externalTask.getVariable("vacancyId")).thenReturn(42L);

        assertThat(ExternalTaskVariables.getLong(externalTask, "vacancyId")).isEqualTo(42L);
    }

    @Test
    void getLongThrowsWhenVariableMissing() {
        when(externalTask.getVariable("vacancyId")).thenReturn(null);

        assertThatThrownBy(() -> ExternalTaskVariables.getLong(externalTask, "vacancyId"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("vacancyId");
    }

    @Test
    void getStringReturnsValue() {
        when(externalTask.getVariable("title")).thenReturn("Java Developer");

        assertThat(ExternalTaskVariables.getString(externalTask, "title")).isEqualTo("Java Developer");
    }

    @Test
    void getStringOrDefaultReturnsDefaultWhenMissing() {
        when(externalTask.getVariable("errorMessage")).thenReturn(null);

        assertThat(ExternalTaskVariables.getStringOrDefault(externalTask, "errorMessage", "Unknown error"))
                .isEqualTo("Unknown error");
    }

    @Test
    void getBooleanReturnsValue() {
        when(externalTask.getVariable("requiresTest")).thenReturn(true);

        assertThat(ExternalTaskVariables.getBoolean(externalTask, "requiresTest")).isTrue();
    }
}
