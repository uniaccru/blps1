package com.example.hhflow.messaging;

import com.example.hhflow.dto.messaging.EmployerApplicationEmailMessage;
import com.example.hhflow.service.EmployerEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployerEmailJmsListener {

    private static final Logger log = LoggerFactory.getLogger(EmployerEmailJmsListener.class);

    private final EmployerEmailService employerEmailService;
    private final ObjectMapper objectMapper;

    @JmsListener(
            id = "employerEmailListener",
            destination = "${hh.email.queue}",
            containerFactory = "jmsListenerContainerFactory"
    )
    public void onEmployerApplicationEmail(String body) {
        try {
            EmployerApplicationEmailMessage payload = objectMapper.readValue(body, EmployerApplicationEmailMessage.class);
            log.info("Received employer email JMS message for applicationId={}", payload.applicationId());
            employerEmailService.sendApplicationNotification(payload);
        } catch (Exception e) {
            log.error("Failed to process employer email JMS message: {}", body, e);
            throw new IllegalStateException("Failed to process employer email JMS message", e);
        }
    }
}
