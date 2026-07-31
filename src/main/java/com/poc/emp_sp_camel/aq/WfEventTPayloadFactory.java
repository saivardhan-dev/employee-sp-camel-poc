package com.poc.emp_sp_camel.aq;

import oracle.jdbc.OracleData;
import oracle.jdbc.OracleDataFactory;

import java.sql.SQLException;
import java.sql.Struct;

public class WfEventTPayloadFactory implements OracleDataFactory {

    public static final WfEventTPayloadFactory INSTANCE =
            new WfEventTPayloadFactory();

    private WfEventTPayloadFactory() {}

    @Override
    public OracleData create(Object datum, int sqlType) throws SQLException {
        if (datum instanceof Struct struct) {
            return new WfEventTOraData(struct);
        }
        return new WfEventTOraData(null);
    }
}