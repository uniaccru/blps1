package com.example.hhflow.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@Component
public class JmsStartupLogger {

    private static final Logger log = LoggerFactory.getLogger(JmsStartupLogger.class);

    private final DefaultMessageListenerContainer employerContainer;

    public JmsStartupLogger(@Qualifier("employerEmailListenerContainer") DefaultMessageListenerContainer employerContainer) {
        this.employerContainer = employerContainer;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        System.out.println("[JMS-STARTUP] employerContainer.isRunning=" + employerContainer.isRunning() + ", concurrentConsumers=" + employerContainer.getConcurrentConsumers());
        log.info("JMS Employer listener running={}, concurrentConsumers={}",
            employerContainer.isRunning(), employerContainer.getConcurrentConsumers());
    }
}
