package com.example.hhflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "camunda")
public class CamundaProperties {

    private String engineRestUrl = "http://localhost:8080/engine-rest";
    private String workerId = "hh-flow-worker";

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
}
