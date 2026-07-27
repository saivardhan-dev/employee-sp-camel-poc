package com.poc.emp_sp_camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * EmployeeRouteProcessor — Step 1 of the Camel pipeline.
 *
 * Reads the raw XML payload dequeued from Oracle AQ (EMPLOYEE_EVENT_Q)
 * via the JMS listener. The payload is an XML representation of an
 * employee record enqueued by the Oracle stored procedure
 * (enqueue_all_employees). No transformation is performed here —
 * the XML body is passed as-is to the next step (XSLT transformation).
 *
 * Sets the following for downstream processors:
 *   - Exchange body     : raw XML string from AQ
 *   - Header "msgId"    : JMS MessageID from the AQ message
 *   - Property "dequeueSuccess" : true if successful, false if exception
 *   - Property "errorMessage"   : error details if dequeueSuccess = false
 */

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