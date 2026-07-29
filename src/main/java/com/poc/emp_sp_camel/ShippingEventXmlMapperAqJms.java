package com.poc.emp_sp_camel;

import com.poc.emp_sp_camel.model.ShippingEventRecordAqJms;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Parses WF_EVENT_T XML into ShippingEventRecordAqJms.
 * Input XML is produced by WfEventTAqMessageConverter.
 */
@Component
public class ShippingEventXmlMapperAqJms {

    public ShippingEventRecordAqJms parse(String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder()
                .parse(new ByteArrayInputStream(
                        xml.getBytes(StandardCharsets.UTF_8)));
        Element root = doc.getDocumentElement();

        ShippingEventRecordAqJms r = new ShippingEventRecordAqJms();
        r.setRawXml(xml);

        // Core event fields
        r.setEventName(text(root, "EVENT_NAME"));
        r.setEventKey(text(root, "EVENT_KEY"));

        // Shipping parameters
        r.setOrgCode(text(root, "P_ORG_CODE"));
        r.setReprocessFlag(text(root, "P_REPROCESS_FLAG"));
        r.setReprocessAction(text(root, "P_REPROCESS_ACTION"));
        r.setCarrierCode(text(root, "P_CARRIER_CODE"));
        r.setIbOb(text(root, "P_IB_OB"));
        r.setTripId(text(root, "P_TRIP_ID"));
        r.setDeliveryId(text(root, "P_DELIVERY_ID"));
        r.setCustomerName(text(root, "P_CUSTOMER_NAME"));
        r.setCustomerNumber(text(root, "P_CUSTOMER_NUMBER"));
        r.setShipToLocId(text(root, "P_SHIP_TO_LOC_ID"));
        r.setCollectionDate(text(root, "P_COLLECTION_DATE"));
        r.setCollectionOrder(text(root, "P_COLLECTION_ORDER"));
        r.setIbcItem(text(root, "P_IBC_ITEM"));
        r.setNoOfIbc(text(root, "P_NO_OF_IBC"));
        r.setBookingInInstr(text(root, "P_BOOKING_IN_INSTR"));
        r.setShipFromOrg(text(root, "P_SHIP_FROM_ORG"));
        r.setOrganizationCode(text(root, "P_ORGANIZATION_CODE"));
        r.setOrganizationId(text(root, "P_ORGANIZATION_ID"));
        r.setBookingInOverdue(text(root, "P_BOOKING_IN_OVERDUE"));
        r.setDebugIn(text(root, "P_DEBUG_IN"));
        r.setSoaInstanceId(text(root, "P_SOA_INSTANCE_ID"));
        r.setInterfaceSystem(text(root, "P_INTERFACE_SYSTEM"));

        return r;
    }

    /**
     * Extracts text content of first matching element
     * regardless of namespace.
     */
    private String text(Element parent, String localName) {
        NodeList nl = parent.getElementsByTagNameNS("*", localName);
        if (nl.getLength() > 0) {
            String t = nl.item(0).getTextContent();
            return t == null ? null : t.trim();
        }
        // Fallback — no namespace
        nl = parent.getElementsByTagName(localName);
        if (nl.getLength() > 0) {
            String t = nl.item(0).getTextContent();
            return t == null ? null : t.trim();
        }
        return null;
    }
}