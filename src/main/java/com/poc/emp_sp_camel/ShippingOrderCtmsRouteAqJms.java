package com.poc.emp_sp_camel;

import com.poc.emp_sp_camel.model.ShippingEventRecordAqJms;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Main Camel route — consumes CTMS shipping events from Oracle AQ
 * WF_BPEL_Q topic and processes them through the shipping order flow.
 */
@Component
public class ShippingOrderCtmsRouteAqJms extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(
            ShippingOrderCtmsRouteAqJms.class);

    private static final String INPUT_XSLT =
            "xslt:xslt/Xform_receiveInput_To_QueryShippingOrderDBService_Input.xsl";

    private static final String OUTPUT_XSLT =
            "xslt:xslt/TransformEdiCarrierIMessage.xsl";

    private static final String EXPECTED_EVENT = "CTMS_EVENT";

    private final ShippingEventXmlMapperAqJms mapper;
    private final ShippingOrderDaoAqJms dao;

    @Value("${oracle.aq.subscriber:ORA_EDICUSTOMSCTMSSUBSCRIBER}")
    private String subscriber;

    public ShippingOrderCtmsRouteAqJms(
            ShippingEventXmlMapperAqJms mapper,
            ShippingOrderDaoAqJms dao) {
        this.mapper = mapper;
        this.dao    = dao;
    }

    @Override
    public void configure() throws Exception {

        // Global error handler
        onException(Exception.class)
                .handled(false)
                .useOriginalMessage()
                .process(exchange -> {
                    Throwable t = exchange.getProperty(
                            Exchange.EXCEPTION_CAUGHT, Throwable.class);
                    exchange.getIn().setHeader("auditStatus", "E");
                    exchange.getIn().setHeader("auditError",
                            t != null
                                    ? truncate(t.getMessage(), 3000)
                                    : "unknown error");
                })
                .end();

        // ── Main route ────────────────────────────────────────────────────
        from("Jms:topic:EMPUSER.WF_BPEL_Q" +
                "?consumerType=Custom" +
                "&subscriptionDurable=true" +
                "&durableSubscriptionName=" + subscriber +
                "&clientId=emp-sp-camel-ctms")
                .routeId("integration-shippingOrderCtmsAqJms")
                .log(">>> Event received from WF_BPEL_Q")

                // Step 1: Convert body to String (XML from WfEventTAqMessageConverter)
                .convertBodyTo(String.class)

                // Step 2: Parse WF_EVENT_T XML → set headers
                .process(this::parseAndStamp)
                .log(">>> eventName=${header.eventName} " +
                        "deliveryId=${header.deliveryId} " +
                        "isTest=${header.isTest}")

                // Step 3: Validate event name
                .choice()
                .when(header("eventName").isNotEqualTo(EXPECTED_EVENT))
                .log(LoggingLevel.WARN,
                        ">>> Not a CTMS_EVENT — stopping. " +
                                "eventName=${header.eventName}")
                .setHeader("auditStatus", constant("E"))
                .setHeader("auditError",
                        simple("Unexpected event: ${header.eventName}"))
                .to("activemq:queue:shipping.dlq")
                .stop()
                .when(header("isTest").isEqualTo("Y"))
                .log(LoggingLevel.WARN,
                        ">>> isTest=Y — stopping.")
                .stop()
                .end()

                // Step 4: Transform WF_EVENT_T XML → InputParameters XML
                .to(INPUT_XSLT)
                .process(exchange -> exchange.setProperty(
                        "inputParamsXml",
                        exchange.getIn().getBody(String.class)))
                .log(">>> INPUT_PARAMS:\n${exchangeProperty.inputParamsXml}")

                // Step 5: Call wrapper SP → OutputParameters XML
                .process(this::callQuery)
                .log(">>> OUTPUT_PARAMS:\n${body}")

                // Step 6: Transform OutputParameters → SHIPPINGREQUEST
                .to(OUTPUT_XSLT)
                .log(">>> SHIPPING_REQUEST:\n${body}")

                // Step 7: Count SHIPMENT elements
                .process(this::countShipments)

                // Step 8: Route based on shipment count
                .choice()
                .when(simple("${header.shipmentCount} > 0"))
                .log(">>> Shipments found: ${header.shipmentCount} " +
                        "— publishing to shipping.output")
                .to("direct:shp-aq-jms-publish")
                .setHeader("auditStatus", constant("S"))
                .otherwise()
                .log(LoggingLevel.WARN,
                        ">>> No shipments found for " +
                                "DELIVERY_ID=${header.deliveryId}")
                .setHeader("auditStatus", constant("E"))
                .setHeader("auditError",
                        constant("No Records exist in Oracle " +
                                "for the combination given"))
                .to("activemq:queue:shipping.dlq")
                .end()
                .log(">>> ShippingOrderCtmsAqJms done. " +
                        "DELIVERY_ID=${header.deliveryId} " +
                        "shipments=${header.shipmentCount}");

        // ── Publish route ─────────────────────────────────────────────────
        from("direct:shp-aq-jms-publish")
                .routeId("integration-shippingOrderCtmsAqJms-publish")
                .convertBodyTo(String.class)
                .setHeader(Exchange.HTTP_METHOD,  constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/xml"))
                .log(">>> Publishing SHIPPINGREQUEST " +
                        "for DELIVERY_ID=${header.deliveryId}")
                .to("activemq:queue:shipping.output")
                .log(">>> Published SHIPPINGREQUEST " +
                        "for DELIVERY_ID=${header.deliveryId}");
    }

    // ── Private helpers ───────────────────────────────────────────────────

    /**
     * Parses WF_EVENT_T XML body → ShippingEventRecordAqJms
     * and stamps headers on the exchange.
     */
    private void parseAndStamp(Exchange exchange) throws Exception {
        String xml = exchange.getIn().getBody(String.class);
        ShippingEventRecordAqJms rec;

        try {
            rec = mapper.parse(xml);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not parse WF_EVENT_T: " + e.getMessage(), e);
        }

        exchange.setProperty("record", rec);
        exchange.getIn().setHeader("eventName",    rec.getEventName());
        exchange.getIn().setHeader("eventKey",     rec.getEventKey());
        exchange.getIn().setHeader("isTest",
                rec.isTestEvent() ? "Y" : "N");
        exchange.getIn().setHeader("deliveryId",   rec.getDeliveryId());
        exchange.getIn().setHeader("reprocessFlag",rec.getReprocessFlag());
        exchange.getIn().setHeader("environment",  rec.getFromAgentSystem());

        if (rec.getDeliveryId() != null
                && !rec.getDeliveryId().isBlank()) {
            exchange.getIn().setHeader("correlationId", rec.getDeliveryId());
        }
    }

    /**
     * Calls the wrapper SP via ShippingOrderDaoAqJms.
     * Body becomes the OutputParameters XML.
     */
    private void callQuery(Exchange exchange) throws Exception {
        ShippingEventRecordAqJms rec = exchange.getProperty(
                "record", ShippingEventRecordAqJms.class);

        try {
            String outputXml = dao.query(rec);
            exchange.getIn().setBody(outputXml);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Shipping query procedure failed: " + e.getMessage(), e);
        }
    }

    /**
     * Counts SHIPMENT elements in the SHIPPINGREQUEST body.
     * Sets header shipmentCount.
     */
    private void countShipments(Exchange exchange) {
        String body  = exchange.getIn().getBody(String.class);
        int    count = 0;

        try {
            var dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            var doc = dbf.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(
                            body.getBytes(StandardCharsets.UTF_8)));
            count = doc.getElementsByTagNameNS("*", "SHIPMENT")
                    .getLength();
        } catch (Exception e) {
            log.warn("Could not count SHIPMENT elements: {}",
                    e.getMessage());
        }

        exchange.getIn().setHeader("shipmentCount", count);
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }
}