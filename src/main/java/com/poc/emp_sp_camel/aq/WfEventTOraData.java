package com.poc.emp_sp_camel.aq;

import oracle.sql.Datum;
import oracle.sql.ORAData;
import oracle.sql.STRUCT;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ORAData wrapper for WF_EVENT_T STRUCT.
 * Returned by WfEventTPayloadFactory.create() for each dequeued message.
 * Passed as AQjmsAdtMessage payload — retrieved via getAdtPayload().
 */
public class WfEventTOraData implements ORAData {

    private final STRUCT struct;

    public WfEventTOraData(STRUCT struct) {
        this.struct = struct;
    }

    public STRUCT getStruct() {
        return struct;
    }

    @Override
    public Datum toDatum(Connection connection) throws SQLException {
        return struct;
    }
}