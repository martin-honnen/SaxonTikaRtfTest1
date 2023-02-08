<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:tika="http://example.com/mf/tika"
                exclude-result-prefixes="#all"
                expand-text="yes">

    <xsl:template match="SPECORMETHOD">
        <rtf-as-xhtml>
            <xsl:sequence select="tika:parse-rtf(.)"/>
        </rtf-as-xhtml>
    </xsl:template>

    <xsl:mode on-no-match="shallow-copy"/>

    <xsl:output indent="yes"/>

    <xsl:template match="/" name="xsl:initial-template">
        <xsl:next-match/>
        <xsl:comment>Run with {system-property('xsl:product-name')} {system-property('xsl:product-version')} {system-property('Q{http://saxon.sf.net/}platform')}</xsl:comment>
    </xsl:template>

</xsl:stylesheet>