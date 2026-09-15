package com.example.hhflow.service;

import com.example.hhflow.config.CamundaProperties;
import com.example.hhflow.dto.request.SubmitApplicationRequest;
import com.example.hhflow.dto.response.ApplicationDto;
import com.example.hhflow.dto.response.SubmissionResponse;
import com.example.hhflow.mapper.ApiMapper;
import com.example.hhflow.model.SubmissionOutcome;
import com.example.hhflow.model.User;
import com.example.hhflow.model.Vacancy;
import com.example.hhflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CamundaProcessClient {

    private static final Duration OUTCOME_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration POLL_INTERVAL = Duration.ofMillis(200);

    private final CamundaProperties properties;
    private final JobApplicationService jobApplicationService;
    private final VacancyService vacancyService;
    private final UserRepository userRepository;
    private final ApiMapper apiMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public SubmissionResponse submitApplication(SubmitApplicationRequest request, Long applicantUserId) {
        Vacancy vacancy = vacancyService.getById(request.getVacancyId());
        Map<String, Object> variables = new HashMap<>();
        variables.put("applicantUserId", variable(applicantUserId, "Long"));
        variables.put("applicantCamundaUserId", variable(camundaUserId(applicantUserId), "String"));
        variables.put("employerCamundaUserId", variable(camundaUserId(vacancy.getEmployer()), "String"));
        variables.put("vacancyId", variable(request.getVacancyId(), "Long"));
        variables.put("resumeId", variable(request.getResumeId(), "Long"));
        variables.put("resumeFullName", variable(request.getResumeFullName(), "String"));
        variables.put("resumeSummary", variable(request.getResumeSummary(), "String"));

        String processInstanceId = startProcess("Process_JobApplication", variables);
        return awaitApplicationOutcome(processInstanceId, OUTCOME_TIMEOUT);
    }

    public String startVacancyProcess(Long employerUserId) {
        User employer = userRepository.findById(employerUserId)
                .orElseThrow(() -> new IllegalStateException("Employer not found: " + employerUserId));
        return startProcess("Process_CreateVacancy", Map.of(
                "employerUserId", variable(employerUserId, "Long"),
                "employerCamundaUserId", variable(camundaUserId(employer), "String")
        ));
    }

    private String camundaUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));
        return camundaUserId(user);
    }

    private String camundaUserId(User user) {
        String email = user.getEmail() == null ? "" : user.getEmail().trim().toLowerCase();
        String normalized = email.replaceAll("[^a-z0-9]", "");
        return normalized.isBlank() ? "user" + user.getId() : normalized;
    }

    private String startProcess(String processDefinitionKey, Map<String, Object> variables) {
        String url = properties.getEngineRestUrl()
                + "/process-definition/key/" + processDefinitionKey + "/start";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of("variables", variables);
        Map<?, ?> response = restTemplate.postForObject(url, new HttpEntity<>(body, headers), Map.class);
        if (response == null || response.get("id") == null) {
            throw new IllegalStateException("Camunda did not return a process instance id");
        }
        return response.get("id").toString();
    }

    private SubmissionResponse awaitApplicationOutcome(String processInstanceId, Duration timeout) {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            Map<String, Object> variables = readVariables(processInstanceId);
            SubmissionResponse outcome = toSubmissionResponse(variables);
            if (outcome != null) {
                return outcome;
            }
            sleep(POLL_INTERVAL);
        }

        return new SubmissionResponse(
                SubmissionOutcome.PENDING,
                "Application process is still running.",
                null
        );
    }

    private SubmissionResponse toSubmissionResponse(Map<String, Object> variables) {
        Boolean vacancyActive = booleanVariable(variables, "vacancyActive");
        if (Boolean.FALSE.equals(vacancyActive)) {
            return new SubmissionResponse(
                    SubmissionOutcome.VACANCY_ARCHIVED,
                    "Vacancy is archived. Submission declined.",
                    null
            );
        }

        Boolean testPassed = booleanVariable(variables, "testPassed");
        if (Boolean.FALSE.equals(testPassed)) {
            return new SubmissionResponse(
                    SubmissionOutcome.AUTO_REJECTED,
                    "Test is not passed. Automatic rejection.",
                    null
            );
        }

        Long applicationId = longVariable(variables, "applicationId");
        if (applicationId == null) {
            return null;
        }

        ApplicationDto application = apiMapper.toDto(jobApplicationService.getById(applicationId));
        return new SubmissionResponse(
                SubmissionOutcome.COMPLETED,
                "Application successfully created and is waiting for employer review.",
                application
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readVariables(String processInstanceId) {
        String url = properties.getEngineRestUrl()
                + "/history/variable-instance?processInstanceId=" + processInstanceId;
        List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
        Map<String, Object> variables = new HashMap<>();
        if (response != null) {
            for (Map<String, Object> variable : response) {
                variables.put((String) variable.get("name"), variable.get("value"));
            }
        }
        return variables;
    }

    private Map<String, Object> variable(Object value, String type) {
        Map<String, Object> variable = new HashMap<>();
        variable.put("value", value);
        variable.put("type", type);
        return variable;
    }

    private Boolean booleanVariable(Map<String, Object> variables, String name) {
        Object value = variables.get(name);
        return value instanceof Boolean ? (Boolean) value : null;
    }

    private Long longVariable(Map<String, Object> variables, String name) {
        Object value = variables.get(name);
        return value instanceof Number ? ((Number) value).longValue() : null;
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for Camunda process", exception);
        }
    }
}