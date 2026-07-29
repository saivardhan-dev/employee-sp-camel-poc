package com.poc.emp_sp_camel.aq;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;
import jakarta.jms.Topic;
import oracle.jms.AQjmsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * Overrides createConsumer to register WfEventTPayloadFactory
 * via AQjmsSession.createDurableSubscriber() — fixes JMS-137.
 */
public class WfEventTAdtMessageListenerContainer
        extends DefaultMessageListenerContainer {

    private static final Logger log = LoggerFactory.getLogger(
            WfEventTAdtMessageListenerContainer.class);

    private final String durableSubscriptionName;

    public WfEventTAdtMessageListenerContainer(
            String durableSubscriptionName) {
        this.durableSubscriptionName = durableSubscriptionName;
    }

    @Override
    protected MessageConsumer createConsumer(Session session,
                                             Destination destination)
            throws JMSException {

        log.info("Creating Oracle AQ consumer. session={}, " +
                        "destination={}, subscription={}",
                session.getClass().getName(),
                destination.getClass().getName(),
                durableSubscriptionName);

        // Must be AQjmsSession for Oracle AQ specific API
        if (!(session instanceof AQjmsSession aqSession)) {
            throw new JMSException(
                    "Expected AQjmsSession but got: "
                            + session.getClass().getName());
        }

        // Must be Topic for durable subscriber
        if (!(destination instanceof Topic topic)) {
            throw new JMSException(
                    "Expected Topic but got: "
                            + destination.getClass().getName());
        }

        log.info("Registering WfEventTPayloadFactory on " +
                "createDurableSubscriber — fixes JMS-137.");

        try {
            // createDurableSubscriber(Topic, subscriberName, payloadFactory)
            // Third param = AQObjectPayload implementation → fixes JMS-137 ✅
            return aqSession.createDurableSubscriber(
                    topic,
                    durableSubscriptionName,
                    WfEventTPayloadFactory.INSTANCE  // ORADataFactory ✅
            );
        } catch (JMSException e) {
            log.error("Failed to create durable subscriber. " +
                            "topic={}, subscription={}",
                    destination, durableSubscriptionName, e);
            throw e;
        }
    }
}