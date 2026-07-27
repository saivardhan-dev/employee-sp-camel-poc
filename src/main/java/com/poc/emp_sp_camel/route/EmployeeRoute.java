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
                .process("employeeRouteProcessor")
                .choice()
                .when(exchangeProperty("dequeueSuccess").isEqualTo(true))
                .to("xslt:employee-transform.xsl")
                .process("employeeEnvelopeProcessor")
                .otherwise()
                .process("employeeEnvelopeProcessor")
                .end()

                .log(">>> Final XML: ${body}")
                .to("activemq:queue:employee.output")
                .log(">>> Message sent to AMQ queue: employee.output");
    }
}