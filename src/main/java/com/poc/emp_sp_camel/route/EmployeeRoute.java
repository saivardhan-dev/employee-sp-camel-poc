package com.poc.emp_sp_camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("timer:employeeSync?period=10000&repeatCount=1")
            .routeId("employee-sp-route")
            .log(">>> Calling stored procedure...")
            .process("employeeSpProcessor")
            .log(">>> Result: ${body}")
            .to("activemq:queue:employee.output")
            .log(">>> Message sent to AMQ queue: employee.output");
    }
}