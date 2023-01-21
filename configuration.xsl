<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:target="http://www.esei.uvigo.es/dai/hybridserver" >
    <xsl:template match="/">
        <html>
            <head>
                <title>Configuration</title>
            </head>
            <body>
                <h1>Configuration</h1>
                <xsl:apply-templates select="target:configuration/target:connections"/>
                <xsl:apply-templates select="target:configuration/target:database"/>
                <xsl:apply-templates select="target:configuration/target:servers"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="target:connections">
        <h2>Connections</h2>
        <ul>
            <li>HTTP: <xsl:value-of select="target:http"/></li>
            <li>Web Service: <xsl:value-of select="target:webservice"/></li>
            <li>Number of Clients: <xsl:value-of select="target:numClients"/></li>
        </ul>
    </xsl:template>

    <xsl:template match="target:database">
        <h2>Database</h2>
        <ul>
            <li>User: <xsl:value-of select="target:user"/></li>
            <li>Password: <xsl:value-of select="target:password"/></li>
            <li>URL: <xsl:value-of select="target:url"/></li>
        </ul>
    </xsl:template>

    <xsl:template match="target:servers">
        <h2>Servers</h2>
        <ul>
            <xsl:for-each select="server">
                <li>
                    <xsl:value-of select="@name"/> - 
                    WSDL: <xsl:value-of select="@wsdl"/> -
                    Namespace: <xsl:value-of select="@namespace"/> -
                    Servicio: <xsl:value-of select="@service"/> -
                    Dirección HTTP: <xsl:value-of select="@httpAddress"/>
                </li>
            </xsl:for-each>
        </ul>
    </xsl:template>
</xsl:stylesheet >