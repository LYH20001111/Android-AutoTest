package com.newland.nsdkdemo.internal.utils;

import java.lang.reflect.Method;

public class SystemPropertyUtil {
	
	public static String getProperty(String key, String defaultValue) {    
	    String value = defaultValue;
	    try {
	        Class<?> c = Class.forName("android.os.SystemProperties");
	        Method get = c.getMethod("get", String.class, String.class);
	        value = (String)(get.invoke(c, key, defaultValue));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return value;
	    }
	    return value;
	}
	
	public static void setProperty(String key, String value) {
	    try {
	        Class<?> c = Class.forName("android.os.SystemProperties");
	        Method set = c.getMethod("set", String.class, String.class);
	        set.invoke(c, key, value);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
