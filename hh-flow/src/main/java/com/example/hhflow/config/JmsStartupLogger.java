package com.example.hhflow.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JmsStartupLogger {

    private static final Logger log = LoggerFactory.getLogger(JmsStartupLogger.class);
    private static final String LISTENER_ID = "employerEmailListener";

    private final JmsListenerEndpointRegistry registry;
    private final HhMessagingProperties messagingProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        MessageListenerContainer container = registry.getListenerContainer(LISTENER_ID);
        if (container instanceof DefaultMessageListenerContainer dmlc) {
            log.info("JMS employer email listener running={}, concurrentConsumers={}, queue={}",
                    dmlc.isRunning(), dmlc.getConcurrentConsumers(), messagingProperties.getEmail().getQueue());
        } else if (container != null) {
            log.info("JMS employer email listener running={}, queue={}",
                    container.isRunning(), messagingProperties.getEmail().getQueue());
        } else {
            log.warn("JMS employer email listener container '{}' not found", LISTENER_ID);
        }
    }
}
