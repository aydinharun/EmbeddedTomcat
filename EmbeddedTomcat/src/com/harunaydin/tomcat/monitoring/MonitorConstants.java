package com.harunaydin.tomcat.monitoring;

public class MonitorConstants {

	public static final String SESSION_USER_TEMP_DIR = "session.user.temp.dir";
	public static final String SESSION_MSG_LIST = "session.message.list";
	// Context variables
	public static final String CONTEXT_EXPORT_ADAPTER_LOOKUPS = "context.export.adapter.lookups";
	public static final String CONTEXT_IMPORT_ADAPTER_LOOKUPS = "context.import.adapter.lookups";
	public static final String CONTEXT_MESSAGE_STATUSES = "context.message.statuses";
	public static final String SESSION_USER_DETAIL = "session.user.detail";
	public static final String REQUEST_USER_TASKS = "request.user.tasks";
	// Monitor
	public static final String monitorVersion = "1.2";
	public static final String monitorBuildDate = "24/12/2012";
	// Log Viewer
	public static boolean logViewerEnabled;
	public static boolean logViewerAutoScroll;
	public static boolean logViewerStartAtBegining;
	public static int logViewerFontSize;
	public static final String logViewerVersion = "1.1";
	public static final String logViewerBuildDate = "24/12/2012";

	// Channel Monitor
	public static boolean channelMonitorEnabled;
	public static boolean channelMonitorAutoScroll;
	public static int channelMonitorFontSize;
	public static final String channelMonitorVersion = "1.1";
	public static final String channelMonitorBuildDate = "24/12/2012";
	public static boolean channelMonitorSendMessageEnabled;
	public static String channelLogFile;
	public static String lastModifiedXmlName;

}
