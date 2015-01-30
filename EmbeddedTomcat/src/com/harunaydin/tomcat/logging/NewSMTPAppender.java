package com.harunaydin.tomcat.logging;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SMTPAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.harunaydin.tomcat.utils.XmlUtils;

/**
 * J32BitSMTPAppender is based on {@link org.apache.log4j.appender.SMTPAppender} so most of the configuration options can be taken from the documentation on
 * that class.
 */
public class NewSMTPAppender extends SMTPAppender {
	/**
	 * Mail template xsl
	 */
	private String mailTemplateFileName = null;
	/**
	 * Mail header
	 */
	private String header;
	/**
	 * Mail footer
	 */
	private String footer;

	/**
	 * if send insterval >0 appender wait specified interval than send another mail. Appender puts recevied event in cyclic buffer
	 */
	private Timer timer = new Timer("J32Bit log4j mail appender", true);

	private TimerTask timerTask;

	/**
	 * Sned mail interval. if its equals 0 send error mail immediately
	 */
	private int sendInterval = 0;
	/**
	 * Next mail send cycle
	 */
	long nextCycle = 0;

	public void setSendInterval(int sendInterval) {
		this.sendInterval = sendInterval;
	}

	public NewSMTPAppender() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.net.SMTPAppender#append(org.apache.log4j.spi.LoggingEvent )
	 */
	@Override
	public void append(LoggingEvent event) {
		if (!checkEntryConditions()) {
			return;
		}

		event.getThreadName();
		event.getNDC();
		event.getMDCCopy();

		if (getLocationInfo()) {
			event.getLocationInformation();
		}

		if (evaluator.isTriggeringEvent(event)) {
			// Add evetn to cyclic evet buffer
			cb.add(event);

			if (sendInterval == 0 || nextCycle < System.currentTimeMillis()) {
				sendBuffer();
			} else {
				if (timerTask == null) {
					timerTask = new TimerTask() {
						@Override
						public void run() {
							timerTask = null;
							sendBuffer();
						}
					};

					long delay = nextCycle - System.currentTimeMillis();
					// Calculate delay
					timer.schedule(timerTask, delay);

				}
			}

		}
	}

	/**
	 * Send the contents of the cyclic buffer as an e-mail message.
	 */
	@Override
	protected void sendBuffer() {
		try {
			nextCycle = System.currentTimeMillis() + sendInterval * 1000L;

			// Create XML according to events
			// Header

			Document xmlDoc = new org.apache.xerces.dom.DocumentImpl();
			Element logElement = xmlDoc.createElement("Log");
			xmlDoc.appendChild(logElement);

			// Header
			Element headerElement = xmlDoc.createElement("Header");
			headerElement.setTextContent(getHeader());
			logElement.appendChild(headerElement);

			// Footer
			if (StringUtils.isNotBlank(footer)) {
				Element footerElement = xmlDoc.createElement("Footer");
				footerElement.setTextContent(footer);
				logElement.appendChild(footerElement);
			}

			Element eventsElement = xmlDoc.createElement("Events");
			logElement.appendChild(eventsElement);

			while (cb.length() > 0) {
				LoggingEvent event = cb.get();

				Element eventElement = xmlDoc.createElement("Event");
				eventsElement.appendChild(eventElement);

				Element messageElement = xmlDoc.createElement("Message");
				messageElement.setTextContent(layout.format(event));
				eventElement.appendChild(messageElement);

				if (layout.ignoresThrowable()) {
					String[] throwableStr = event.getThrowableStrRep();
					if (throwableStr != null) {
						for (String element : throwableStr) {
							Element lineElement = xmlDoc.createElement("Line");
							lineElement.setTextContent(element);
							eventElement.appendChild(lineElement);
						}
					}
				}

			}

			MimeBodyPart bodyPart = new MimeBodyPart();

			String mailContent = null;
			if (StringUtils.isNotBlank(mailTemplateFileName)) {
				File templateFile = new File(mailTemplateFileName);
				if (templateFile.exists()) {
					mailContent = XmlUtils.transformXmlXsl(xmlDoc, mailTemplateFileName);
				} else {
					StringBuilder sbMailContent = new StringBuilder();
					sbMailContent.append("<br/><hr/><br/>Mail Template file not defined or Not found.");
					sbMailContent.append("<br/>Please check log4j.xml configuration file.");
					sbMailContent.append("<br/>XSL mail template path : ").append(templateFile.getAbsolutePath());
					sbMailContent.append("<br/><hr/><br/><br/><br/>");
					sbMailContent.append(XmlUtils.xmlToString(xmlDoc));
					mailContent = sbMailContent.toString();
				}
			}

			// Transform xml with specified mail template and create mail body
			bodyPart.setContent(mailContent, layout.getContentType());
			bodyPart.addHeader("Content-Type", "text/html; charset= UTF-8");

			Multipart mp = new MimeMultipart();
			mp.addBodyPart(bodyPart);
			msg.setContent(mp);

			Transport.send(msg);
		} catch (Exception e) {
			LogLog.error("Error occured while sending e-mail notification.", e);
		}
	}

	private String getHeader() {
		if (StringUtils.isNotBlank(header)) {
			return header;
		} else {
			return getSubject();
		}
	}

	public void setTemplate(String templateFileName) {
		mailTemplateFileName = templateFileName;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

}
