package com.newland.sdk.mtypex.module.common.emv;

import android.util.Log;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class CommonUtils {

    private static final String TAG = "CommonUtils";
    private static CommonUtils instance;
    private String sdkVersion = null;
    private Properties sdkProperties;
    private CommonUtils() {
    }

    public static final CommonUtils getInstance() {
        if (instance == null) {
            synchronized (CommonUtils.class) {
                if (instance == null) {
                    instance = new CommonUtils();
                }
            }
        }
        return instance;
    }

    public String getSDKVersion() {
        if (sdkVersion == null)
            initSDKVersion();
        return sdkVersion;
    }

    private void initSDKVersion() {
        try {
            if (sdkProperties == null) {
                Properties p = new Properties();
                URL url = getClass().getClassLoader().getResource("sdk.properties");
                InputStream inputStream = null;
                if(url != null){
                    inputStream = url.openStream();
                }
                if(inputStream == null){
                    inputStream = this.getClass().getClassLoader().getResourceAsStream("sdk.properties");
                }
                if (inputStream == null)
                    return;
                else {
                    p.load(inputStream);
                    sdkProperties = p;
                }
            }
            if (sdkProperties == null)
                return;
            sdkVersion = sdkProperties.getProperty("mesdk.version");
        } catch (Exception e) {
            Log.e(TAG,"[initSDKVersion] failed to init sdk version!", e);
        }
    }
}
