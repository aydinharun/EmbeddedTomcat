<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:xalan="http://xml.apache.org/xslt">
	<xsl:output indent="yes" encoding="utf-8" method="html"
		standalone="yes" />

	<!-- ******* -->
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<title>
					<xsl:value-of select="//Header" disable-output-escaping="yes"/>
				</title>
			</head>
			<style>
				.column, .columns {
					display: inline;
					margin: 40px 10px 0;
					position: relative;
				}
				.mt30 {
					margin-top: 30px !important;
				}

				h1.gdl-page-title {
					border-bottom: 1px solid;
					font-size: 20px;
					margin-bottom: 0;
					padding-bottom: 6px;
				}

				div.gdl-page-content {
					margin-top: 35px;
				}

				div.message-box-wrapper {
					border: 1px solid;
					margin-bottom: 20px;
					padding: 20px;
				}
				div.message-box-title {
					font-size: 14px;
					font-weight: bold;
					margin-top: 2px;
				}
				div.message-box-content {
					margin-top: 12px;
				}
				div.message-box-wrapper.red {
					background-color: #FFDCDD;
					border-color: #E9B9BB;
					color: #713A3C;
				}
				div.message-box-wrapper.red .message-box-title {
					color: #8D4345;
				}
				div.message-box-wrapper.yellow {
					background-color: #FFFFE0;
					border-color: #E1DB8B;
					color: #4D4D39;
				}
				div.message-box-wrapper.yellow .message-box-title {
					color: #737357;
				}
				div.message-box-wrapper.blue {
					background-color: #C2E1EE;
					border-color: #8EC3D9;
					color: #3D5058;
				}
				div.message-box-wrapper.blue .message-box-title {
					color: #475D66;
				}
				div.message-box-wrapper.green {
					background-color: #E1FFD9;
					border-color: #BADEB1;
					color: #435B3D;
				}
				div.message-box-wrapper.green .message-box-title {
					color: #526D4B;
				}

			</style>
			<body>


				<div class="columns mt30">
					<h1 class="gdl-page-title gdl-divider gdl-title title-color">
						<xsl:value-of select="//Header" disable-output-escaping="yes"/>
					</h1>
					<div class="gdl-page-content">

						<xsl:for-each select="//Event">
							<div class="message-box-wrapper red">
								<div class="message-box-title">Error Details</div>
								<div class="message-box-content">
									<xsl:value-of select="Message" disable-output-escaping="yes"/>
									<br/>
									<xsl:if test="count(Line) &gt; 0">
										<br/>
										<b>Exception Details :</b> 
										<br/>
										<xsl:for-each select="Line">
											<br />
											<xsl:value-of select="current()" disable-output-escaping="yes"/>
										</xsl:for-each>		
									</xsl:if>								
								</div>
							</div>
						</xsl:for-each>
						
						<div class="message-box-wrapper green">
							<div class="message-box-title">Monitoring Page</div>
							<div class="message-box-content">
								<a href="http://127.0.0.1:10080/embededtomcat/index.jsp">Embeded Tomcat</a>
							</div>
						</div>
						
						
						<div class="message-box-wrapper yellow">
							<div class="message-box-title">Please contact</div>
							<div class="message-box-content">
									32Bit Bilg. Hizm. Ltd. Şti.<br/>
									Telephone: +90 (262) 646 50 11<br/>
									email: destek@32bit.com.tr <br/>
									website: <a href="http://www.32bit.com.tr">www.32bit.com.tr</a><br/>
							</div>
						</div>
						
						
						<div class="gdl-code">
							<xsl:value-of select="//Footer" disable-output-escaping="yes"/>
							<br/><br/><br/>
						</div>
					</div>
				</div>

			</body>
		</html>
	</xsl:template>


</xsl:stylesheet>