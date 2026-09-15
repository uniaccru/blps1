package com.example.hhflow.camunda;

import com.example.hhflow.config.CamundaProperties;
import com.example.hhflow.model.User;
import com.example.hhflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CamundaIdentitySyncService {

    private final UserRepository userRepository;
    private final CamundaIdentityClient identityClient;
    private final CamundaProperties properties;

    @EventListener(ApplicationReadyEvent.class)
    public void syncOnStartup() {
        if (!properties.isIdentitySyncEnabled() || !identityClient.isAvailable()) {
            log.info("Camunda identity sync skipped: disabled or engine unavailable");
            return;
        }

        try {
            ensureGroupsAndAuthorizations();
            userRepository.findAll().forEach(user -> syncUser(user, properties.getIdentitySyncInitialPassword()));
        } catch (RuntimeException exception) {
            log.warn("Camunda identity sync failed; application will continue: {}", exception.getMessage());
        }
    }

    public void syncUser(User user, String password) {
        if (!properties.isIdentitySyncEnabled()) {
            return;
        }
        try {
            String userId = camundaUserId(user);
            identityClient.ensureUser(userId, user.getEmail(), password);
            identityClient.ensureMembership(userId, user.getRole().name());
        } catch (RuntimeException exception) {
            log.warn("Cannot sync user {} to Camunda: {}", user.getEmail(), exception.getMessage());
        }
    }

    private void ensureGroupsAndAuthorizations() {
        identityClient.ensureGroup("APPLICANT", "Applicants");
        identityClient.ensureGroup("EMPLOYER", "Employers");
        identityClient.ensureGroupAuthorization("APPLICANT", 0, "tasklist", java.util.List.of("ACCESS"));
        identityClient.ensureGroupAuthorization("EMPLOYER", 0, "tasklist", java.util.List.of("ACCESS"));
        identityClient.ensureGroupAuthorization("APPLICANT", 8, "*", java.util.List.of("CREATE"));
        identityClient.ensureGroupAuthorization("EMPLOYER", 8, "*", java.util.List.of("CREATE"));
        identityClient.ensureGroupAuthorization("APPLICANT", 6, "Process_JobApplication", java.util.List.of("CREATE_INSTANCE", "READ"));
        identityClient.ensureGroupAuthorization("EMPLOYER", 6, "Process_CreateVacancy", java.util.List.of("CREATE_INSTANCE", "READ"));
    }

    public static String camundaUserId(User user) {
        String email = user.getEmail() == null ? "" : user.getEmail().trim().toLowerCase();
        String normalized = email.replaceAll("[^a-z0-9]", "");
        return normalized.isBlank() ? "user" + user.getId() : normalized;
    }
}