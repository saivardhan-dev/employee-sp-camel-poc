package com.poc.emp_sp_camel.config;

import com.poc.emp_sp_camel.aq.WfEventTAdtMessageListenerContainerFactory;
import com.poc.emp_sp_camel.aq.WfEventTAqDestinationResolver;
import com.poc.emp_sp_camel.aq.WfEventTAqMessageConverter;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jakarta.jms.AQjmsFactory;
import org.apache.camel.component.jms.ConsumerType;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class OracleAqJmsConfig {

    private static final Logger log =
            LoggerFactory.getLogger(OracleAqJmsConfig.class);

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String jdbcUsername;

    @Value("${spring.datasource.password}")
    private String jdbcPassword;

    @Value("${oracle.aq.schema:EMPUSER}")
    private String aqSchema;

    @Value("${oracle.aq.subscriber:ORA_EDICUSTOMSCTMSSUBSCRIBER}")
    private String aqSubscriber;

    @Bean(name = "oracleAqDataSource")
    public DataSource oracleAqDataSource() throws SQLException {
        OracleDataSource ds = new OracleDataSource();
        ds.setURL(jdbcUrl);
        ds.setUser(jdbcUsername);
        ds.setPassword(jdbcPassword);
        System.out.println(">>> DB URL: " + jdbcUrl);
        System.out.println(">>> DB User: " + jdbcUsername);
        return ds;
    }

    @Bean(name = "oracleAqConnectionFactory")
    public ConnectionFactory oracleAqConnectionFactory(
            @Qualifier("oracleAqDataSource") DataSource oracleAqDataSource)
            throws JMSException, SQLException {
        return AQjmsFactory.getQueueConnectionFactory(
                (OracleDataSource) oracleAqDataSource);
    }

    @Bean
    public WfEventTAqMessageConverter wfEventTAqMessageConverter(
            @Qualifier("oracleAqDataSource") DataSource oracleAqDataSource) {
        return new WfEventTAqMessageConverter(oracleAqDataSource);
    }

    @Bean
    public WfEventTAqDestinationResolver wfEventTAqDestinationResolver() {
        return new WfEventTAqDestinationResolver(aqSchema);
    }

    @Bean
    public WfEventTAdtMessageListenerContainerFactory
    wfEventTAdtMessageListenerContainerFactory() {
        return new WfEventTAdtMessageListenerContainerFactory(aqSubscriber);
    }

    @Bean(name = "Jms")
    @Primary
    public JmsComponent aqJmsComponent(
            @Qualifier("oracleAqConnectionFactory")
            ConnectionFactory oracleAqConnectionFactory,
            WfEventTAqMessageConverter wfEventTAqMessageConverter,
            WfEventTAqDestinationResolver wfEventTAqDestinationResolver,
            WfEventTAdtMessageListenerContainerFactory factory) {

        JmsConfiguration config =
                new JmsConfiguration(oracleAqConnectionFactory);

        config.setMessageConverter(wfEventTAqMessageConverter);
        config.setDestinationResolver(wfEventTAqDestinationResolver);
        config.setMessageListenerContainerFactory(factory);

        // ConsumerType.Custom → triggers getCustomMessageListenerContainer()
        // → uses WfEventTAdtMessageListenerContainerFactory ✅
        config.setConsumerType(ConsumerType.Custom);

        // Debug — verify config is set correctly
        System.out.println(">>> Factory set: " +
                config.getMessageListenerContainerFactory());
        System.out.println(">>> ConsumerType: " +
                config.getConsumerType());

        JmsComponent component = new JmsComponent();
        component.setConfiguration(config);

        System.out.println(">>> Component config factory: " +
                component.getConfiguration()
                        .getMessageListenerContainerFactory());
        System.out.println(">>> Component config consumerType: " +
                component.getConfiguration().getConsumerType());

        return component;
    }
}