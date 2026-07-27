package com.poc.emp_sp_camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRouteProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        // Get JMS MessageID from header
        String msgId = exchange.getIn()
                .getHeader("JMSMessageID", String.class);

        try {
            // Read XML payload directly from AQ — no JSON/Jackson needed
            String xmlBody = exchange.getIn().getBody(String.class);

            // Pass XML straight to XSLT step
            exchange.getIn().setBody(xmlBody);
            exchange.getIn().setHeader("msgId", msgId);
            exchange.setProperty("dequeueSuccess", true);

        } catch (Exception e) {
            exchange.setProperty("dequeueSuccess", false);
            exchange.setProperty("errorMessage", e.getMessage());
            exchange.getIn().setBody(null);
        }
    }
}