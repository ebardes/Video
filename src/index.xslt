<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

	<xsl:output method="html" indent="yes" encoding="utf-8" />

	<xsl:template match="/">
		<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
		<html>
			<head>
				<link rel="stylesheet" type="text/css" href="/static/style.css" />
			</head>
			<body>
				<h1>Media Assets</h1>
				<xsl:apply-templates select="/config/groups/group" />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="group">
		<h4>
			<xsl:text>Group </xsl:text>
			<xsl:value-of select="id" /> :
			<xsl:value-of select="description" />
		</h4>
		<xsl:for-each select="items/entry/value">
			<div class="slot">
				<div class="title">
				<xsl:text>Slot </xsl:text>
				<xsl:value-of select="id" />
				</div>
				<div class="title">
				<xsl:value-of select="description" />
				</div>
				<div class="title">
				Length:	<xsl:value-of select="length" />
				</div>
				<div class="preview">
				<xsl:if test="@xsi:type = 'image'">
				<img src="{substring(reference, string-length(/config/workDirectory)+6)}" />
				</xsl:if>
				<xsl:if test="@xsi:type = 'video'">
				video clip
				</xsl:if>
				</div>
			</div>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>