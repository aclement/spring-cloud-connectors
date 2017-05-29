package org.springframework.cloud.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.cloud.CloudConnector;
import org.springframework.cloud.CloudException;

/**
 * Environment available to the deployed app.
 *
 * The main purpose of this class is to allow unit-testing of {@link CloudConnector} implementations
 * that rely on environment
 *
 * @author Ramnivas Laddad
 */
public class EnvironmentAccessor {

	private static Logger logger = Logger.getLogger(EnvironmentAccessor.class.getName());
	
	private final static String LOCAL_PROPERTIES_FILE = "localcloud.properties";
	
	Properties localProperties;
	
	public EnvironmentAccessor() {
		// Is there a local file we should be using?
		// Load from classpath?
		File localPropertiesFile = new File(LOCAL_PROPERTIES_FILE);
		if (!localPropertiesFile.exists()) {
			localPropertiesFile = new File(System.getProperty("user.home")+File.separator+LOCAL_PROPERTIES_FILE);
		}
		if (localPropertiesFile.exists() ) {
			logger.log(Level.INFO,"Local properties file found for environment variables: "+localPropertiesFile);
			localProperties = new Properties();
			try {
				localProperties.load(new FileInputStream(localPropertiesFile));
			} catch (IOException e) {
				logger.log(Level.WARNING, "Unable to load local properties file: "+localPropertiesFile, e);
			}
		}
	}

	public Map<String, String> getEnv() {
		return System.getenv();
	}

	public String getEnvValue(String key) {
		String systemValue = System.getenv(key);
		if (systemValue != null) {
			return systemValue;
		}
		if (localProperties != null) {
			String localFileValue = localProperties.getProperty(key);
			if (localFileValue != null) {
				logger.log(Level.INFO, "Using local file value: "+key+"="+localFileValue);
				return localFileValue;
			}
		}
		return System.getenv(key);
	}

	public Properties getSystemProperties() {
	    return System.getProperties();
	}

	public String getSystemProperty(String key) {
		return getSystemProperty(key, null);
	}

	public String getSystemProperty(String key, String def) {
	    return System.getProperty(key, def);
	}

	public String getHost() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException ex) {
			throw new CloudException(ex);
		}
	}
}
