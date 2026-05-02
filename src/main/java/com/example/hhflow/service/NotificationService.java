package com.example.hhflow.service;

import com.example.hhflow.model.JobApplication;
import com.example.hhflow.model.Notification;
import com.example.hhflow.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final Clock clock;

    public void notifyEmployer(JobApplication application) {
        log.info("Employer {} notified about application {}", application.getVacancy().getEmployer().getEmail(), application.getId());

        Notification n = new Notification();
        n.setRecipient(application.getVacancy().getEmployer());
        n.setApplication(application);
        n.setMessage(String.format("New application %d for %s", application.getId(), application.getVacancy().getTitle()));
        n.setCreatedAt(OffsetDateTime.now(clock));
        n.setRead(false);

        notificationRepository.save(n);
    }
}
