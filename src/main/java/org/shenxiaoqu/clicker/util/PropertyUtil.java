package org.shenxiaoqu.clicker.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertyUtil {
	
	private static final Logger log  = Logger.getLogger(PropertyUtil.class.getName());
    
	private static Properties clickerProperties = null;
    
    private static final String clickerResources = "conf/rclicker.properties";

	public static String getProperty(String key) {
		if(clickerProperties == null){
			try {
				clickerProperties = getProperties(clickerResources);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Cannot open property file: " + clickerResources);
                return null;
            }
		}
        try {
            return new String(clickerProperties.getProperty(key).getBytes("ISO8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("Unsupported encoding. ");
            return null;
        }
    }
	
	public static Integer getPropertyInt(String key) {
		return Integer.parseInt(getProperty(key));
	}
	
	private static Properties getProperties(String resource) throws IOException {
        ClassLoader loader = PropertyUtil.class.getClassLoader();
		InputStream inputStream = loader.getResourceAsStream(resource);
		Properties properties = new Properties();
		properties.load(inputStream);
		return properties;
	}
}