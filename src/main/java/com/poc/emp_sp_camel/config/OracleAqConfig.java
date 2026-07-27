package com.poc.emp_sp_camel.config;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jms.AQjmsFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

/**
 * OracleAqConfig — Configures Oracle Advanced Queue (AQ) as a JMS provider
 * for Apache Camel.
 *
 * Oracle AQ supports the JMS standard natively via the AQjmsFactory.
 * This configuration creates a dedicated, unpooled OracleDataSource
 * specifically for AQ — separate from the HikariCP DataSource used for
 * JDBC/stored procedure calls — because Oracle AQ's JMS layer requires
 * direct access to the underlying OracleConnection and cannot work with
 * Hikari's proxied connections.
 *
 * The resulting JmsComponent is registered as "Jms" in the Camel context,
 * allowing routes to consume from Oracle AQ using:
 *   from("Jms:queue:EMPLOYEE_EVENT_Q")
 *
 * Note: This bean registration causes Spring Boot's ActiveMQAutoConfiguration
 * to back off (it only fires when no ConnectionFactory bean exists). A
 * separate ActiveMqConfig is therefore required to explicitly wire the
 * ActiveMQ component.
 */

@Configuration
public class OracleAqConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String jdbcUsername;

    @Value("${spring.datasource.password}")
    private String jdbcPassword;

    @Bean
    public ConnectionFactory oracleAqConnectionFactory()
            throws JMSException, SQLException {
        OracleDataSource aqDataSource = new OracleDataSource();
        aqDataSource.setURL(jdbcUrl);
        aqDataSource.setUser(jdbcUsername);
        aqDataSource.setPassword(jdbcPassword);
        return AQjmsFactory.getQueueConnectionFactory(aqDataSource);
    }

    @Bean(name = "Jms")
    public JmsComponent aqJmsComponent(
            @Qualifier("oracleAqConnectionFactory")
            ConnectionFactory oracleAqConnectionFactory) {
        return JmsComponent.jmsComponent(oracleAqConnectionFactory);
    }
}