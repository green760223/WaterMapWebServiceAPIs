package tw.com.util;

import java.io.IOException;
import java.util.Properties;


public class PropertyLoader 
{
	public static Properties props = new Properties();
		
	static {
		
		try {
			props.load(PropertyLoader.class.getResourceAsStream("/db.properties"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

    }
	
}