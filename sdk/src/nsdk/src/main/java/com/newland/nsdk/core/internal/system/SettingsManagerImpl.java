package com.newland.nsdk.core.internal.system;

import android.annotation.SuppressLint;
import android.content.Context;
import android.newland.content.NlContext;
import android.os.Binder;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.api.internal.setting.Settings;
import com.newland.nsdk.core.api.internal.setting.SettingsManager;
import com.newland.nsdk.core.internal.jni.NSDKJni;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author lin
 * @version 2020/2/12
 */
public class SettingsManagerImpl implements SettingsManager {
    private static final String TAG = "SettingsManagerImpl";
    private android.newland.SettingsManager settingsManager;
    private Context mContext;
    private volatile static SettingsManagerImpl instance;
    public static SettingsManagerImpl getInstance(Context context) {
        if (instance == null) {
            synchronized (SettingsManagerImpl.class) {
                if (instance == null || instance.mContext != context) {
                    instance = new SettingsManagerImpl(context);
                }
            }
        } else {
            if (instance.mContext != context) {
                instance = new SettingsManagerImpl(context);
            }
        }
        return instance;
    }

    public boolean isSupported;

    @SuppressLint("WrongConstant")
    private SettingsManagerImpl(Context mContext) {
        this.mContext = mContext;
        settingsManager = (android.newland.SettingsManager) mContext.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
        this.isSupported = true;
    }

