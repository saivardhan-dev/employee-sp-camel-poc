package com.poc.emp_sp_camel.aq;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import oracle.jakarta.jms.AQjmsSession;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.util.StringUtils;

public class WfEventTAqDestinationResolver implements DestinationResolver {

    private final String schema;

    public WfEventTAqDestinationResolver(String schema) {
        this.schema = schema;
    }

    @Override
    public Destination resolveDestinationName(Session session,
                                              String destinationName,
                                              boolean pubSubDomain)
            throws JMSException {

        if (!(session instanceof AQjmsSession)) {
            if (pubSubDomain) {
                return session.createTopic(destinationName);
            }
            return session.createQueue(destinationName);
        }
        AQjmsSession aqSession = (AQjmsSession) session;

        String queueName = unqualifiedName(destinationName);
        String owner     = owner(destinationName);

        if (pubSubDomain) {
            return aqSession.getTopic(owner, queueName);
        }
        return aqSession.getQueue(owner, queueName);
    }

    private String owner(String destinationName) {
        int dot = destinationName.indexOf('.');
        if (dot > 0) return destinationName.substring(0, dot);
        return StringUtils.hasText(schema) ? schema : null;
    }

    private String unqualifiedName(String destinationName) {
        int dot = destinationName.indexOf('.');
        int len = destinationName.length();
        if (dot > 0 && dot + 1 < len) {
            return destinationName.substring(dot + 1);
        }
        return destinationName;
    }
}