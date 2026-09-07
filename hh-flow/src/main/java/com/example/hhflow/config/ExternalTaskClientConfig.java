package com.example.hhflow.config;

import com.example.hhflow.camunda.TopicExternalTaskHandler;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(CamundaProperties.class)
public class ExternalTaskClientConfig {

    @Bean(destroyMethod = "stop")
    public ExternalTaskClient externalTaskClient(
            CamundaProperties properties,
            List<TopicExternalTaskHandler> handlers) {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl(properties.getEngineRestUrl())
                .workerId(properties.getWorkerId())
                .asyncResponseTimeout(10000)
                .build();
        handlers.forEach(h -> client.subscribe(h.getTopic())
                .lockDuration(20000)
                .handler(h)
                .open());
        return client;
    }
}
