package com.example.hhflow.camunda;

import org.camunda.bpm.client.task.ExternalTaskHandler;

public interface TopicExternalTaskHandler extends ExternalTaskHandler {

    String getTopic();
}
