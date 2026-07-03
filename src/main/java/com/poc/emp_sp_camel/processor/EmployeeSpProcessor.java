package com.poc.emp_sp_camel.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import oracle.jdbc.OracleTypes;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Component
public class EmployeeSpProcessor implements Processor {

    @Autowired
    private DataSource dataSource;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {

        long empId = 5L;

        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "BEGIN get_employee_with_addresses(?, ?); END;")) {

            cs.setLong(1, empId);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(2);

            Map<String, Object> employee = null;
            List<Map<String, Object>> addresses = new ArrayList<>();

            while (rs.next()) {
                if (employee == null) {
                    employee = new LinkedHashMap<>();
                    employee.put("empId",      rs.getLong("EMP_ID"));
                    employee.put("empName",    rs.getString("EMP_NAME"));
                    employee.put("department", rs.getString("DEPARTMENT"));
                    employee.put("hireDate",   rs.getDate("HIRE_DATE").toString());
                }

                Map<String, Object> address = new LinkedHashMap<>();
                address.put("addressType", rs.getString("ADDRESS_TYPE"));
                address.put("line1",       rs.getString("LINE1"));
                address.put("line2",       rs.getString("LINE2"));
                address.put("city",        rs.getString("CITY"));
                address.put("state",       rs.getString("STATE"));
                address.put("zipCode",     rs.getString("ZIP_CODE"));
                address.put("country",     rs.getString("COUNTRY"));
                addresses.add(address);
            }

            rs.close();

            if (employee == null) {
                exchange.getIn().setBody(
                    "{\"error\": \"Employee not found for id: " + empId + "\"}");
            } else {
                employee.put("addresses", addresses);
                String json = objectMapper.writeValueAsString(employee);
                exchange.getIn().setBody(json);
            }
        }
    }
}