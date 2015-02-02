package com.harunaydin.tomcat.monitoring;

import java.net.InetAddress;
import java.util.Properties;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.realm.RealmBase;
import org.apache.catalina.startup.Embedded;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.IntrospectionUtils;

/**
 * @author placson
 */
public class Tomcat55 {
	protected Logger logger = Logger.getLogger(Tomcat55.class);

	private String catalinaHome = "";
	private Embedded embedded = null;
	private Host host = null;
	private int monitorPort = 8080;
	private boolean useHttps = false;
	private String httpsKeyPass;
	private String httpsKeyStore;
	private String contextPath;
	private String realmType;
	private String realmDigest;
	private String address;

	private RealmBase realmBase;

	public Tomcat55() {

	}

	/**
	 * This method Starts the Tomcat server.
	 */
	public void startTomcat() throws Exception {
		StringBuilder logString = new StringBuilder();
		logString.append("\n\t****************************************************************");
		logString.append("\n\t*****    EmbeddedTomcat is starting                 *****");
		logString.append("\n\t****************************************************************");
		logger.info(logString.toString());

		Engine engine = null;

		// Create an embedded server
		embedded = new Embedded();
		embedded.setCatalinaHome(catalinaHome);

		embedded.setRealm(realmBase);

		if (StringUtils.isNotBlank(realmDigest)) {
			realmBase.setDigest(realmDigest.toUpperCase());
		}

		// Create an engine
		engine = embedded.createEngine();
		engine.setDefaultHost("localhost");
		// Create a default virtual host
		host = embedded.createHost("localhost", catalinaHome + "/webapps");
		engine.addChild(host);

		final Context context = embedded.createContext("/" + address, contextPath);

		context.setReloadable(true);
		host.addChild(context);

		// Install the assembled container hierarchy
		embedded.addEngine(engine);

		Connector httpConnector = null;
		InetAddress address = null;
		try {
			httpConnector = new Connector();
			if (useHttps) {
				httpConnector.setScheme("https");
				httpConnector.setSecure(true);
				httpConnector.setProtocol("SSL");

				// Https settings
				IntrospectionUtils.setProperty(httpConnector, "sslProtocol", "TLS");
				IntrospectionUtils.setProperty(httpConnector, "keypass", httpsKeyPass);
				IntrospectionUtils.setProperty(httpConnector, "keystore", httpsKeyStore);
				IntrospectionUtils.setProperty(httpConnector, "port", "" + monitorPort);

				// Supported ciphers. we eliminate weak cipher and tested via
				// SSLDigger
				final StringBuilder supportedCiphers = new StringBuilder();
				supportedCiphers.append("SSL_RSA_WITH_RC4_128_MD5, SSL_RSA_WITH_RC4_128_SHA, TLS_RSA_WITH_AES_128_CBC_SHA,");
				supportedCiphers.append("TLS_DHE_RSA_WITH_AES_128_CBC_SHA, TLS_DHE_DSS_WITH_AES_128_CBC_SHA,");
				supportedCiphers.append("SSL_RSA_WITH_3DES_EDE_CBC_SHA, SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA, SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA");

				IntrospectionUtils.setProperty(httpConnector, "ciphers", supportedCiphers.toString());

			} else {
				httpConnector.setScheme("http");
				httpConnector.setSecure(false);
				IntrospectionUtils.setProperty(httpConnector, "port", "" + monitorPort);
			}

			address = InetAddress.getLocalHost();
			if (address != null) {
				IntrospectionUtils.setProperty(httpConnector, "address", "" + address);
			}
		} catch (final Exception e) {
			logger.error(e, e);
		}
		httpConnector.setEnableLookups(false);

		embedded.addConnector(httpConnector);
		// Start the embedded server

		logger.info("\n\nStarting embedded tomcat. Monitoring URL : " + httpConnector.getScheme() + "://" + address.getHostName() + ":" + monitorPort + "/"
				+ address + "\n\n");

		try {
			embedded.start();
		} catch (Exception e) {
			// already in use hatasında throw etmeli
			if (e.getMessage().contains("Address already in use")) {
				throw e;
			}
			logger.error(e, e);
		}
	}

	/**
	 * This method Stops the Tomcat server.
	 */
	public void stopTomcat() throws Exception {
		logger.info("Stopping Tomcat");
		// Stop the embedded server
		if (embedded != null) {
			embedded.stop();
		}
		logger.info("Tomcat Stopped");
	}

	public void initialize(final Properties appProps) throws Exception {
		catalinaHome = appProps.getProperty("monitor.web.root", "monitor");
		contextPath = appProps.getProperty("monitor.web.context.path", "monitor/webapps/TestWeb.war");
		monitorPort = Integer.parseInt(appProps.getProperty("monitor.port", "8080").trim());
		httpsKeyPass = appProps.getProperty("monitor.https.keystore.password", "passw0rd").trim();
		httpsKeyStore = appProps.getProperty("monitor.https.keystore");
		useHttps = appProps.getProperty("monitor.use.https", "false").equalsIgnoreCase("true");
		realmType = appProps.getProperty("monitor.realm.type", "memory").trim();
		realmDigest = appProps.getProperty("monitor.realm.digest", "").trim();
		address = appProps.getProperty("monitor.web.address", "").trim();
		final RealmFactory realmFactory = new RealmFactory();
		realmBase = realmFactory.getRealm(appProps);

	}
}
