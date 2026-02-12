package com.newland.sdk.me.module.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.newland.SettingsManager;
import android.newland.content.NlContext;
import android.newland.os.NlBuild;
import android.newland.security.CertificateInfo;
import android.newland.telephony.TelephonyManager;

import com.newland.sdk.module.settings.ApnUtil;
import com.newland.sdk.module.settings.InfoItem;
import com.newland.sdk.module.settings.NavigationKey;
import com.newland.sdk.module.settings.SettingsItems;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.module.settings.SettingsModule;

import java.security.cert.X509Certificate;
import java.util.Arrays;

public class MESettings extends AbstractModule implements SettingsModule {
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("MESettings");
    private SettingsManager settingsManager;
    private TelephonyManager teleManager;
    private Context context;

    @SuppressLint("WrongConstant")
    public MESettings(AbstractDevice owner, Context context) {
        super(owner);
        this.context = context;
        settingsManager = (SettingsManager) context.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
        teleManager = new TelephonyManager(context);

    }

    @Override
    public boolean isStandardModule() {
        return true;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return ModuleType.SETTINGS;
    }

    @Override
    public String getExModuleType() {
        return null;
    }

    @Override
    public boolean setScreenBrightness(int value) {
        deviceLogger.debug("[setScreenBrightness], value:" + value);
        boolean rslt = settingsManager.setScreenBrightness(value);
        deviceLogger.debug("[setScreenBrightness] rslt:" + rslt);
        return rslt;
    }

//    @Override
//    public void setLauncher(LaunchType launchType, String packageName) {
//        deviceLogger.debug("setLauncher, launchType:" + launchType + ", packageName:" + packageName);
//        if (launchType == LaunchType.DEFAULT) {
//            settingsManager.setLauncher(packageName);
//        } else if (launchType == LaunchType.TEMP) {
//            settingsManager.setDefaultHome(packageName);
//        }
//    }

    @Override
    public void setSettingsItemsVisible(SettingsItems[] settingsItems, boolean isVisible) {
        deviceLogger.debug("[setSettingsItemsVisible], settingsItems:" + (settingsItems == null ? "null" : Arrays.toString(settingsItems)) + ", isVisible:" + isVisible);
        if (settingsItems == null) {
            return;
        }
        int visible = 0;
        // 0 visible, 1 invisible
        if (isVisible) {
            visible = 0;
        } else {
            visible = 1;
        }
        for (SettingsItems items : settingsItems) {
            if (items == SettingsItems.STORAGE) {
                settingsManager.setSettingStorageDispley(visible);
            } else if (items == SettingsItems.BATTERY) {
                settingsManager.setSettingBatteryDispley(visible);
            } else if (items == SettingsItems.HOME) {
                settingsManager.setSettingHomeDispley(visible);
            } else if (items == SettingsItems.BACKUP_RESET) {
                settingsManager.setSettingPrivacyDispley(visible);
            }
//            else if (items == SettingsItems.APPS) {
//                settingsManager.setSettingAppDisplay(visible);
//            }
            else if (items == SettingsItems.DATA_USAGE) {
                settingsManager.setSettingDataUsageDispley(visible);
            } else if (items == SettingsItems.ACCESSIBILITY) {
                settingsManager.setSettingAccessibilitySettingsDispley(visible);
            } else if (items == SettingsItems.DEVELOPER_OPTIONS) {
                settingsManager.setSettingDevelopmentSettingsDispley(visible);
            } else if (items == SettingsItems.LOCATION) {
                settingsManager.setSettingLocationSettingsDispley(visible);
            } else if (items == SettingsItems.SECURITY) {
                settingsManager.setSettingSecuritySettingsDispley(visible);
            } else if (items == SettingsItems.PRINT) {
                settingsManager.setSettingPrintSettingsDispley(visible);
            } else if (items == SettingsItems.VPN) {
                settingsManager.setSettingVpnDispley(visible);
            } else if (items == SettingsItems.SCREEN_LOCK) {
                settingsManager.setSettingLockScreenDisplay(visible);
            }
        }
    }

    @Override
    public void setBatteryPercentVisible(boolean isVisible) {
        deviceLogger.debug("[setBatteryPercentVisible], isVisible:" + isVisible);
        settingsManager.setShowBatteryPercent(isVisible);
    }

    @Override
    public boolean setStatusBarDropDown(boolean isDrop) {
        deviceLogger.debug("[setStatusBarDropDown], isDrop:" + isDrop);
        // 0 drop, 1 no
        int drop = 0;
        if (isDrop) {
            drop = 0;
        } else {
            drop = 1;
        }
        boolean rslt = settingsManager.setStatusBarEnabled(drop);
        deviceLogger.debug("[setStatusBarDropDown] rslt:" + rslt);
        return rslt;
    }

