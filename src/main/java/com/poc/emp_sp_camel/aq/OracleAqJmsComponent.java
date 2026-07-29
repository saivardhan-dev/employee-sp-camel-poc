package com.poc.emp_sp_camel.aq;

import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.AbstractMessageListenerContainer;

/**
 * Custom JmsComponent that forces use of WfEventTAdtMessageListenerContainer
 * for every endpoint — overrides createMessageListenerContainer directly.
 */
public class OracleAqJmsComponent extends JmsComponent {

    private static final Logger log = LoggerFactory.getLogger(
            OracleAqJmsComponent.class);

    private final WfEventTAdtMessageListenerContainerFactory factory;

    public OracleAqJmsComponent(
            WfEventTAdtMessageListenerContainerFactory factory) {
        this.factory = factory;
    }

    @Override
    public AbstractMessageListenerContainer createMessageListenerContainer(
            JmsEndpoint endpoint) {
        log.info(">>> OracleAqJmsComponent: creating ADT listener " +
                "container for: {}", endpoint.getEndpointUri());
        return factory.createMessageListenerContainer(endpoint);
    }
}