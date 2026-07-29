package com.poc.emp_sp_camel.aq;

import oracle.sql.Datum;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.STRUCT;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Implements ORADataFactory — the correct Oracle interface for ADT
 * payload factories in createDurableSubscriber().
 * Fixes JMS-137 and JMS-222.
 *
 * Oracle calls create(Datum, int) for each dequeued WF_EVENT_T message.
 * We wrap the STRUCT in WfEventTOraData for downstream processing.
 */
public class WfEventTPayloadFactory implements ORADataFactory {

    public static final WfEventTPayloadFactory INSTANCE =
            new WfEventTPayloadFactory();

    private WfEventTPayloadFactory() {}

    /**
     * Called by Oracle AQ JMS for each dequeued WF_EVENT_T message.
     * datum is the raw Oracle STRUCT containing WF_EVENT_T attributes.
     */
    @Override
    public ORAData create(Datum datum, int sqlType) throws SQLException {
        if (datum instanceof STRUCT struct) {
            return new WfEventTOraData(struct);
        }
        return new WfEventTOraData(null);
    }
}