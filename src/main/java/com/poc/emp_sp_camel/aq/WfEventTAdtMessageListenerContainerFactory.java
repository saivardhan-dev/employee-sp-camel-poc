package com.poc.emp_sp_camel.aq;

import org.apache.camel.component.jms.JmsEndpoint;
import org.apache.camel.component.jms.MessageListenerContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.AbstractMessageListenerContainer;

public class WfEventTAdtMessageListenerContainerFactory
        implements MessageListenerContainerFactory {

    private static final Logger log = LoggerFactory.getLogger(
            WfEventTAdtMessageListenerContainerFactory.class);

    private final String durableSubscriptionName;

    public WfEventTAdtMessageListenerContainerFactory(
            String durableSubscriptionName) {
        this.durableSubscriptionName = durableSubscriptionName;
    }

    @Override
    public AbstractMessageListenerContainer createMessageListenerContainer(
            JmsEndpoint endpoint) {

        log.info("Creating WF_EVENT_T ADT listener container. " +
                        "endpoint={}, subscription={}",
                endpoint.getEndpointUri(),
                durableSubscriptionName);

        return new WfEventTAdtMessageListenerContainer(
                durableSubscriptionName);
    }
}