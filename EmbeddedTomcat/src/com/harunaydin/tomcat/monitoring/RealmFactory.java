package com.harunaydin.tomcat.monitoring;

import java.util.Properties;

import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.realm.RealmBase;
import org.apache.log4j.Logger;

import com.harunaydin.tomcat.utils.PropertiesUtils;

public class RealmFactory {
	private static final String REALM_TYPE_DB = "db";
	private static final String REALM_TYPE_LDAP = "ldap";
	private static final String REALM_TYPE_MEMORY = "memory";
	private static Logger logger = Logger.getLogger(RealmFactory.class);
	final PropertiesUtils propertiesUtils = new PropertiesUtils();

	public RealmBase getRealm(final Properties props) throws Exception {
		RealmBase realmBase = null;

		final String realmType = props.getProperty("monitor.realm.type", "memory").trim();

		logger.info("Monitoring Realm Type : " + realmType);

		if (realmType.equalsIgnoreCase(REALM_TYPE_MEMORY)) {
			realmBase = new MemoryRealm();
		} else {
			throw new Exception("Unknown realm type. realmType : " + realmType);
		}

		return realmBase;
	}

}