    private void isSupported() throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported Printer Module");
        }
    }

    /**
     * @param key    {@link Settings}
     * @param sValue 0-显示 1-不显示
     * @return
     */
    @Override
    public void set(String key, String sValue) throws NSDKException {
        isSupported();

        int value = 0;
        boolean isValue = false;
        if (key == null) {
            throw new NSDKIllegalParameterException("Key shall not be null!");
        }

        if (sValue == null && (!key.equals(Settings.SETTINGS_APP_SIGNATURE_SCHEME))) {
            throw new NSDKIllegalParameterException(String.format(Locale.US, "Value is required when key is %s", key));
        }

        switch (key) {
            case Settings.SCREEN_BRIGHTNESS:
                value = convertStringToInt(sValue);
                if (value <= -1 || value > 255) {
                    throw new NSDKIllegalParameterException("The screen brightness should between 0 and 255!");
                }
                break;
            case Settings.SCREEN_OFF_TIMEOUT:
                value = convertStringToInt(sValue);
                if (value <= -2 || value == 0) {
                    throw new NSDKIllegalParameterException("The screen off timeout should be -1 or above 0!");
                } else if (value == -1) {
                    value = Integer.MAX_VALUE;
                    isValue = true;
                }
                break;
            case Settings.SETTING_APP_DISPLAY:
            case Settings.SETTING_STORAGE_DISPLAY:
            case Settings.SETTING_HOME_DISPLAY:
            case Settings.SETTING_WALLPAPER_DISPLAY:
            case Settings.SETTING_PRIVACY_DISPLAY:
            case Settings.STATUS_BAR_SHOW_BATTERY_PERCENT:
            case Settings.SETTING_APK_NEED_LOGIN:
            case Settings.SETTING_DATA_USAGE_DISPLAY:
            case Settings.SETTING_BATTERY_DISPLAY:
            case Settings.SETTING_ACCESSIBILITY_SETTINGS_DISPLAY:
            case Settings.SETTING_DEVELOPMENT_SETTINGS_DISPLAY:
            case Settings.SETTING_LOCATION_SETTINGS_DISPLAY:
            case Settings.SETTING_SECURITY_SETTINGS_DISPLAY:
            case Settings.SETTING_PRINT_SETTINGS_DISPLAY:
            case Settings.SETTING_DISABLE_APP_SWITCH_KEY:
            case Settings.SETTING_VPN_DISPLAY:
            case Settings.SETTING_PROCESSOR_DISPLAY:
            case Settings.SETTING_OTA_UPDATE:
            case Settings.SETTING_LANGUAGE_USER_DICTIONARY_DISPLAY:
            case Settings.STATUS_BAR_ENABLED:
            case Settings.STATUS_BAR_ADB_NOTIFY:
            case Settings.STATUS_BAR_SETTING_ENABLED:
            case Settings.TETHER_DISPLAY:
            case Settings.WIFI_INSTALL_CREDENTIALS_DISPLAY:
            case Settings.SETTING_BLUETOOTH_FILE_TRANSFER:
            case Settings.SETTING_PAYMENT_CERT_UPDATE_DISPLAY:
            case Settings.SETTING_PROTECTED_CHARGE_ENABLE:
            case Settings.SETTING_DISABLE_HOME_KEY:
                value = convertStringToInt(sValue);
                if (value != 0 && value != 1) {
                    throw new NSDKIllegalParameterException("Setting value shall be 0 or 1.");
                }
                break;
            case Settings.SETTING_PROTECTED_CHARGE_CAPACITY:
            case Settings.SETTING_PROTECTED_RECHARGE_CAPACITY:
                value = convertStringToInt(sValue);
                if (value < 0 || value >100) {
                    throw new NSDKIllegalParameterException("Setting value shall be range from 0 to 100.");
                }
                break;
            case Settings.LOCKSCREEN_DISABLED:
                value = convertStringToInt(sValue);
                if (value != 0 && value != 1) {
                    throw new NSDKIllegalParameterException("Setting value shall be 0 or 1.");
                }

                settingsManager.setSettingLockScreenDisplay(value);
                return;

            case Settings.SETTING_NOTIFICATION_ITEMS_DISPLAY:
                if (sValue.length() != 8) {
                    throw new NSDKIllegalParameterException("Value length shall be 8.");
                }
                int ii;
                try {
                    ii = Integer.parseInt(new StringBuilder(sValue.trim()).reverse().toString(), 2);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new NSDKException(e);
                }
                if (ii < 0 || ii > 255) {
                    throw new NSDKIllegalParameterException("The notification items should between 0 to 255!");
                }
                break;

            case Settings.SETTING_APK_LOGIN_PASSWORD:
                settingsManager.setLoginPassword(sValue);
                return;
            case Settings.RELAYOUT_NAVIGATION_BAR:
                value = convertStringToInt(sValue);
                if (value < 0 || value > 2) {
                    throw new NSDKIllegalParameterException("The relayout navigation bar should between 0 to 2!");
                }
                break;
            case Settings.NAVIGATION_BAR_CONFIG:
            case Settings.SETTINGS_RECENT_APPS_KEY_CODE:
                value = convertStringToInt(sValue);
                break;
            case Settings.SETTING_DEVICE_INFO_ITEMS_DISPLAY:
                if (sValue.length() != 5) {
                    throw new NSDKIllegalParameterException("Value length shall be 5.");
                }
                try {
                    ii = Integer.parseInt(new StringBuilder(sValue.trim()).reverse().toString(), 2);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new NSDKException(e);
                }
                if (ii < 0 || ii > 31) {
                    throw new NSDKIllegalParameterException("The device info items should between 0 to 31!");
                }
                break;

            case Settings.SETTING_PRODUCT_MODEL:
                if (Binder.getCallingUid() != 10063) {
                    throw new NSDKException("Invalid calling UID.");
                }
                SystemPropertyUtil.setProperty("persist.sys.pos_model", sValue);
                return;

            case Settings.SETTINGS_APP_SIGNATURE_SCHEME:
                String[] localSchemes = new String[]{"newland", "allinpay", "meituan", "cmbc", "liandong",
                        "yinsheng", "landi_infix", "landi_joint"};
                if (sValue == null || sValue.length() == 0) {
                    sValue = "null";
                } else {
                    String[] values = sValue.split(",");
                    StringBuffer sb = new StringBuffer();
                    boolean b = false;
                    for (String s : values) {
                        for (String localScheme : localSchemes) {
                            if (localScheme.equals(s)) {
                                b = true;
                                continue;
                            }
                        }
                        if (!b) {
                            throw new NSDKException("Scheme not found.");
                        }
                        b = false;
                        sb.append(s).append(",");
                    }
                    sValue = sb.toString();
                }
                break;
            case Settings.STATUS_BAR_QS_TILE:
                setStatusBardQsTile(sValue);
                return;
            case Settings.SETTING_LOCALES:
                setLocales(sValue);
                return;
            case Settings.DEFAULT_INPUT_METHOD:
                setDefaultInputMethod(sValue);
                return;
            case Settings.PRINTER_GREY_LEVEL:
                setPrinterGreyLevel(sValue);
                return;
            case Settings.PRINTER_PAPER_SIZE:
                setPrinterPaperSize(sValue);
                return;
            case Settings.SETTING_LAUNCHER:
                settingsManager.setLauncher(sValue);
                return;
            case Settings.LED_BACKGROUND_WIDTH:
            case Settings.LED_BACKGROUND_HEIGHT:
                throw new NSDKIllegalParameterException("LED background height or width is only readable.");
            default:
                break;
        }
        if (isValue) {
            sValue = String.valueOf(value);
        }
        LogUtils.e(TAG, "setSystemSetting: " + key + ", value:" + sValue);
        boolean isSucc = settingsManager.setSystemSetting(key, sValue);
        LogUtils.e(TAG, "setSystemSetting result:" + isSucc);
        if (!isSucc) {
            throw new NSDKException("Failed to set system setting.");
        }
    }

    private void setLocales(String sValue) {
        if (TextUtils.isEmpty(sValue)) {
            LogUtils.w(TAG, "The value is empty!");
            return;
        }
        String[] values = sValue.split(",");
        if (values.length > 0) {
            settingsManager.setSettingLocales(values);
        }
    }

    private void setPrinterPaperSize(String sValue) throws NSDKIllegalParameterException, NSDKNDKException {
        int value;
        value = convertStringToInt(sValue);
        if (value != 2 && value != 3) {
            throw new NSDKIllegalParameterException("The printer paper size should be 2 or 3!");
        }
        int retCode = NSDKJni.getInstance().TTF_PrnSetPaperSize(value);
        if (retCode == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (retCode != ErrorCode.OK) {
            throw new NSDKNDKException(retCode, String.format(Locale.US, "Failed to set printer paper size, result code = %d", retCode));
        }
        return;
    }

    private void setPrinterGreyLevel(String sValue) throws NSDKIllegalParameterException, NSDKNDKException {
        int value;
        value = convertStringToInt(sValue);
        if (value < 0 || value > 6) {
            throw new NSDKIllegalParameterException("The printer grey should between 0 and 6!");
        }
        int ret = NSDKJni.getInstance().NDK_PrnSetGreyScale(value);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to set printer grey level, result code = %d", ret));
        }
    }

    private void setDefaultInputMethod(String sValue) throws NSDKException {
        if (TextUtils.isEmpty(sValue)) {
            throw new NSDKIllegalParameterException("The value is empty!");
        }

        InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService
                (Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> mInputMethodInfo = mInputMethodManager
                .getInputMethodList();
        String matchedMethod = null;
        for (Iterator iterator = mInputMethodInfo.iterator(); iterator
                .hasNext(); ) {
            InputMethodInfo inputMethodInfo = (InputMethodInfo) iterator.next();
            if (!inputMethodInfo.getServiceName().endsWith(sValue)) {
                continue;
            }

            matchedMethod = inputMethodInfo.getServiceName();
        }

        if (matchedMethod == null) {
            throw new NSDKException("Unsupported input method.");
        }

        sValue = matchedMethod;
        boolean result = settingsManager.setDefaultInputMethod(sValue);
        if (!result) {
            throw new NSDKException("Failed to set system setting.");
        }
    }

    private void setStatusBardQsTile(String sValue) throws NSDKException {
        String[] legalQsTiles = new String[]{"wifi", "bt", "inversion", "cell", "airplane",
                "rotation", "flashlight", "location", "cast", "hotspot", "setting"};

        if (sValue == null || sValue.length() == 0) {
            throw new NSDKIllegalParameterException("No QS tile to set.");
        }

        String[] values = sValue.split(",");
        StringBuffer sb = new StringBuffer();
        boolean b = false;
        for (String s : values) {
            for (String localScheme : legalQsTiles) {
                if (localScheme.equals(s)) {
                    b = true;
                    sb.append(s).append(",");
                }
            }
        }
        if (!b) {
            throw new NSDKIllegalParameterException("No valid legal QS tile found.");
        }
        sValue = sb.toString();
        boolean result = settingsManager.setStatusBarQsTiles(sValue.split(","));
        if (!result) {
            throw new NSDKException("Failed to set system setting.");
        }
    }

    @Override
    public String get(String key) throws NSDKException {
        isSupported();

        if (key == null || key.isEmpty()) {
            throw new NSDKIllegalParameterException();
        }

        // No NAPI or NDK API to get printer grey level.
        if (key.equals(Settings.PRINTER_GREY_LEVEL) || key.equals(Settings.PRINTER_PAPER_SIZE)) {
            throw new NSDKIllegalParameterException("Printer grey level and paper size are yet supported to get.");
        }

        if (Settings.LED_BACKGROUND_WIDTH.equals(key)) {
            return SystemPropertyUtil.getProperty(Settings.LED_BACKGROUND_WIDTH, "");
        }
        if (Settings.LED_BACKGROUND_HEIGHT.equals(key)) {
            return SystemPropertyUtil.getProperty(Settings.LED_BACKGROUND_HEIGHT, "");
        }

        String result = settingsManager.getSystemSetting(key);

        if (key.equals(Settings.STATUS_BAR_QS_TILE) && !TextUtils.isEmpty(result)) {
            String tempStr = result.trim();
            if (tempStr.endsWith(",")) {
                return tempStr.substring(0, tempStr.length() - 1);
            }
        }

        return result;
    }

    private int convertStringToInt(String value) throws NSDKIllegalParameterException {
        if (value == null || value.isEmpty()) {
            throw new NSDKIllegalParameterException("The value is null or empty.");
        }

        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new NSDKIllegalParameterException(String.format(Locale.US, "Failed to parse %s to int.", value));
        }
    }
}
