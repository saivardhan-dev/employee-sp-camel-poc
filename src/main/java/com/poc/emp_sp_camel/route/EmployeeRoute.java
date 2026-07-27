package com.poc.emp_sp_camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * EmployeeRoute — Main Camel route for the Oracle AQ to ActiveMQ pipeline.
 *
 * This route implements the Oracle Fusion to Apache Camel migration pattern,
 * replacing Oracle Fusion's Database Adapter and Composite flow with a
 * standard Camel JMS route.
 *
 * Pipeline flow:
 *
 *   Oracle AQ (EMPLOYEE_EVENT_Q)
 *       │
 *       │ JMS listener (Jms component wired to Oracle AQ via OracleAqConfig)
 *       ▼
 *   EmployeeRouteProcessor
 *       │ Reads raw XML payload from AQ message body
 *       │ Extracts JMS MessageID into header
 *       │ Sets dequeueSuccess property
 *       ▼
 *   XSLT (employee-transform.xsl)  — only if dequeueSuccess = true
 *       │ Transforms flat <Employee> XML into structured <EventXML>
 *       │ Adds <Address> nesting around address fields
 *       ▼
 *   EmployeeEnvelopeProcessor
 *       │ Wraps EventXML in final <EmployeeEvent> envelope
 *       │ Adds <Dequeued> status and <MessageId>
 *       ▼
 *   ActiveMQ (employee.output)
 *       │ Publishes final XML message
 *       ▼
 *   Done ✅
 *
 * Error handling:
 *   If EmployeeRouteProcessor fails, dequeueSuccess = false and
 *   EmployeeEnvelopeProcessor builds a Dequeued=false error envelope
 *   which is still published to ActiveMQ for audit/traceability.
 */

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