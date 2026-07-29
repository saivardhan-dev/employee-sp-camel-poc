<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml"
                indent="yes"
                omit-xml-declaration="yes"/>

    <!--
        Transforms SP OutputParameters XML into SHIPPINGREQUEST.
        Input:  <OutputParameters>
                    <X_WMS_EXT_TBL_OUT><row>...</row></X_WMS_EXT_TBL_OUT>
                    <X_WMS_EMAIL_TBL><row>...</row></X_WMS_EMAIL_TBL>
                </OutputParameters>
        Output: <SHIPPINGREQUEST> with SHIPMENT elements
    -->
    <xsl:template match="/OutputParameters">
        <SHIPPINGREQUEST>
            <xsl:for-each select="X_WMS_EXT_TBL_OUT/row">
                <SHIPMENT>
                    <TRIP_ID>
                        <xsl:value-of select="TRIP_ID"/>
                    </TRIP_ID>
                    <DELIVERY_ID>
                        <xsl:value-of select="DELIVERY_ID"/>
                    </DELIVERY_ID>
                    <CARRIER_CODE>
                        <xsl:value-of select="CARRIER_CODE"/>
                    </CARRIER_CODE>
                    <ORG_CODE>
                        <xsl:value-of select="ORG_CODE"/>
                    </ORG_CODE>
                    <CUSTOMER_NAME>
                        <xsl:value-of select="CUSTOMER_NAME"/>
                    </CUSTOMER_NAME>
                    <CUSTOMER_NUMBER>
                        <xsl:value-of select="CUSTOMER_NUMBER"/>
                    </CUSTOMER_NUMBER>
                    <SHIP_TO_LOC_ID>
                        <xsl:value-of select="SHIP_TO_LOC_ID"/>
                    </SHIP_TO_LOC_ID>
                    <COLLECTION_DATE>
                        <xsl:value-of select="COLLECTION_DATE"/>
                    </COLLECTION_DATE>
                    <COLLECTION_ORDER>
                        <xsl:value-of select="COLLECTION_ORDER"/>
                    </COLLECTION_ORDER>
                    <IBC_ITEM>
                        <xsl:value-of select="IBC_ITEM"/>
                    </IBC_ITEM>
                    <NO_OF_IBC>
                        <xsl:value-of select="NO_OF_IBC"/>
                    </NO_OF_IBC>
                    <SHIP_FROM_ORG>
                        <xsl:value-of select="SHIP_FROM_ORG"/>
                    </SHIP_FROM_ORG>
                    <ORGANIZATION_CODE>
                        <xsl:value-of select="ORGANIZATION_CODE"/>
                    </ORGANIZATION_CODE>
                    <ORGANIZATION_ID>
                        <xsl:value-of select="ORGANIZATION_ID"/>
                    </ORGANIZATION_ID>
                    <INTERFACE_SYSTEM>
                        <xsl:value-of select="INTERFACE_SYSTEM"/>
                    </INTERFACE_SYSTEM>
                    <STATUS_CODE>
                        <xsl:value-of select="STATUS_CODE"/>
                    </STATUS_CODE>
                    <ERROR_MESSAGE>
                        <xsl:value-of select="ERROR_MESSAGE"/>
                    </ERROR_MESSAGE>
                </SHIPMENT>
            </xsl:for-each>
            <xsl:for-each select="X_WMS_EMAIL_TBL/row">
                <EMAIL_NOTIFICATION>
                    <EMAIL_ADDRESS>
                        <xsl:value-of select="EMAIL_ADDRESS"/>
                    </EMAIL_ADDRESS>
                    <EMAIL_SUBJECT>
                        <xsl:value-of select="EMAIL_SUBJECT"/>
                    </EMAIL_SUBJECT>
                    <EMAIL_BODY>
                        <xsl:value-of select="EMAIL_BODY"/>
                    </EMAIL_BODY>
                    <STATUS_CODE>
                        <xsl:value-of select="STATUS_CODE"/>
                    </STATUS_CODE>
                </EMAIL_NOTIFICATION>
            </xsl:for-each>
        </SHIPPINGREQUEST>
    </xsl:template>

</xsl:stylesheet>