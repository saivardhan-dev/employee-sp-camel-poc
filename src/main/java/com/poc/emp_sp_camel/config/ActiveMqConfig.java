package com.poc.emp_sp_camel.config;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ActiveMqConfig — Configures ActiveMQ Classic as a separate JMS provider
 * for Apache Camel.
 *
 * This configuration is required because once OracleAqConfig registers a
 * ConnectionFactory bean for Oracle AQ, Spring Boot's
 * ActiveMQAutoConfiguration backs off — it only fires when no
 * ConnectionFactory bean exists. Without this explicit configuration,
 * the "activemq" Camel component would silently wire to the Oracle AQ
 * connection factory instead of the real ActiveMQ broker.
 *
 * The resulting JmsComponent is registered as "activemq" in the Camel
 * context, allowing routes to publish to ActiveMQ using:
 *   .to("activemq:queue:employee.output")
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
