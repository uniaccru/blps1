package com.example.hhflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "camunda")
public class CamundaProperties {

    private String engineRestUrl = "http://localhost:8081/engine-rest";
    private String workerId = "hh-flow-worker";
    private String restUsername = "";
    private String restPassword = "";
    private boolean identitySyncEnabled = true;
    private String identitySyncInitialPassword = "password";

    public String getEngineRestUrl() {
        return engineRestUrl;
    }

    public void setEngineRestUrl(String engineRestUrl) {
        this.engineRestUrl = engineRestUrl;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getRestUsername() {
        return restUsername;
    }

    public void setRestUsername(String restUsername) {
        this.restUsername = restUsername;
    }

    public String getRestPassword() {
        return restPassword;
    }

    public void setRestPassword(String restPassword) {
        this.restPassword = restPassword;
    }

    public boolean isIdentitySyncEnabled() {
        return identitySyncEnabled;
    }

    public void setIdentitySyncEnabled(boolean identitySyncEnabled) {
        this.identitySyncEnabled = identitySyncEnabled;
    }

    public String getIdentitySyncInitialPassword() {
        return identitySyncInitialPassword;
    }

    public void setIdentitySyncInitialPassword(String identitySyncInitialPassword) {
        this.identitySyncInitialPassword = identitySyncInitialPassword;
    }
}
