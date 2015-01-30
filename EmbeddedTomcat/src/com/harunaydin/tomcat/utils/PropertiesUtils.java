package com.harunaydin.tomcat.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class PropertiesUtils {

	private static Logger logger = Logger.getLogger(PropertiesUtils.class);
	private final static String PROPERTIES_FILE_NAME_KEY = "@@PROPERTIES_FILE_NAME@@";

	/**
	 * fileName ismi ile verilen properties dosyasini props nesnesine yukler
	 */
	public static boolean loadProperties(String fileName, Properties props) {
		File f = new File(fileName);
		logger.debug("Loading setings Filename: " + f.getAbsolutePath());
		if (!f.exists()) {
			f = new File(System.getProperty("user.dir") + File.separator + fileName);
		}

		if (f.exists() && !f.isDirectory()) {
			try {
				FileInputStream fis = new FileInputStream(f);
				props.load(fis);

				props.put(PROPERTIES_FILE_NAME_KEY, fileName);
				fis.close();
			} catch (IOException e) {
				logger.error(e, e);
				return false;
			}
		} else {
			logger.fatal(fileName + " : File not exist");
			return false;
		}
		return true;
	}

	/**
	 * This method update properties file.if user password successfully changed, copy new password value to password value and clear new_password value
	 * 
	 * @param passwdPropertyName
	 *            User password property name
	 * @param newPasswdPropertyName
	 *            User new password property name
	 * @param newPasswd
	 *            User new password value
	 * 
	 */
	public static synchronized void updatePropertiesFile(Properties props, String propertyName, String propertyValue) {

		File propsFile = new File(props.getProperty(PROPERTIES_FILE_NAME_KEY));
		logger.info("Updating  properties from file : " + propsFile.getAbsolutePath());

		if (propsFile.exists()) {
			try {

				// Construct the new file that will later be renamed to the
				// original filename.
				File tempFile = new File(propsFile.getAbsolutePath() + ".tmp");

				BufferedReader br = new BufferedReader(new FileReader(propsFile));
				PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

				String line = null;

				// Read from the original file and write to the new
				while ((line = br.readLine()) != null) {
					int equalIndex = line.indexOf("=");

					String currentPropName = "";

					if (equalIndex > 0) {
						currentPropName = line.substring(0, equalIndex).trim();
					}

					if (StringUtils.isNotBlank(currentPropName) && currentPropName.equals(propertyName)) {
						pw.println(propertyName + "=" + propertyValue);
					} else {
						pw.println(line);
					}
					pw.flush();
				}
				pw.close();
				br.close();

				// Delete the original file
				if (!propsFile.delete()) {
					logger.warn("Could not delete file " + propsFile.getAbsolutePath());
				}

				// Rename the new file to the filename the original file had.
				if (!tempFile.renameTo(propsFile)) {
					logger.warn("Could not rename file " + tempFile.getAbsolutePath());
				}

			} catch (Exception e) {
				logger.error(e, e);
			}
		} else {
			logger.fatal("PropertiesFile not found : " + props.getProperty(PROPERTIES_FILE_NAME_KEY));
		}

	}

}