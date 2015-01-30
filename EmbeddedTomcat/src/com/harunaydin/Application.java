package com.harunaydin;

public class Application {
	private final String name = "EmbeddedTomcat";
	private final String version = "1.0";
	private final String buildDate = "14/01/2015 09:50:21";

	public String getNameAndVersion() {
		return name + " v" + version;
	}

	public String getFullDescription() {
		return name + " - Version : " + version + " - Build Date : " + buildDate;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getBuildDate() {
		return buildDate;
	}
}
