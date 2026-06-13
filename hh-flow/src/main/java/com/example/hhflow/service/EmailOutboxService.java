package com.example.hhflow.service;

import com.example.hhflow.dto.messaging.EmployerApplicationEmailMessage;
import com.example.hhflow.model.EmailOutbox;
import com.example.hhflow.model.JobApplication;
import com.example.hhflow.model.OutboxEventType;
import com.example.hhflow.model.OutboxStatus;
import com.example.hhflow.repository.EmailOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class EmailOutboxService {

    private static final Logger log = LoggerFactory.getLogger(EmailOutboxService.class);

    private final EmailOutboxRepository emailOutboxRepository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public void enqueueEmployerApplicationEmail(JobApplication application, String notificationMessage) {
        try {
            EmployerApplicationEmailMessage message = new EmployerApplicationEmailMessage(
                    application.getId(),
                    application.getVacancy().getEmployer().getEmail(),
                    application.getVacancy().getTitle(),
                    application.getResume().getFullName(),
                    notificationMessage
            );

            EmailOutbox outbox = new EmailOutbox();
            outbox.setEventType(OutboxEventType.EMPLOYER_APPLICATION_EMAIL);
            outbox.setAggregateId(application.getId());
            outbox.setPayload(objectMapper.writeValueAsString(message));
            outbox.setStatus(OutboxStatus.PENDING);
            outbox.setCreatedAt(OffsetDateTime.now(clock));
            outbox.setRetryCount(0);

            emailOutboxRepository.save(outbox);
            log.info("Queued employer email outbox event for applicationId={}", application.getId());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize employer email outbox payload", e);
        }
    }
}
