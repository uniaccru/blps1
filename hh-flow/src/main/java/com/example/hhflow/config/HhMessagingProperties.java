package com.example.hhflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hh")
public class HhMessagingProperties {

    private final Email email = new Email();
    private final Outbox outbox = new Outbox();

    public Email getEmail() {
        return email;
    }

    public Outbox getOutbox() {
        return outbox;
    }

    public static class Email {
        private String queue = "employer.application.email";
        private String from = "noreply@hhflow.local";

        public String getQueue() {
            return queue;
        }

        public void setQueue(String queue) {
            this.queue = queue;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }
    }

    public static class Outbox {
        private long pollIntervalMs = 5000;
        private int batchSize = 50;
        private int maxRetries = 5;

        public long getPollIntervalMs() {
            return pollIntervalMs;
        }

        public void setPollIntervalMs(long pollIntervalMs) {
            this.pollIntervalMs = pollIntervalMs;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public int getMaxRetries() {
            return maxRetries;
        }

        public void setMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
        }
    }
}
