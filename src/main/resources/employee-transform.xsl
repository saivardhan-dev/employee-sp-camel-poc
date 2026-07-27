<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>

    <xsl:template match="/Employee">
        <Employee>
            <EmpId><xsl:value-of select="EmpId"/></EmpId>
            <EmpName><xsl:value-of select="EmpName"/></EmpName>
            <Department><xsl:value-of select="Department"/></Department>
            <HireDate><xsl:value-of select="HireDate"/></HireDate>
            <Address>
                <Line1><xsl:value-of select="Line1"/></Line1>
                <City><xsl:value-of select="City"/></City>
                <State><xsl:value-of select="State"/></State>
                <ZipCode><xsl:value-of select="ZipCode"/></ZipCode>
                <Country><xsl:value-of select="Country"/></Country>
            </Address>
        </Employee>
    </xsl:template>

</xsl:stylesheet>