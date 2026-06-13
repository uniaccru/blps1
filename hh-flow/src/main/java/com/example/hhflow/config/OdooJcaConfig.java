package com.example.hhflow.config;

import com.example.odoo.connector.OdooConnectionFactory;
import com.example.odoo.connector.OdooManagedConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OdooProperties.class)
public class OdooJcaConfig {

    @Bean
    @ConditionalOnProperty(prefix = "hh.odoo", name = "enabled", havingValue = "true")
    public OdooConnectionFactory odooConnectionFactory(OdooProperties properties) {
        OdooManagedConnectionFactory managedConnectionFactory = new OdooManagedConnectionFactory();
        managedConnectionFactory.setBaseUrl(properties.getUrl());
        managedConnectionFactory.setDatabase(properties.getDatabase());
        managedConnectionFactory.setUsername(properties.getUsername());
        managedConnectionFactory.setPassword(properties.getPassword());
        try {
            return (OdooConnectionFactory) managedConnectionFactory.createConnectionFactory();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create Odoo JCA connection factory", e);
        }
    }
}
