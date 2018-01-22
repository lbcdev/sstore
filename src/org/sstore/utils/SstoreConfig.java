package org.sstore.utils;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class SstoreConfig {

	private static Configurations configs = new Configurations();
	private static Configuration config;

	public SstoreConfig(String configPath) {
		try {
			config = configs.properties(new File(configPath));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

	}

	public int getInteger(String key) {
		return config.getInt(key);
	}
	
	public String getProp(String key) {
		return config.getString(key);
	}

	public boolean getBoolean(String key) {
		return config.getBoolean(key);
	}
	
	public String toString() {
		return config.toString();
	}
}
