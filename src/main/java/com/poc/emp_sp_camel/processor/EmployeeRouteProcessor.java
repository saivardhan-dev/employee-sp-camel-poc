package com.poc.emp_sp_camel.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.emp_sp_camel.model.EmployeeEvent;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRouteProcessor implements Processor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {

        String msgId = exchange.getIn()
                .getHeader("JMSMessageID", String.class);

        try {
            // Step 1: Read raw JSON body from AQ
            String jsonBody = exchange.getIn().getBody(String.class);

            // Step 2: Unmarshal JSON → Java EmployeeEvent object
            EmployeeEvent emp = objectMapper.readValue(jsonBody,
                    EmployeeEvent.class);

            // Step 3: Convert Java object → intermediate XML
            String intermediateXml = toXml(emp);

            // Pass to next step (XSLT)
            exchange.getIn().setBody(intermediateXml);
            exchange.getIn().setHeader("msgId", msgId);
            exchange.setProperty("dequeueSuccess", true);

        } catch (Exception e) {
            exchange.setProperty("dequeueSuccess", false);
            exchange.setProperty("errorMessage", e.getMessage());
            exchange.getIn().setBody(null);
        }
    }

    private String toXml(EmployeeEvent emp) {
        return "<Employee>"                                          +
                "<EmpId>"      + emp.getEmpId()      + "</EmpId>"      +
                "<EmpName>"    + emp.getEmpName()    + "</EmpName>"    +
                "<Department>" + emp.getDepartment() + "</Department>" +
                "<HireDate>"   + emp.getHireDate()   + "</HireDate>"   +
                "<Line1>"      + emp.getLine1()      + "</Line1>"      +
                "<City>"       + emp.getCity()       + "</City>"       +
                "<State>"      + emp.getState()      + "</State>"      +
                "<ZipCode>"    + emp.getZipCode()    + "</ZipCode>"    +
                "<Country>"    + emp.getCountry()    + "</Country>"    +
                "</Employee>";
    }
}