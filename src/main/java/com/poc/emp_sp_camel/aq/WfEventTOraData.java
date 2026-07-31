package com.poc.emp_sp_camel.aq;

import oracle.jdbc.OracleData;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;

public class WfEventTOraData implements OracleData {

    private final Struct struct;

    public WfEventTOraData(Struct struct) {
        this.struct = struct;
    }

    public Struct getStruct() {
        return struct;
    }

    @Override
    public Object toJDBCObject(Connection connection) throws SQLException {
        return struct;
    }
}