package org.shenxiaoqu.clicker.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertyUtil {
	
	private static final Logger logger  = Logger.getLogger(PropertyUtil.class.getName());
    
	private static Properties clickerProperties = null;
    
    private static final String clickerResources = "conf/rclick.properties";
	
	public static String getProperty(String key) {
		if(clickerProperties == null){
			try {
				clickerProperties = getProperties(clickerResources);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("Cannot open property file: " + clickerResources);
                return null;
            }
		}
	    return clickerProperties.getProperty(key);
	}
	
	public static Integer getPropertyInt(String key) {
		return Integer.parseInt(getProperty(key));
	}
	
	private static Properties getProperties(String resource) throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = new FileInputStream(resource);
		Properties properties = new Properties();
		properties.load(inputStream);
		return properties;
	}
}