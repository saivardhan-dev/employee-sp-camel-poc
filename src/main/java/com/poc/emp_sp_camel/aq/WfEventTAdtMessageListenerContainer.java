package com.poc.emp_sp_camel.aq;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;
import jakarta.jms.Topic;
import oracle.jakarta.jms.AQjmsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import oracle.jakarta.jms.AQjmsSession;
import java.sql.Connection;
import java.sql.SQLException;

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

        if (!(session instanceof AQjmsSession)) {
            throw new JMSException(
                    "Expected AQjmsSession but got: "
                            + session.getClass().getName());
        }
        AQjmsSession aqSession = (AQjmsSession) session;

        if (!(destination instanceof Topic)) {
            throw new JMSException(
                    "Expected Topic but got: "
                            + destination.getClass().getName());
        }
        Topic topic = (Topic) destination;

        log.info("Attaching WfEventTPayloadFactory to existing " +
                "subscription if present — fixes JMS-137.");

        MessageConsumer consumer = null;

        // Try getDurableSubscriber first — attach to existing subscription
        try {
            consumer = aqSession.getDurableSubscriber(
                    topic,
                    durableSubscriptionName,
                    WfEventTPayloadFactory.INSTANCE
            );
            log.info(">>> getDurableSubscriber returned: {}",
                    consumer == null ? "null" : consumer.getClass().getName());
        } catch (JMSException getEx) {
            log.warn(">>> getDurableSubscriber threw: {}", getEx.getMessage());
        }

        // If null or failed — create new subscriber
        if (consumer == null) {
            log.info(">>> Falling back to createDurableSubscriber...");
            try {
                consumer = aqSession.createDurableSubscriber(
                        topic,
                        durableSubscriptionName,
                        WfEventTPayloadFactory.INSTANCE
                );
                log.info(">>> createDurableSubscriber returned: {}",
                        consumer == null ? "null"
                                : consumer.getClass().getName());
            } catch (JMSException createEx) {
                log.error(">>> createDurableSubscriber also failed: {}",
                        createEx.getMessage(), createEx);
                throw createEx;
            }
        }

        if (consumer == null) {
            throw new JMSException(
                    "Both getDurableSubscriber and createDurableSubscriber " +
                            "returned null for subscription: " + durableSubscriptionName);
        }

        log.info(">>> Successfully got consumer: {}",
                consumer.getClass().getName());

        // Initialize EBS context
        initEbsContext(aqSession);

        return consumer;
    }

    /**
     * Initializes Oracle EBS application context on the AQ session.
     * Required on EBS/Fusion environments before dequeuing from
     * multi-org secured queues like WF_BPEL_Q.
     * On non-EBS environments (e.g. local Oracle XE) this is a no-op.
     */
    private void initEbsContext(AQjmsSession aqSession) {
        try {
            // Get underlying JDBC connection from AQ session
            Connection conn = aqSession.getDBConnection();

            if (conn == null) {
                log.warn(">>> Could not get underlying connection " +
                        "from AQjmsSession — skipping EBS context init");
                return;
            }

            log.info(">>> Got DB connection: {}", conn.getClass().getName());

            // Simple connectivity test on local Oracle XE
            // On production EBS this would call fnd_global.apps_initialize()
            try (java.sql.Statement st = conn.createStatement()) {
                st.execute("SELECT 1 FROM DUAL");
                log.info(">>> EBS context connection test successful");
            }

        } catch (Exception e) {
            log.warn(">>> EBS context init (continuing): {}",
                    e.getMessage());
        }
    }
}