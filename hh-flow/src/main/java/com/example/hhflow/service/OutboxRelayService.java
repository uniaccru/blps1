package com.example.hhflow.service;

import com.example.hhflow.config.HhMessagingProperties;
import com.example.hhflow.model.EmailOutbox;
import com.example.hhflow.model.OutboxStatus;
import com.example.hhflow.repository.EmailOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxRelayService {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelayService.class);

    private final EmailOutboxRepository emailOutboxRepository;
    private final JmsTemplate jmsTemplate;
    private final HhMessagingProperties messagingProperties;
    private final Clock clock;

    @Transactional
    public int relayPendingEvents() {
        Pageable limit = PageRequest.of(0, messagingProperties.getOutbox().getBatchSize());
        List<EmailOutbox> pending = emailOutboxRepository.findByStatusOrderByCreatedAtAsc(
                OutboxStatus.PENDING,
                limit
        );

        int published = 0;
        String destination = messagingProperties.getEmail().getQueue();

        for (EmailOutbox event : pending) {
            try {
                jmsTemplate.send(destination, session -> session.createTextMessage(event.getPayload()));
                event.setStatus(OutboxStatus.PUBLISHED);
                event.setPublishedAt(OffsetDateTime.now(clock));
                event.setLastError(null);
                published++;
                log.info("Published outbox event id={} for applicationId={}", event.getId(), event.getAggregateId());
            } catch (Exception e) {
                event.setRetryCount(event.getRetryCount() + 1);
                event.setLastError(e.getMessage());
                if (event.getRetryCount() >= messagingProperties.getOutbox().getMaxRetries()) {
                    event.setStatus(OutboxStatus.FAILED);
                    log.error("Outbox event id={} failed after {} retries", event.getId(), event.getRetryCount(), e);
                } else {
                    log.warn("Failed to publish outbox event id={}, retry {}/{}",
                            event.getId(),
                            event.getRetryCount(),
                            messagingProperties.getOutbox().getMaxRetries(),
                            e);
                }
            }
        }

        return published;
    }
}
