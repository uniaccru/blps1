package com.example.hhflow.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.time.Clock;

@Configuration
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    /**
     * Configure JNDI DataSource for WildFly deployment.
     * When deployed on WildFly, the datasource is managed by the container.
     * The jndi-name property is read from application.yml
     */
    @Bean
    @ConditionalOnProperty(
        name = "spring.datasource.jndi-name",
        matchIfMissing = false,
        havingValue = "java:/PostgresDS"
    )
    public DataSource jndiDataSource() {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("java:/PostgresDS");
        bean.setResourceRef(true);
        bean.setProxyInterface(DataSource.class);
        try {
            bean.afterPropertiesSet();
        } catch (IllegalArgumentException | NamingException e) {
            // If JNDI lookup fails, datasource will fallback to auto-configuration
            return null;
        }
        return (DataSource) bean.getObject();
    }
}

