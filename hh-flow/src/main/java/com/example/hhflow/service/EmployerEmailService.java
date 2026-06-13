package com.example.hhflow.service;

import com.example.hhflow.config.HhMessagingProperties;
import com.example.hhflow.dto.messaging.EmployerApplicationEmailMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployerEmailService {

    private static final Logger log = LoggerFactory.getLogger(EmployerEmailService.class);

    private final HhMessagingProperties messagingProperties;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendApplicationNotification(EmployerApplicationEmailMessage message) {
        if (mailSender == null) {
            log.warn("JavaMailSender is not configured; email to {} was not sent. Message: {}",
                    message.employerEmail(),
                    message.message());
            return;
        }

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(messagingProperties.getEmail().getFrom());
        mail.setTo(message.employerEmail());
        mail.setSubject("New application for " + message.vacancyTitle());
        mail.setText(buildBody(message));

        mailSender.send(mail);
        log.info("Employer email sent to {} for applicationId={}", message.employerEmail(), message.applicationId());
    }

    private String buildBody(EmployerApplicationEmailMessage message) {
        return """
                Hello,

                You have received a new job application.

                Vacancy: %s
                Applicant: %s
                Application ID: %d

                %s

                — HH Flow
                """.formatted(
                message.vacancyTitle(),
                message.applicantName(),
                message.applicationId(),
                message.message()
        );
    }
}
