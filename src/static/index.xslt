<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

	<xsl:output method="html" indent="yes" encoding="utf-8" />

	<xsl:template match="/">
		<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
		<html>
			<head>
				<link rel="stylesheet" type="text/css" href="/static/style.css" />
				<script src="/static/jq.js"></script>
				<script src="/static/video.js"></script>
			</head>
			<body>
				<h3>System Info</h3>
				<div class="protocol">Protocol: <xsl:value-of select="/config/protocol" /> - Universe: <xsl:value-of select="/config/universe"/></div>
				<xsl:for-each select="/config/layers/layer">
					<div class="layer"><b><xsl:value-of select="personality"/></b> layer at <b><xsl:value-of select="/config/universe"/>/<xsl:value-of select="address"/></b></div>
				</xsl:for-each>
				
				<h1>Media Assets</h1>
				<xsl:apply-templates select="/config/groups/group" />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="group[id = 0]">
		<h4>Built In Content: Group 0</h4>
		<xsl:for-each select="items/entry/value">
			<div class="slot">
				<div class="title head">
					<xsl:text>Slot </xsl:text>
					<xsl:value-of select="id" />
				</div>
				<div class="title">
					<b><xsl:value-of select="description" /></b>
				</div>
				<div class="preview">
					<xsl:if test="@xsi:type = 'image'">
					<img src="{substring(page, string-length(/config/workDirectory)+7)}" />
					</xsl:if>
					<xsl:if test="@xsi:type = 'video'">
					<img src="{substring(page, string-length(/config/workDirectory)+7)}-thumbnail.png" />
					</xsl:if>
				</div>
			</div>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="group[id &gt; 0]">
		<h4>
			<xsl:text>Group </xsl:text>
			<xsl:value-of select="id" /> :
			<xsl:value-of select="description" />
		</h4>
		<div>
		<xsl:for-each select="items/entry/value">
			<div class="slot drop" id="replace/{../../../id}/{id}">
				<div class="title head">
					<xsl:text>Slot </xsl:text>
					<xsl:value-of select="id" />
				</div>
				<div class="title">
					<b><xsl:value-of select="description" /></b>
				</div>
				<div class="title">
					<xsl:text>Length: </xsl:text>
					<b>
					<xsl:choose>
						<xsl:when test="length &gt; (960 * 1024 * 1024)">
							<xsl:value-of select="format-number((length div (1024 * 1024 * 1024)), '#,##0.0')" />
							<xsl:text>&#160;GB</xsl:text>
						</xsl:when>
						<xsl:when test="length &gt; (960 * 1024)">
							<xsl:value-of select="format-number((length div (1024 * 1024)), '#,##0.0')" />
							<xsl:text>&#160;MB</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="format-number((length div 1024), '#,##0')" />
							<xsl:text>&#160;KB</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
					</b>
				</div>
				<div class="title">
					<xsl:value-of select="datestring" />
				</div>
				<div class="preview">
					<xsl:if test="@xsi:type = 'image'">
					<img src="{substring(page, string-length(/config/workDirectory)+1)}" />
					</xsl:if>
					<xsl:if test="@xsi:type = 'video'">
					<img src="{substring(page, string-length(/config/workDirectory)+1)}-thumbnail.png" />
					</xsl:if>
				</div>
				<xsl:if test="../../../id &gt; 0">
					<div class="title remove">[remove]</div>
				</xsl:if>				
			</div>
		</xsl:for-each>
		<div id="add/{id}/0" class="slot drop add">
			<div class="title head">New</div>
			<div class="progress"></div>
		</div>
		</div>
	</xsl:template>
</xsl:stylesheet>
