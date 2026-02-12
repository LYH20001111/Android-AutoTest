package com.newland.nsdk.core.external.command;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.common.Version;

public class ExternalCommandConfig {
    private static final String TAG = "ExternalCommandConfig";
    public static final String MODEL_ME51P = "ME51P";
    public static final String MODEL_ME30SU = "ME30SU";
    public static final String MODEL_SP100 = "SP100";
    public static final String MODEL_P300 = "P300";
    /**
     * 指令类型
     */
    private ExternalCommandType commandType = ExternalCommandType.NDK;
    /**
     * 指令集版本号
     */
    private String version;

    private boolean needUpdate;

    private String model;

    public ExternalCommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(ExternalCommandType commandType) {
        this.commandType = commandType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isNeedUpdate() {
        return needUpdate;
    }

    public void setNeedUpdate(boolean needUpdate) {
        this.needUpdate = needUpdate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public static ExternalCommandConfig create(String versionStr, String model) throws NSDKException {
        if (versionStr == null || versionStr.isEmpty()) {
            throw new NSDKException("Version shall not be null or empty.");
        }

        ExternalCommandConfig config = new ExternalCommandConfig();
        config.setVersion(versionStr);
        Version version = Version.getVersion(versionStr);
        if (versionStr.startsWith("V08")) {
            // ME51P
            config.setCommandType(ExternalCommandType.NAPI);
            config.setModel(MODEL_ME51P);
        } else if (versionStr.startsWith("V05")) {
            // ME51P
            if (version.isLower(5, 0, 5)) {
                config.setNeedUpdate(true);
                LogUtils.d(TAG, String.format("Current ME51P version is %s, required to be >= 05.00.05.", versionStr));
            }
            config.setModel(MODEL_ME51P);
        } else if (versionStr.startsWith("V07")) {
            // ME30SU
            config.setCommandType(ExternalCommandType.NAPI);
            if (version.isLower(7, 0, 2)) {
                config.setNeedUpdate(true);
                LogUtils.d(TAG, String.format("Current ME30SU version is %s, required to be >= 07.00.02.", versionStr));
            }
            config.setModel(MODEL_ME30SU);
        } else if (versionStr.startsWith("V04")) {
            // SP100
            if (version.isLower(4, 0, 5)) {
                config.setNeedUpdate(true);
                LogUtils.d(TAG, String.format("Current SP100 version is %s, required to be >= 04.00.05.", versionStr));
            }
            config.setModel(MODEL_SP100);
        } else if (MODEL_P300.equalsIgnoreCase(model)) {
            config.setNeedUpdate(false);
            config.setCommandType(ExternalCommandType.NAPI);
            config.setModel(MODEL_P300);
        } else {
            config.setNeedUpdate(true);
            LogUtils.d(TAG, String.format("Unsupported version: %s", versionStr));
        }

        return config;
    }
}
