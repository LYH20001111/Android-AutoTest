package com.newland.sdk.me.utils;

import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtils {
    private static DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger(PropertiesUtils.class);
    private static Map<String, PropertiesUtils> propertiesUtilMap = new HashMap<String, PropertiesUtils>();
    private String propFileName = null;
    //    private static String propFileName = "/data/share/SDK_EXT_PINPAD/COMMParam.properties";
    public static final String EXT_PARAM = "EXT_PARAM";

    public static final PropertiesUtils getInstance(String fileAlias, String filePath) {
        PropertiesUtils propertiesUtils = propertiesUtilMap.get(fileAlias);
        if (null == propertiesUtils) {
            synchronized (deviceLogger) {
                if (null == propertiesUtils) {
                    propertiesUtils = new PropertiesUtils();
                    propertiesUtils.setPropFileName(filePath);
                    propertiesUtilMap.put(fileAlias, propertiesUtils);
                }
                File file = new File(filePath);
                File tracesDir = file.getParentFile();
                if (!tracesDir.exists()) {
                    tracesDir.mkdirs();
                    tracesDir.setWritable(true, false);
                    tracesDir.setExecutable(true, false);
                    tracesDir.setReadable(true, false);
                }
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                        file.setWritable(true, false);
                        file.setExecutable(true, false);
                        file.setReadable(true, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return propertiesUtils;
    }

    public String getValue(String key) {
        FileInputStream fi = null;
        Properties propObj = null;
        try {
            fi = new FileInputStream(propFileName);
            propObj = new Properties();
            propObj.load(fi);
            if (null != propObj)
                return (String) propObj.get(key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean setProp(String key, String value) {
        OutputStream out = null;
        InputStream fi = null;
        try {
            fi = new FileInputStream(propFileName);
            Properties propObj = new Properties();
            propObj.load(fi);
            out = new FileOutputStream(propFileName);
            propObj.setProperty(key, value);
            propObj.store(out, "set prop:[baurate]:" + key + "[portType]:" + value);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                fi.close();
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean removeProp(String key) {
        OutputStream out = null;
        InputStream fi = null;
        try {
            fi = new FileInputStream(propFileName);
            Properties propObj = new Properties();
            propObj.load(fi);
            out = new FileOutputStream(propFileName);
            propObj.remove(key);

            propObj.store(out, "set prop:[baurate]:" + key);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                fi.close();
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    private void setPropFileName(String propFileName) {
        this.propFileName = propFileName;
    }
}
