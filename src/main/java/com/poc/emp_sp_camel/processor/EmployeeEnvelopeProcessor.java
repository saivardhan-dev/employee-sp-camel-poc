package com.poc.emp_sp_camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

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