package com.example.hhflow.scheduler;

import com.example.hhflow.config.HhMessagingProperties;
import com.example.hhflow.service.OutboxRelayService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxRelayScheduler {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelayScheduler.class);

    private final OutboxRelayService outboxRelayService;
    private final HhMessagingProperties messagingProperties;

    @Scheduled(fixedDelayString = "${hh.outbox.poll-interval-ms:5000}")
    public void relayPendingOutboxEvents() {
        int published = outboxRelayService.relayPendingEvents();
        if (published > 0) {
            log.debug("Relayed {} outbox event(s) to JMS queue {}", published, messagingProperties.getEmail().getQueue());
        }
    }
}
