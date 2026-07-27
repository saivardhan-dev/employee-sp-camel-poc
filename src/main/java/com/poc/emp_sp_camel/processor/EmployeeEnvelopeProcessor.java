package com.poc.emp_sp_camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * EmployeeEnvelopeProcessor — Step 3 (final step) of the Camel pipeline.
 *
 * Wraps the XSLT-transformed XML (EventXML) in a final envelope structure
 * before publishing to ActiveMQ. The envelope contains:
 *   - Dequeued   : boolean indicating whether the message was successfully
 *                  dequeued and processed
 *   - MessageId  : the JMS MessageID stamped by Oracle AQ at enqueue time
 *   - EventXML   : the XSLT-transformed employee XML payload
 *
 * Final output structure published to ActiveMQ (employee.output):
 *
 * <EmployeeEvent>
 *     <Dequeued>true</Dequeued>
 *     <MessageId>ID:57557EF2...</MessageId>
 *     <EventXML>
 *         <Employee>
 *             <EmpId>1</EmpId>
 *             <EmpName>John Doe</EmpName>
 *             ...
 *         </Employee>
 *     </EventXML>
 * </EmployeeEvent>
 *
 * If dequeueSuccess = false, the envelope contains Dequeued=false
 * and an Error element inside EventXML with the exception message.
 */

@Component
public class EmployeeEnvelopeProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        Boolean dequeueSuccess = exchange.getProperty(
                "dequeueSuccess", Boolean.class);
        String msgId = exchange.getIn()
                .getHeader("msgId", String.class);
        String finalXml;

        if (Boolean.TRUE.equals(dequeueSuccess)) {
            String eventXml = exchange.getIn().getBody(String.class);

            finalXml = "<EmployeeEvent>"                                    +
                    "<Dequeued>true</Dequeued>"                      +
                    "<MessageId>" + msgId    + "</MessageId>"        +
                    "<EventXML>"  + eventXml + "</EventXML>"         +
                    "</EmployeeEvent>";
        } else {
            String error = exchange.getProperty(
                    "errorMessage", String.class);

            finalXml = "<EmployeeEvent>"                                             +
                    "<Dequeued>false</Dequeued>"                              +
                    "<MessageId>" + msgId + "</MessageId>"                    +
                    "<EventXML><Error>" + error + "</Error></EventXML>"       +
                    "</EmployeeEvent>";
        }

        exchange.getIn().setBody(finalXml);
    }
}