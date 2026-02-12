package com.newland.nsdk.core.external;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.external.setting.ExtSettingsManager;
import com.newland.nsdk.core.external.command.common.ExternalCommonModule;

public class ExtSettingsManagerImpl implements ExtSettingsManager {

    private ExternalCommonModule commonModule;

    private volatile static ExtSettingsManagerImpl instance;

    public static ExtSettingsManagerImpl getInstance() {
        if(instance == null) {
            synchronized (ExtSettingsManagerImpl.class) {
                if(instance == null) {
                    instance = new ExtSettingsManagerImpl();
                }
            }
        }
        return instance;
    }

    private ExtSettingsManagerImpl() {
        commonModule = new ExternalCommonModule();
    }

    @Override
    public void set(String key, String value) throws NSDKException {
        if(key == null || value == null) {
            throw new NSDKIllegalParameterException("key and value should not be null!");
        }

        commonModule.setProperty(key, value);
    }

    @Override
    public String get(String key) throws NSDKException {
        if(key == null) {
            throw new NSDKIllegalParameterException("key should not be null!");
        }
        return commonModule.getProperty(key);
    }
}
