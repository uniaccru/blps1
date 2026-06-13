package com.example.hhflow.service;

import com.example.odoo.connector.ApplicantData;
import com.example.odoo.connector.OdooConnection;
import com.example.odoo.connector.OdooConnectionFactory;
import com.example.hhflow.model.JobApplication;
import jakarta.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OdooApplicantSyncService {

    private static final Logger log = LoggerFactory.getLogger(OdooApplicantSyncService.class);

    private final OdooConnectionFactory connectionFactory;
    private final boolean enabled;

    public OdooApplicantSyncService(
            @Autowired(required = false) OdooConnectionFactory connectionFactory,
            @Value("${hh.odoo.enabled:false}") boolean enabled) {
        this.connectionFactory = connectionFactory;
        this.enabled = enabled;
    }

    public void syncApplicant(JobApplication application) {
        if (!enabled) {
            return;
        }
        if (connectionFactory == null) {
            log.warn("Odoo sync skipped for applicationId={}: connection factory is not configured", application.getId());
            return;
        }

        ApplicantData applicantData = new ApplicantData(
                application.getResume().getFullName(),
                application.getApplicant().getEmail(),
                application.getVacancy().getTitle(),
                application.getCreatedAt(),
                application.getResume().getSummary()
        );

        try (OdooConnection connection = connectionFactory.getConnection()) {
            int applicantId = connection.createApplicant(applicantData);
            log.info("Odoo applicant created for applicationId={}, odooApplicantId={}", application.getId(), applicantId);
        } catch (ResourceException e) {
            log.warn("Odoo sync failed for applicationId={}: {}", application.getId(), e.getMessage());
        }
    }
}
