package com.example.hhflow.camunda.handler;

import com.example.hhflow.camunda.TopicExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CheckTokenHandler implements TopicExternalTaskHandler {

    @Override
    public String getTopic() {
        return "checkToken";
    }

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        externalTaskService.complete(externalTask, Map.of("authorized", true));
    }
}
