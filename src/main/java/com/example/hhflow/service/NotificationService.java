package com.example.hhflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.hhflow.model.JobApplication;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void notifyEmployer(JobApplication application) {
        log.info("Employer {} notified about application {}", application.getVacancy().getEmployerEmail(), application.getId());
    }
}
