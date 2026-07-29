<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml"
                indent="yes"
                omit-xml-declaration="yes"/>

    <!--
        Transforms WF_EVENT_T XML into SP input parameters XML.
        Input:  <WF_EVENT_T> with all shipping fields
        Output: <InputParameters> with named parameter elements
                matching IN_PARAMS order in ShippingOrderDaoAqJms
    -->
    <xsl:template match="/WF_EVENT_T">
        <InputParameters>
            <P_ORG_CODE>
                <xsl:value-of select="P_ORG_CODE"/>
            </P_ORG_CODE>
            <P_REPROCESS_FLAG>
                <xsl:value-of select="P_REPROCESS_FLAG"/>
            </P_REPROCESS_FLAG>
            <P_REPROCESS_ACTION>
                <xsl:value-of select="P_REPROCESS_ACTION"/>
            </P_REPROCESS_ACTION>
            <P_CARRIER_CODE>
                <xsl:value-of select="P_CARRIER_CODE"/>
            </P_CARRIER_CODE>
            <P_IB_OB>
                <xsl:value-of select="P_IB_OB"/>
            </P_IB_OB>
            <P_TRIP_ID>
                <xsl:value-of select="P_TRIP_ID"/>
            </P_TRIP_ID>
            <P_DELIVERY_ID>
                <xsl:value-of select="P_DELIVERY_ID"/>
            </P_DELIVERY_ID>
            <P_CUSTOMER_NAME>
                <xsl:value-of select="P_CUSTOMER_NAME"/>
            </P_CUSTOMER_NAME>
            <P_CUSTOMER_NUMBER>
                <xsl:value-of select="P_CUSTOMER_NUMBER"/>
            </P_CUSTOMER_NUMBER>
            <P_SHIP_TO_LOC_ID>
                <xsl:value-of select="P_SHIP_TO_LOC_ID"/>
            </P_SHIP_TO_LOC_ID>
            <P_COLLECTION_DATE>
                <xsl:value-of select="P_COLLECTION_DATE"/>
            </P_COLLECTION_DATE>
            <P_COLLECTION_ORDER>
                <xsl:value-of select="P_COLLECTION_ORDER"/>
            </P_COLLECTION_ORDER>
            <P_IBC_ITEM>
                <xsl:value-of select="P_IBC_ITEM"/>
            </P_IBC_ITEM>
            <P_NO_OF_IBC>
                <xsl:value-of select="P_NO_OF_IBC"/>
            </P_NO_OF_IBC>
            <P_BOOKING_IN_INSTRUCTIONS>
                <xsl:value-of select="P_BOOKING_IN_INSTR"/>
            </P_BOOKING_IN_INSTRUCTIONS>
            <P_SHIP_FROM_ORG>
                <xsl:value-of select="P_SHIP_FROM_ORG"/>
            </P_SHIP_FROM_ORG>
            <P_ORGANIZATION_CODE>
                <xsl:value-of select="P_ORGANIZATION_CODE"/>
            </P_ORGANIZATION_CODE>
            <P_ORGANIZATION_ID>
                <xsl:value-of select="P_ORGANIZATION_ID"/>
            </P_ORGANIZATION_ID>
            <P_BOOKING_IN_OVERDUE>
                <xsl:value-of select="P_BOOKING_IN_OVERDUE"/>
            </P_BOOKING_IN_OVERDUE>
            <P_DEBUG_IN>
                <xsl:value-of select="P_DEBUG_IN"/>
            </P_DEBUG_IN>
            <P_SOA_INSTANCE_ID>
                <xsl:value-of select="P_SOA_INSTANCE_ID"/>
            </P_SOA_INSTANCE_ID>
            <P_INTERFACE_SYSTEM>
                <xsl:value-of select="P_INTERFACE_SYSTEM"/>
            </P_INTERFACE_SYSTEM>
        </InputParameters>
    </xsl:template>

</xsl:stylesheet>