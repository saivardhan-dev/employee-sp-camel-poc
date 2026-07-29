package com.poc.emp_sp_camel;

import com.poc.emp_sp_camel.model.ShippingEventRecordAqJms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Calls Oracle wrapper SP XXSW_WMS_CARRIER_OB_MAIN_SOA.
 * Binds 22 IN parameters (positions 1-22) and reads
 * 2 OUT collection arrays (positions 23-24).
 * Returns output collections marshalled to XML.
 */
@Repository
public class ShippingOrderDaoAqJms {

    private static final Logger log =
            LoggerFactory.getLogger(ShippingOrderDaoAqJms.class);

    private static final String WRAPPER_CALL =
            "BEGIN EMPUSER.XXSW_WMS_CARRIER_OB_MAIN_SOA(" +
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); END;";

    private static final String T_EXT_TBL_OUT =
            "EMPUSER.XXSW_WMS_CARRIER_TBL";

    private static final String T_EMAIL_TBL =
            "EMPUSER.XXSW_WMS_EMAIL_TBL";

    // IN parameter names — order matches SP positions 1..22
    private static final String[] IN_PARAMS = {
            "P_ORG_CODE",           // pos 1
            "P_REPROCESS_FLAG",     // pos 2
            "P_REPROCESS_ACTION",   // pos 3
            "P_CARRIER_CODE",       // pos 4
            "P_IB_OB",              // pos 5
            "P_TRIP_ID",            // pos 6
            "P_DELIVERY_ID",        // pos 7
            "P_CUSTOMER_NAME",      // pos 8
            "P_CUSTOMER_NUMBER",    // pos 9
            "P_SHIP_TO_LOC_ID",     // pos 10
            "P_COLLECTION_DATE",    // pos 11
            "P_COLLECTION_ORDER",   // pos 12
            "P_IBC_ITEM",           // pos 13
            "P_NO_OF_IBC",          // pos 14
            "P_BOOKING_IN_INSTR",   // pos 15
            "P_SHIP_FROM_ORG",      // pos 16
            "P_ORGANIZATION_CODE",  // pos 17
            "P_ORGANIZATION_ID",    // pos 18
            "P_BOOKING_IN_OVERDUE", // pos 19
            "P_DEBUG_IN",           // pos 20
            "P_SOA_INSTANCE_ID",    // pos 21
            "P_INTERFACE_SYSTEM"    // pos 22
    };

    // Numeric params — bound as BigDecimal
    private static final Set<String> NUMERIC = Set.of(
            "P_TRIP_ID",
            "P_DELIVERY_ID",
            "P_SHIP_TO_LOC_ID",
            "P_NO_OF_IBC",
            "P_ORGANIZATION_ID",
            "P_SOA_INSTANCE_ID"
    );

    // Date params — bound as java.sql.Date
    private static final Set<String> DATE_PARAMS = Set.of(
            "P_COLLECTION_DATE"
    );

    private final DataSource dataSource;

    public ShippingOrderDaoAqJms(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Calls the wrapper SP with all IN parameters from the record.
     * Returns OUT collections marshalled to XML.
     */
    public String query(ShippingEventRecordAqJms rec)
            throws SQLException {
        try (Connection con = dataSource.getConnection()) {
            return call(con, rec);
        }
    }

    private String call(Connection con, ShippingEventRecordAqJms rec)
            throws SQLException {

        Map<String, String> params = buildParams(rec);

        try (CallableStatement cs = con.prepareCall(WRAPPER_CALL)) {

            // Bind IN parameters at positions 1..22
            for (int i = 0; i < IN_PARAMS.length; i++) {
                int    pos   = i + 1;
                String name  = IN_PARAMS[i];
                String value = params.get(name);

                log.info("BIND pos={} name={} value=[{}]",
                        pos, name, value);

                if (NUMERIC.contains(name)) {
                    cs.setBigDecimal(pos, toDecimal(value));
                } else if (DATE_PARAMS.contains(name)) {
                    cs.setDate(pos, toDate(value));
                } else {
                    cs.setString(pos, value);
                }
            }

            // Register OUT parameters at positions 23 and 24
            cs.registerOutParameter(23, Types.ARRAY, T_EXT_TBL_OUT);
            cs.registerOutParameter(24, Types.ARRAY, T_EMAIL_TBL);

            cs.execute();

            java.sql.Array extTbl   = cs.getArray(23);
            java.sql.Array emailTbl = cs.getArray(24);

            return buildOutputXml(extTbl, emailTbl);
        }
    }

    /**
     * Maps ShippingEventRecordAqJms fields to IN_PARAMS names.
     */
    private Map<String, String> buildParams(ShippingEventRecordAqJms rec) {
        Map<String, String> p = new LinkedHashMap<>();
        p.put("P_ORG_CODE",           rec.getOrgCode());
        p.put("P_REPROCESS_FLAG",     rec.getReprocessFlag());
        p.put("P_REPROCESS_ACTION",   rec.getReprocessAction());
        p.put("P_CARRIER_CODE",       rec.getCarrierCode());
        p.put("P_IB_OB",              rec.getIbOb());
        p.put("P_TRIP_ID",            rec.getTripId());
        p.put("P_DELIVERY_ID",        rec.getDeliveryId());
        p.put("P_CUSTOMER_NAME",      rec.getCustomerName());
        p.put("P_CUSTOMER_NUMBER",    rec.getCustomerNumber());
        p.put("P_SHIP_TO_LOC_ID",     rec.getShipToLocId());
        p.put("P_COLLECTION_DATE",    rec.getCollectionDate());
        p.put("P_COLLECTION_ORDER",   rec.getCollectionOrder());
        p.put("P_IBC_ITEM",           rec.getIbcItem());
        p.put("P_NO_OF_IBC",          rec.getNoOfIbc());
        p.put("P_BOOKING_IN_INSTR",   rec.getBookingInInstr());
        p.put("P_SHIP_FROM_ORG",      rec.getShipFromOrg());
        p.put("P_ORGANIZATION_CODE",  rec.getOrganizationCode());
        p.put("P_ORGANIZATION_ID",    rec.getOrganizationId());
        p.put("P_BOOKING_IN_OVERDUE", rec.getBookingInOverdue());
        p.put("P_DEBUG_IN",           rec.getDebugIn());
        p.put("P_SOA_INSTANCE_ID",    rec.getSoaInstanceId());
        p.put("P_INTERFACE_SYSTEM",   rec.getInterfaceSystem());
        return p;
    }

    /**
     * Marshals OUT Array collections to XML.
     *
     * <OutputParameters>
     *     <X_WMS_EXT_TBL_OUT>
     *         <row>...</row>
     *     </X_WMS_EXT_TBL_OUT>
     *     <X_WMS_EMAIL_TBL>
     *         <row>...</row>
     *     </X_WMS_EMAIL_TBL>
     * </OutputParameters>
     */
    private String buildOutputXml(java.sql.Array extTbl,
                                  java.sql.Array emailTbl)
            throws SQLException {

        StringBuilder xml = new StringBuilder("<OutputParameters>");

        // X_WMS_EXT_TBL_OUT
        xml.append("<X_WMS_EXT_TBL_OUT>");
        if (extTbl != null) {
            Object[] rows = (Object[]) extTbl.getArray();
            for (Object row : rows) {
                Object[] attrs =
                        ((oracle.sql.STRUCT) row).getAttributes();
                xml.append("<row>")
                        .append(elem("TRIP_ID",           attrs, 0))
                        .append(elem("DELIVERY_ID",        attrs, 1))
                        .append(elem("CARRIER_CODE",       attrs, 2))
                        .append(elem("ORG_CODE",           attrs, 3))
                        .append(elem("CUSTOMER_NAME",      attrs, 4))
                        .append(elem("CUSTOMER_NUMBER",    attrs, 5))
                        .append(elem("SHIP_TO_LOC_ID",     attrs, 6))
                        .append(elem("COLLECTION_DATE",    attrs, 7))
                        .append(elem("COLLECTION_ORDER",   attrs, 8))
                        .append(elem("IBC_ITEM",           attrs, 9))
                        .append(elem("NO_OF_IBC",          attrs, 10))
                        .append(elem("SHIP_FROM_ORG",      attrs, 11))
                        .append(elem("ORGANIZATION_CODE",  attrs, 12))
                        .append(elem("ORGANIZATION_ID",    attrs, 13))
                        .append(elem("INTERFACE_SYSTEM",   attrs, 14))
                        .append(elem("STATUS_CODE",        attrs, 15))
                        .append(elem("ERROR_MESSAGE",      attrs, 16))
                        .append("</row>");
            }
        }
        xml.append("</X_WMS_EXT_TBL_OUT>");

        // X_WMS_EMAIL_TBL
        xml.append("<X_WMS_EMAIL_TBL>");
        if (emailTbl != null) {
            Object[] rows = (Object[]) emailTbl.getArray();
            for (Object row : rows) {
                Object[] attrs =
                        ((oracle.sql.STRUCT) row).getAttributes();
                xml.append("<row>")
                        .append(elem("EMAIL_ADDRESS", attrs, 0))
                        .append(elem("EMAIL_SUBJECT", attrs, 1))
                        .append(elem("EMAIL_BODY",    attrs, 2))
                        .append(elem("STATUS_CODE",   attrs, 3))
                        .append("</row>");
            }
        }
        xml.append("</X_WMS_EMAIL_TBL>");

        xml.append("</OutputParameters>");
        return xml.toString();
    }

    private String elem(String name, Object[] attrs, int idx) {
        String v = (attrs != null
                && idx < attrs.length
                && attrs[idx] != null)
                ? attrs[idx].toString().trim() : "";
        return "<" + name + ">" + v + "</" + name + ">";
    }

    private BigDecimal toDecimal(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return new BigDecimal(s.trim());
        } catch (NumberFormatException e) {
            log.warn("Failed to parse decimal: {}", s);
            return null;
        }
    }

    private java.sql.Date toDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            // Handle "2026-07-28 00:00:00.0" or "2026-07-28" formats
            String datePart = s.trim().split(" ")[0];
            return java.sql.Date.valueOf(datePart);
        } catch (Exception e) {
            log.warn("Failed to parse date: {}", s);
            return null;
        }
    }

    public record EbsContext(
            String user,
            String responsibility,
            String orgId) {}
}