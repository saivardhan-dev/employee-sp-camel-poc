package com.poc.emp_sp_camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        errorHandler(defaultErrorHandler());

        from("Jms:queue:EMPLOYEE_EVENT_Q")
                .routeId("employee-sp-route")
                .log(">>> Event received from Oracle AQ: ${header.JMSMessageID}")

                // Step 1 + 2: JSON → Java object → intermediate XML
                .process("employeeObjProcessor")

                // Step 3: XSLT transform intermediate XML → EventXML
                .to("xslt:employee-transform.xsl")

                // Step 4: Wrap in final envelope
                .process("employeeEnvelopeProcessor")

                .log(">>> Final XML: ${body}")
                .to("activemq:queue:employee.output")
                .log(">>> Message sent to AMQ queue: employee.output");
    }
}
