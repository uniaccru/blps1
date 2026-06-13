package com.example.hhflow.messaging;

import com.example.hhflow.dto.messaging.EmployerApplicationEmailMessage;
import com.example.hhflow.service.EmployerEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployerEmailJmsListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(EmployerEmailJmsListener.class);

    private final EmployerEmailService employerEmailService;
    private final ObjectMapper objectMapper;


    @Override
    public void onMessage(Message message) {
        String body = null;
        try {
            if (!(message instanceof TextMessage textMessage)) {
                throw new IllegalStateException("Expected TextMessage but got " + message.getClass().getName());
            }
            body = textMessage.getText();
            EmployerApplicationEmailMessage payload = objectMapper.readValue(body, EmployerApplicationEmailMessage.class);
            log.info("Received employer email JMS message for applicationId={}", payload.applicationId());
            employerEmailService.sendApplicationNotification(payload);
        } catch (Exception e) {
            log.error("Failed to process employer email JMS message: {}", body, e);
            throw new IllegalStateException("Failed to process employer email JMS message", e);
        }
    }
}
