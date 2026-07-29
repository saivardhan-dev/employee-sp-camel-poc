package com.poc.emp_sp_camel.aq;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import oracle.jms.AQjmsAdtMessage;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Struct;

public class WfEventTAqMessageConverter implements MessageConverter {

    private static final String ROOT_ELEMENT = "WF_EVENT_T";
    private final DataSource dataSource;

    public WfEventTAqMessageConverter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Object fromMessage(Message message)
            throws JMSException, MessageConversionException {
        try {
            if (message instanceof TextMessage textMessage) {
                return textMessage.getText();
            }

            if (message instanceof AQjmsAdtMessage adtMessage) {
                Object payload = adtMessage.getAdtPayload();

                // Payload is WfEventTOraData (created by WfEventTPayloadFactory)
                if (payload instanceof WfEventTOraData oraData) {
                    return structToXml(oraData.getStruct());
                }

                // Fallback — raw STRUCT
                if (payload instanceof java.sql.Struct struct) {
                    return structToXml((oracle.sql.STRUCT) struct);
                }

                throw new MessageConversionException(
                        "Unsupported ADT payload type: "
                                + (payload == null ? "null"
                                : payload.getClass().getName()));
            }
            throw new MessageConversionException(
                    "Unsupported JMS message type: "
                            + message.getClass().getName());

        } catch (MessageConversionException e) {
            throw e;
        } catch (Exception e) {
            throw new MessageConversionException(
                    "Failed to convert WF_EVENT_T to XML", e);
        }
    }

    @Override
    public Message toMessage(Object object, Session session)
            throws JMSException, MessageConversionException {
        if (object instanceof String text) {
            return session.createTextMessage(text);
        }
        throw new MessageConversionException(
                "Outbound conversion not supported for: "
                        + (object == null ? "null"
                        : object.getClass().getName()));
    }

    private String structToXml(Struct struct) throws SQLException {
        // getAttributes() returns all WF_EVENT_T fields in definition order
        Object[] attrs = struct.getAttributes();

        return "<" + ROOT_ELEMENT + ">"
                + xml("EVENT_NAME",           attrs, 0)
                + xml("EVENT_KEY",            attrs, 1)
                + xml("P_ORG_CODE",           attrs, 2)
                + xml("P_REPROCESS_FLAG",     attrs, 3)
                + xml("P_REPROCESS_ACTION",   attrs, 4)
                + xml("P_CARRIER_CODE",       attrs, 5)
                + xml("P_IB_OB",              attrs, 6)
                + xml("P_TRIP_ID",            attrs, 7)
                + xml("P_DELIVERY_ID",        attrs, 8)
                + xml("P_CUSTOMER_NAME",      attrs, 9)
                + xml("P_CUSTOMER_NUMBER",    attrs, 10)
                + xml("P_SHIP_TO_LOC_ID",     attrs, 11)
                + xml("P_COLLECTION_DATE",    attrs, 12)
                + xml("P_COLLECTION_ORDER",   attrs, 13)
                + xml("P_IBC_ITEM",           attrs, 14)
                + xml("P_NO_OF_IBC",          attrs, 15)
                + xml("P_BOOKING_IN_INSTR",   attrs, 16)
                + xml("P_SHIP_FROM_ORG",      attrs, 17)
                + xml("P_ORGANIZATION_CODE",  attrs, 18)
                + xml("P_ORGANIZATION_ID",    attrs, 19)
                + xml("P_BOOKING_IN_OVERDUE", attrs, 20)
                + xml("P_DEBUG_IN",           attrs, 21)
                + xml("P_SOA_INSTANCE_ID",    attrs, 22)
                + xml("P_INTERFACE_SYSTEM",   attrs, 23)
                + "</" + ROOT_ELEMENT + ">";
    }

    private String xml(String name, Object[] attrs, int index) {
        String value = (attrs != null
                && index < attrs.length
                && attrs[index] != null)
                ? attrs[index].toString().trim() : "";
        return "<" + name + ">" + value + "</" + name + ">";
    }
}