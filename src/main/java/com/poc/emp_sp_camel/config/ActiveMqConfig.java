package com.poc.emp_sp_camel.config;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Explicit ActiveMQ ConnectionFactory + "activemq" Camel component.
 * <p>
 * Once OracleAqConfig registers a ConnectionFactory bean for AQ, Spring
 * Boot's own ActiveMQAutoConfiguration backs off (it only fires when no
 * ConnectionFactory bean exists yet), so the "activemq" component would
 * otherwise silently end up wired to the AQ connection factory instead of
 * a real broker. Defining both integrations explicitly avoids relying on
 * that autoconfiguration ordering.
 */
@Configuration
public class ActiveMqConfig {

    @Bean
    public ConnectionFactory activeMqConnectionFactory(
            @Value("${spring.activemq.broker-url}") String brokerUrl,
            @Value("${spring.activemq.user}") String user,
            @Value("${spring.activemq.password}") String password) {
        return new ActiveMQConnectionFactory(user, password, brokerUrl);
    }

    @Bean(name = "activemq")
    public JmsComponent activemqComponent(
            @Qualifier("activeMqConnectionFactory") ConnectionFactory activeMqConnectionFactory) {
        return JmsComponent.jmsComponent(activeMqConnectionFactory);
    }
}
