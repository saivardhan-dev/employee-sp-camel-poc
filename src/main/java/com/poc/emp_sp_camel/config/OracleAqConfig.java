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