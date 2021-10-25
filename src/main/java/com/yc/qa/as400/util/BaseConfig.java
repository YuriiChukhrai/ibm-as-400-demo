package com.yc.qa.as400.util;

import java.util.Properties;

import lombok.extern.log4j.Log4j;

/**
 *
 * @author limit (Yurii Chukhrai)
 */
@Log4j
public final class BaseConfig {

	private BaseConfig() {
		throw new UnsupportedOperationException("Illegal access to private constructor");
	}
	
	/**
	 * System properties are set on the Java command line using the
	 * -Dpropertyname=value syntax. <br>
	 * They can also be added at runtime using System.setProperty(String key, String
	 * value) or via the various System.getProperties().load() methods. <br>
	 * To get a specific system property you can use System.getProperty(String key)
	 * or System.getProperty(String key, String def). <br>
	 * 
	 */
	private static final Properties properties = new Properties();

	static {

		/**
		 * 
		 * Environment variables are set in the OS, e.g. in Linux export
		 * HOME=/Users/myusername or <br>
		 * on Windows SET WINDIR=C:\Windows etc, and, unlike properties, may not be set
		 * at runtime. <br>
		 * To get a specific environment variable you can use System.getenv(String
		 * name). <br>
		 * Returns an unmodifiable string map view of the current system environment.
		 * The environment is a <br>
		 * system-dependent mapping from names to values which is passed from parent to
		 * child processes.
		 */

		properties.putAll(System.getenv());
		properties.putAll(System.getProperties());

		log.info("Environment variables and System properties were upload Successfully.");
	}

	public static String getProperty(final String propKey) {

		if (properties.containsKey(propKey)) {
			log.info(
					String.format("TID [%d] Was loaded property (key) [%s].", Thread.currentThread().getId(), propKey));
		} else {
			log.info(String.format("TID [%d] Property (key) [%s] was NOT found.", Thread.currentThread().getId(),
					propKey));
		}

		return properties.getProperty(propKey);
	}

	public static void setProperty(final String propKey, final String propValue) {

		log.info(String.format("TID [%d] Update property [%s].", Thread.currentThread().getId(), propKey));
		properties.put(propKey, propValue);
	}

	public static synchronized boolean isProperty(final String propKey) {

		boolean status = false;

		if (properties.containsKey(propKey)) {
			status = true;
			log.debug(
					String.format("TID [%d] Was loaded property (key) [%s].", Thread.currentThread().getId(), propKey));
		} else {
			log.warn(String.format("TID [%d] Property (key) [%s] was NOT found.", Thread.currentThread().getId(),
					propKey));
		}

		return status;
	}



	public static class Prop {

		/* IBM AS400 Properties */

		/**
		 * IBM AS400 URI
		 * */
		public static final String URI_AS400 = "uri.as400";

		/**
		 * IBM AS400 Username
		 * */
		public static final String USR_AS400 = "user.as400";

		/**
		 * IBM AS400 User password
		 * */
		public static final String PSW_AS400 = "psw.as400";

		/**
		 * IBM AS400 PORT
		 * */
		public static final String SESSION_HOST_PORT_AS400 = "port.as400";

		/**
		 * IBM AS400 PORT
		 * */
		public static final String SSL_TYPE_AS400 = "ssl.type.as400";


		/**
		 * IBM AS400 PORT
		 * */
		public static final String HEADLESS_MODE_AS400 = "isVisible";
	}
}
