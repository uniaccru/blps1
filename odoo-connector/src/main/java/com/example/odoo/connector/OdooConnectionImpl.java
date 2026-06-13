package com.example.odoo.connector;

import jakarta.resource.ResourceException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OdooConnectionImpl implements OdooConnection {

    private static final Pattern RESULT_PATTERN = Pattern.compile("\"result\"\\s*:\\s*(-?\\d+)");
    private static final Pattern ERROR_PATTERN = Pattern.compile("\"message\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final OdooManagedConnectionFactory config;
    private final HttpClient httpClient;
    private OdooManagedConnection managedConnection;
    private boolean valid = true;
    private int requestId = 1;

    OdooConnectionImpl(OdooManagedConnectionFactory config, OdooManagedConnection managedConnection) {
        this.config = config;
        this.managedConnection = managedConnection;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
    }

    void associate(OdooManagedConnection managedConnection) {
        this.managedConnection = managedConnection;
        this.valid = true;
    }

    void invalidate() {
        this.valid = false;
        this.managedConnection = null;
    }

    @Override
    public int createApplicant(ApplicantData data) throws ResourceException {
        ensureValid();
        try {
            int uid = authenticate();
            return createApplicantRecord(uid, data);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResourceException("Odoo request interrupted", e);
        } catch (IOException e) {
            throw new ResourceException("Odoo request failed: " + e.getMessage(), e);
        }
    }

    private int authenticate() throws IOException, InterruptedException, ResourceException {
        String body = """
                {"jsonrpc":"2.0","method":"call","params":{"service":"common","method":"authenticate","args":["%s","%s","%s",{}]},"id":%d}
                """.formatted(
                escapeJson(config.getDatabase()),
                escapeJson(config.getUsername()),
                escapeJson(config.getPassword()),
                nextRequestId()
        );
        String response = postJson(body);
        Integer uid = extractResultInt(response);
        if (uid == null || uid <= 0) {
            throw new ResourceException("Odoo authentication failed: " + extractErrorMessage(response));
        }
        return uid;
    }

    private int createApplicantRecord(int uid, ApplicantData data)
            throws IOException, InterruptedException, ResourceException {
        String description = "Applied: " + data.appliedAt() + "\nResume: " + data.resumeContent();
        String valuesJson = """
                {"partner_name":"%s","email_from":"%s","name":"%s","description":"%s"}
                """.formatted(
                escapeJson(data.candidateName()),
                escapeJson(data.email()),
                escapeJson(data.vacancyTitle()),
                escapeJson(description)
        );
        String body = """
                {"jsonrpc":"2.0","method":"call","params":{"service":"object","method":"execute_kw","args":["%s",%d,"%s","hr.applicant","create",[%s]]},"id":%d}
                """.formatted(
                escapeJson(config.getDatabase()),
                uid,
                escapeJson(config.getPassword()),
                valuesJson,
                nextRequestId()
        );
        String response = postJson(body);
        Integer applicantId = extractResultInt(response);
        if (applicantId == null || applicantId <= 0) {
            throw new ResourceException("Failed to create Odoo applicant: " + extractErrorMessage(response));
        }
        return applicantId;
    }

    private String postJson(String body) throws IOException, InterruptedException, ResourceException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(normalizeBaseUrl(config.getBaseUrl()) + "/jsonrpc"))
                .timeout(TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new ResourceException("Odoo HTTP error: " + response.statusCode());
        }
        return response.body();
    }

    private int nextRequestId() {
        return requestId++;
    }

    private void ensureValid() throws ResourceException {
        if (!valid) {
            throw new ResourceException("Connection handle is invalid");
        }
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    private static Integer extractResultInt(String response) {
        Matcher matcher = RESULT_PATTERN.matcher(response);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }

    private static String extractErrorMessage(String response) {
        Matcher matcher = ERROR_PATTERN.matcher(response);
        if (matcher.find()) {
            return unescapeJson(matcher.group(1));
        }
        return response;
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String unescapeJson(String value) {
        return value
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    @Override
    public void close() {
        if (managedConnection != null) {
            managedConnection.dissociateConnection(this);
        }
        invalidate();
    }
}
