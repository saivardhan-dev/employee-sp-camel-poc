package com.poc.emp_sp_camel.model;

/**
 * POJO representing a shipping event dequeued from Oracle AQ WF_BPEL_Q.
 * Populated by ShippingEventXmlMapperAqJms from WF_EVENT_T XML.
 */
public class ShippingEventRecordAqJms {

    private String eventName;
    private String eventKey;
    private String correlationId;
    private String fromAgentSystem;
    private String deliveryId;
    private String reprocessFlag;
    private String orgCode;
    private String reprocessAction;
    private String carrierCode;
    private String ibOb;
    private String tripId;
    private String customerName;
    private String customerNumber;
    private String shipToLocId;
    private String collectionDate;
    private String collectionOrder;
    private String ibcItem;
    private String noOfIbc;
    private String bookingInInstr;
    private String shipFromOrg;
    private String organizationCode;
    private String organizationId;
    private String bookingInOverdue;
    private String debugIn;
    private String soaInstanceId;
    private String interfaceSystem;

    // Raw XML body from WF_EVENT_T — kept for audit/debugging
    private String rawXml;

    // ── Getters and Setters ───────────────────────────────────────────────

    public String getEventName()                        { return eventName; }
    public void setEventName(String v)                  { this.eventName = v; }

    public String getEventKey()                         { return eventKey; }
    public void setEventKey(String v)                   { this.eventKey = v; }

    public String getCorrelationId()                    { return correlationId; }
    public void setCorrelationId(String v)              { this.correlationId = v; }

    public String getFromAgentSystem()                  { return fromAgentSystem; }
    public void setFromAgentSystem(String v)            { this.fromAgentSystem = v; }

    public String getDeliveryId()                       { return deliveryId; }
    public void setDeliveryId(String v)                 { this.deliveryId = v; }

    public String getReprocessFlag()                    { return reprocessFlag; }
    public void setReprocessFlag(String v)              { this.reprocessFlag = v; }

    public String getOrgCode()                          { return orgCode; }
    public void setOrgCode(String v)                    { this.orgCode = v; }

    public String getReprocessAction()                  { return reprocessAction; }
    public void setReprocessAction(String v)            { this.reprocessAction = v; }

    public String getCarrierCode()                      { return carrierCode; }
    public void setCarrierCode(String v)                { this.carrierCode = v; }

    public String getIbOb()                             { return ibOb; }
    public void setIbOb(String v)                       { this.ibOb = v; }

    public String getTripId()                           { return tripId; }
    public void setTripId(String v)                     { this.tripId = v; }

    public String getCustomerName()                     { return customerName; }
    public void setCustomerName(String v)               { this.customerName = v; }

    public String getCustomerNumber()                   { return customerNumber; }
    public void setCustomerNumber(String v)             { this.customerNumber = v; }

    public String getShipToLocId()                      { return shipToLocId; }
    public void setShipToLocId(String v)                { this.shipToLocId = v; }

    public String getCollectionDate()                   { return collectionDate; }
    public void setCollectionDate(String v)             { this.collectionDate = v; }

    public String getCollectionOrder()                  { return collectionOrder; }
    public void setCollectionOrder(String v)            { this.collectionOrder = v; }

    public String getIbcItem()                          { return ibcItem; }
    public void setIbcItem(String v)                    { this.ibcItem = v; }

    public String getNoOfIbc()                          { return noOfIbc; }
    public void setNoOfIbc(String v)                    { this.noOfIbc = v; }

    public String getBookingInInstr()                   { return bookingInInstr; }
    public void setBookingInInstr(String v)             { this.bookingInInstr = v; }

    public String getShipFromOrg()                      { return shipFromOrg; }
    public void setShipFromOrg(String v)                { this.shipFromOrg = v; }

    public String getOrganizationCode()                 { return organizationCode; }
    public void setOrganizationCode(String v)           { this.organizationCode = v; }

    public String getOrganizationId()                   { return organizationId; }
    public void setOrganizationId(String v)             { this.organizationId = v; }

    public String getBookingInOverdue()                 { return bookingInOverdue; }
    public void setBookingInOverdue(String v)           { this.bookingInOverdue = v; }

    public String getDebugIn()                          { return debugIn; }
    public void setDebugIn(String v)                    { this.debugIn = v; }

    public String getSoaInstanceId()                    { return soaInstanceId; }
    public void setSoaInstanceId(String v)              { this.soaInstanceId = v; }

    public String getInterfaceSystem()                  { return interfaceSystem; }
    public void setInterfaceSystem(String v)            { this.interfaceSystem = v; }

    public String getRawXml()                           { return rawXml; }
    public void setRawXml(String v)                     { this.rawXml = v; }

    // Checks if this is a test event
    public boolean isTestEvent() {
        return "Y".equalsIgnoreCase(reprocessFlag);
    }

    @Override
    public String toString() {
        return "ShippingEventRecordAqJms{" +
                "eventName='"  + eventName  + '\'' +
                ", tripId='"   + tripId     + '\'' +
                ", deliveryId="+ deliveryId +
                ", carrier='"  + carrierCode+ '\'' +
                '}';
    }
}