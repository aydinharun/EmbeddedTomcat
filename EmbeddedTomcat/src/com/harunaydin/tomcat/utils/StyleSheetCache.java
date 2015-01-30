package com.harunaydin.tomcat.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

/**
 * A utility class that cache XSLT stylesheets in memory
 */

public class StyleSheetCache {

	// map xslt file names to Map Entry Instances
	private static Map cache = new HashMap();

	/**
	 * Flush all cached stylesheets from memory
	 */
	public static synchronized void flushall() {

		cache.clear();
	}

	/**
	 * Flush a specific cached stylesheet from memory
	 */
	public static synchronized void flush(String xsltfileName) {

		cache.remove(xsltfileName);
	}

	/**
	 * Obtain a new transformer instance for the specified XSLT file name a new entry will be added to the cache if this is the first request for the specified
	 * filename
	 * 
	 * @param xsltFileName
	 *            the file name of XSLT stylesheet.
	 * @return a transformation context for the given stylesheet.
	 */
	public static synchronized Transformer newTransformer(String xsltFileName) throws TransformerConfigurationException {

		File xsltFile = new File(xsltFileName);
		// determine when when the file was last modified on disk
		long xslLastModified = xsltFile.lastModified();
		MapEntry entry = (MapEntry) cache.get(xsltFileName);
		if (entry != null) {
			if (xslLastModified > entry.lastModified) {
				entry = null;
			}
		}
		// create a new entry in the cache if necessary
		if (entry == null) {
			Source xslSource = new StreamSource(xsltFile);
			TransformerFactory transFact = TransformerFactory.newInstance();
			Templates templates = transFact.newTemplates(xslSource);
			entry = new MapEntry(xslLastModified, templates);
			cache.put(xsltFileName, entry);
		}
		return entry.templates.newTransformer();
	}

	private StyleSheetCache() {

	}

	/**
	 * this class presents a value in the cache map
	 */
	static class MapEntry {

		long lastModified;

		Templates templates;

		MapEntry(long lastModified, Templates templates) {

			this.lastModified = lastModified;
			this.templates = templates;
		}
	}
}