    @Override
    public boolean setNavigationKeyValid(NavigationKey navigationKey, boolean isValid) {
        deviceLogger.debug("[setNavigationKeyValid], navigationKey:" + navigationKey + ", isValid:" + isValid);
        boolean rslt = true;
        if (navigationKey == NavigationKey.HOME) {
            rslt = settingsManager.setHomeKeyEnabled(isValid);
        }
//        else if (navigationKey == NavigationKey.RECENTS_KEY) {
//            rslt = settingsManager.setAppSwitchKeyEnabled(isValid);
//        }
        deviceLogger.debug("[setNavigationKeyValid] rslt:" + rslt);
        return rslt;
    }

//    @Override
//    public boolean setNavigationMode(NavigationMode navigationMode) {
//        deviceLogger.debug("setNavigationMode, navigationMode:" + navigationMode);
//        int mode = 0;
//        if (navigationMode == NavigationMode.LEFT) {
//            mode = 0;
//        } else if (navigationMode == NavigationMode.RIGHT) {
//            mode = 1;
//        } else if (navigationMode == NavigationMode.HIDE) {
//            mode = 2;
//        }
//
//        boolean rslt = settingsManager.relayoutNavigationBar(mode);
//        deviceLogger.debug("rslt:" + rslt);
//        return rslt;
//    }

    @Override
    public ApnUtil getApnUtil() {
        return MEApnUtil.getInstance(context);
    }

    @Override
    public void setMobileDataValid(boolean isValid) {
        deviceLogger.debug("[setMobileDataValid], isValid:" + isValid);
        teleManager.setMobileDataEnabled(isValid);
    }

    @Override
    public boolean getMobileDataStatus() {
        deviceLogger.debug("[getMobileDataStatus]");
        boolean rslt = teleManager.getMobileDataEnabled();
        deviceLogger.debug("[getMobileDataStatus] rslt:" + rslt);
        return rslt;
    }

    @Override
    public X509Certificate getCertificateInfo() {
        deviceLogger.debug("[getCertificateInfo]");
        CertificateInfo certificateInfo = new CertificateInfo(context);
        return certificateInfo.getCertificateInfo();
    }

    @Override
    public String getInfo(InfoItem infoItem) {
        deviceLogger.debug("[getInfo]");
        String info = "";
        if (infoItem == InfoItem.IMEI) {
            info = teleManager.getImei();
        } else if (infoItem == InfoItem.MEID) {
            info = teleManager.getMeid();
        } else if (infoItem == InfoItem.FIRMWARE) {
            info = NlBuild.VERSION.NL_FIRMWARE; // v2.2.29
        } else if (infoItem == InfoItem.FIRMWARE_ID) {
            info = NlBuild.VERSION.FIRMWARE_VERSION;// 01_01_01000001
        } else if (infoItem == InfoItem.HARDWARE_ID) {
            info = NlBuild.VERSION.NL_HARDWARE_ID;
        } else if (infoItem == InfoItem.HARDWARE_CONFIG) {
            info = NlBuild.VERSION.NL_HARDWARE_CONFIG;
        } else if (infoItem == InfoItem.MODEL) {
            info = NlBuild.VERSION.MODEL;
        } else if (infoItem == InfoItem.SERIAL_NUMBER) {
            info = NlBuild.VERSION.SERIAL_NUMBER;
        } else if (infoItem == InfoItem.BASEBAND) {
            info = NlBuild.VERSION.BASEBAND;
        } else if (infoItem == InfoItem.BOOTLOADER_VERSION) {
            info = NlBuild.VERSION.BOOTLOADER_VERSION;
        } else if (infoItem == InfoItem.TOUCHSCREEN_NAME) {
            info = NlBuild.VERSION.TOUCHSCREEN_NAME;
        } else if (infoItem == InfoItem.TOUCHSCREEN_RESOLUTION) {
            info = NlBuild.VERSION.TOUCHSCREEN_RESOLUTION;
        } else if (infoItem == InfoItem.TOUCHSCREEN_VERSION) {
            info = NlBuild.VERSION.TOUCHSCREEN_VERSION;
        } else if (infoItem == InfoItem.PROCESSOR_INFO) {
            info = NlBuild.VERSION.PROCESSOR_INFO;
        } else if (infoItem == InfoItem.CUSTOMER_ID) {
            info = NlBuild.CUSTOMER_ID;
        }
        deviceLogger.debug("[getInfo]"+infoItem + ":" + info);
        return info;
    }

}
