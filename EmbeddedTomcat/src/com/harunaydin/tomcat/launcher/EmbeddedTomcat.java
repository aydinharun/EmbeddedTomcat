package com.harunaydin.tomcat.launcher;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.harunaydin.Application;
import com.harunaydin.tomcat.monitoring.Tomcat55;
import com.harunaydin.tomcat.utils.PropertiesUtils;

public class EmbeddedTomcat {
	private static Logger logger = Logger.getLogger(EmbeddedTomcat.class);

	public static Application application = new Application();
	private static EmbeddedTomcat instance = null;
	private final Properties appProps = new Properties();

	private final long launchedAt = System.currentTimeMillis();

	private Tomcat55 tomcat = null;

	private boolean serviceStopped = true;

	public static void main(final String[] args) {
		// Add shutdown hook for gracefully shutdown
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				getInstance().stopService();
			}
		});

		startService(args);
	}

	public static void startService(final String args[]) {

		// Set application properties file , if it's not specified application
		// try to find and use "EmbeddedTomcat.properties" as default

		String appPropertiesFileName = "EmbeddedTomcat.properties";

		if (args.length > 0 && StringUtils.isNotBlank(args[0])) {
			// Properties file specified, we use this parameter
			appPropertiesFileName = args[0];
		}

		// Configure log4j
		if (args.length > 1 && StringUtils.isNotBlank(args[1])) {
			DOMConfigurator.configure(args[1]);
		}

		final StringBuilder logString = new StringBuilder();
		logString.append("\n\n\n\n");
		logString.append("\n\t\t******************************************************************************************");
		logString.append("\n\t\t***** " + application.getFullDescription() + " Starting....");
		logString.append("\n\t\t******************************************************************************************");
		logString.append("\n\n");
		logger.info(logString.toString());

		getInstance().start(appPropertiesFileName);

	}

	private void start(final String appPropertiesFileName) {
		serviceStopped = false;

		if (getInstance().initialize(appPropertiesFileName)) {
			try {

				powerOn();
				while (!serviceStopped) {
					synchronized (this) {
						printMemoryUsage();
						// 10DK bekle. notify gelince devam ediyor.
						this.wait(10 * 60 * 1000);
					}
				}
				powerOff();

			} catch (final Exception e) {
				logger.fatal(e, e);
			}
		} else {
			logger.fatal("***** " + application.getFullDescription() + " couldn't initialized....");
		}
	}

	public static void stopService(String[] args) {
		getInstance().stopService();
	}

	/**
	 * Stop this service instance
	 */
	public void stopService() {
		serviceStopped = true;
		synchronized (this) {
			notify();
		}
	}

	public void powerOn() throws Exception {
		serviceStopped = false;
		startTomcat();
	}

	private void startTomcat() throws Exception {
		logger.info("Starting web server...");
		tomcat = new Tomcat55();
		tomcat.initialize(appProps);
		tomcat.startTomcat();
		logger.info("Web server started.");
	}

	public void powerOff() throws Exception {

		logger.fatal("EmbedetTomcat is shutting down...");

		serviceStopped = true;

		if (tomcat != null) {
			tomcat.stopTomcat();
		}

		// Waiting 3 sec for sending fatal email
		Thread.sleep(3000);
	}

	private boolean initialize(final String propertiesFileName) {
		try {
			logger.info("Initializing....");

			if (!PropertiesUtils.loadProperties(propertiesFileName, appProps)) {
				return false;
			}
		} catch (final Exception e) {
			logger.fatal(e, e);
			return false;
		}
		return true;
	}

	public static EmbeddedTomcat getInstance() {
		if (instance == null) {
			instance = new EmbeddedTomcat();
		}
		return instance;
	}

	/**
	 * Print memory usage to log file.
	 */
	private void printMemoryUsage() {
		if (logger.isDebugEnabled()) {
			final long max = Runtime.getRuntime().maxMemory() / 1024;
			final long total = Runtime.getRuntime().totalMemory() / 1024;
			final long free = Runtime.getRuntime().freeMemory() / 1024;
			final long used = total - free;

			// Build log string
			final StringBuilder logString = new StringBuilder("\n\tMax Memory : ").append(String.valueOf(max)).append(" kb.");
			logString.append("\n\tTotal Memory : ").append(String.valueOf(total)).append(" kb.");
			logString.append("\n\tUsed Memory : ").append(String.valueOf(used)).append(" kb.");
			logString.append("\n\tFree Memory : ").append(String.valueOf(free)).append(" kb.");

			logger.debug(logString.toString());
		}
	}

	public long getLaunchedAt() {
		return launchedAt;
	}

}
