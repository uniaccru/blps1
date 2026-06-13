package com.example.hhflow.dto.messaging;

public record EmployerApplicationEmailMessage(
        Long applicationId,
        String employerEmail,
        String vacancyTitle,
        String applicantName,
        String message
) {
}
