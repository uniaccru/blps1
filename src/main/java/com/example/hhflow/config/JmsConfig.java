package com.example.hhflow.config;

import com.example.hhflow.messaging.EmployerEmailJmsListener;
import com.rabbitmq.jms.admin.RMQConnectionFactory;
import jakarta.jms.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@EnableJms
@EnableConfigurationProperties(HhMessagingProperties.class)
public class JmsConfig {

    private static final Logger log = LoggerFactory.getLogger(JmsConfig.class);

    @Bean
    public ConnectionFactory jmsConnectionFactory(RabbitProperties rabbitProperties) {
        RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
        connectionFactory.setHost(rabbitProperties.getHost());
        connectionFactory.setPort(rabbitProperties.getPort());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, HhMessagingProperties properties) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setDefaultDestinationName(properties.getEmail().getQueue());
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean
    public DefaultMessageListenerContainer employerEmailListenerContainer(
            ConnectionFactory connectionFactory,
            HhMessagingProperties properties,
            EmployerEmailJmsListener listener
    ) {
        System.out.println("[JMS-CONFIG] Configuring JMS listener container for queue " + properties.getEmail().getQueue());
        log.info("Configuring JMS listener container for queue {}", properties.getEmail().getQueue());
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setDestinationName(properties.getEmail().getQueue());
        container.setMessageListener(listener);
        container.setAutoStartup(true);
        container.setConcurrentConsumers(1);
        container.setRecoveryInterval(5000L);
        container.setErrorHandler(t -> log.error("JMS listener error", t));
        return container;
    }
}
