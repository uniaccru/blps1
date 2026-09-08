package com.example.hhflow.camunda;

import com.example.hhflow.config.CamundaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CamundaIdentityClient {

    private final CamundaProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    public boolean isAvailable() {
        try {
            request(HttpMethod.GET, "/engine", null, Object.class);
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }

    public void ensureGroup(String groupId, String groupName) {
        List<?> groups = request(HttpMethod.GET, "/group?groupId=" + groupId, null, List.class);
        if (groups == null || groups.isEmpty()) {
            Map<String, Object> body = Map.of("id", groupId, "name", groupName, "type", "WORKFLOW");
            request(HttpMethod.POST, "/group/create", body, Object.class);
        }
    }

    public void ensureUser(String userId, String email, String password) {
        List<?> users = request(HttpMethod.GET, "/user?id=" + userId, null, List.class);
        if (users == null || users.isEmpty()) {
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", userId);
            profile.put("email", email);
            profile.put("firstName", email);
            profile.put("lastName", "");
            Map<String, Object> body = new HashMap<>();
            body.put("profile", profile);
            body.put("credentials", Map.of("password", password));
            request(HttpMethod.POST, "/user/create", body, Object.class);
        }
    }

    public void ensureMembership(String userId, String groupId) {
        request(HttpMethod.PUT, "/group/" + groupId + "/members/" + userId, null, Object.class);
    }

    public void ensureGroupAuthorization(String groupId, int resourceType, String resourceId, List<String> permissions) {
        String query = "/authorization?type=1&groupIdIn=" + groupId
                + "&resourceType=" + resourceType + "&resourceId=" + resourceId;
        List<?> authorizations = request(HttpMethod.GET, query, null, List.class);
        if (authorizations != null && !authorizations.isEmpty()) {
            return;
        }
        Map<String, Object> body = new HashMap<>();
        body.put("type", 1);
        body.put("groupId", groupId);
        body.put("resourceType", resourceType);
        body.put("resourceId", resourceId);
        body.put("permissions", permissions);
        request(HttpMethod.POST, "/authorization/create", body, Object.class);
    }

    private <T> T request(HttpMethod method, String path, Object body, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (properties.getRestUsername() != null && !properties.getRestUsername().isBlank()) {
            String credentials = properties.getRestUsername() + ":" + properties.getRestPassword();
            String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
            headers.set(HttpHeaders.AUTHORIZATION, "Basic " + encoded);
        }
        return restTemplate.exchange(
                properties.getEngineRestUrl() + path,
                method,
                new HttpEntity<>(body, headers),
                responseType
        ).getBody();
    }
}