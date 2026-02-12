package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;
import android.newland.content.NlContext;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.bootprovider.BootProvider;
import com.newland.nsdk.core.api.internal.recovery.Recovery;
import com.newland.nsdk.core.api.internal.setting.Settings;
import com.newland.nsdk.core.api.internal.setting.SettingsManager;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;

import java.util.Locale;

public class SettingsManagerFragment extends InternalBaseFragment {

    private SettingsManager settingsManager;
    private BootProvider bootProvider;
    private Recovery recovery;

    public SettingsManagerFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_settings_f);
    }

    @Override
    public void initData() {
        settingsManager = (SettingsManager) moduleManager.getModule(ModuleType.SETTINGS);
        bootProvider = (BootProvider) moduleManager.getModule(ModuleType.BOOT_PROVIDER);
        recovery = (Recovery) moduleManager.getModule(ModuleType.RECOVERY);
    }

    @Override
    public Object getModule() {
        return SettingsManagerFragment.this;
    }


    @MethodGridEntity(btnnameid = R.string.set_screen_bright, functionid = 1)
    private void setScreenBrightness() {
        DialogUtils.createCustomDialog(context, R.string.set_screen_bright, null, R.layout.dialog_settings, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                LinearLayout llScreenBrightnessParams = view.findViewById(R.id.linear_settings_screen_brightness);
                llScreenBrightnessParams.setVisibility(View.VISIBLE);
                LinearLayout llScreenOffTimeoutParams = view.findViewById(R.id.linear_settings_screenOff_timeout);
                llScreenOffTimeoutParams.setVisibility(View.GONE);
                LinearLayout llPrinterPaperSizeParams = view.findViewById(R.id.linear_settings_printer_paper_size);
                llPrinterPaperSizeParams.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View dialogView) {
                EditText etBrightness = dialogView.findViewById(R.id.et_setting_brightness);
                String brightness = etBrightness.getText().toString();
                int screenBrightness = Integer.parseInt(brightness);
                try {
                    settingsManager.set(Settings.SCREEN_BRIGHTNESS, brightness);
                    showMessage(String.format(Locale.US, "%s : %d", context.getString(R.string.set_screen_bright), screenBrightness));
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, context.getString(R.string.set_screen_bright));
                }


            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.set_screen_off_timeout, functionid = 2)
    private void setScreenTimeout() {
        DialogUtils.createCustomDialog(context,R.string.dialog_tv_settings_screen_off_timeout, null, R.layout.dialog_settings, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                LinearLayout llScreenBrightnessParams= view.findViewById(R.id.linear_settings_screen_brightness);
                llScreenBrightnessParams.setVisibility(View.GONE);
                LinearLayout llScreenOffTimeoutParams = view.findViewById(R.id.linear_settings_screenOff_timeout);
                llScreenOffTimeoutParams.setVisibility(View.VISIBLE);
                LinearLayout llPrinterPaperSizeParams = view.findViewById(R.id.linear_settings_printer_paper_size);
                llPrinterPaperSizeParams.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View dialogView) {
                EditText etTimeout = dialogView.findViewById(R.id.et_setting_screen_Off_timeout);
                int screenTimeout = Integer.parseInt(etTimeout.getText().toString()) * 1000;
                String screenOffTimeout = String.valueOf(screenTimeout);
                try {
                    settingsManager.set(Settings.SCREEN_OFF_TIMEOUT, screenOffTimeout);
                    showMessage(String.format(Locale.US, "%s: %d s", context.getString(R.string.set_screen_off_timeout),Integer.parseInt(etTimeout.getText().toString())));
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, context.getString(R.string.set_screen_off_timeout));
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.printer_paper_size, functionid = 3)
    private void setPrinterPaperSize() {
        DialogUtils.createCustomDialog(context, R.string.printer_paper_size, null, R.layout.dialog_settings, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                LinearLayout llScreenBrightnessParams = view.findViewById(R.id.linear_settings_screen_brightness);
                llScreenBrightnessParams.setVisibility(View.GONE);
                LinearLayout llScreenOffTimeoutParams = view.findViewById(R.id.linear_settings_screenOff_timeout);
                llScreenOffTimeoutParams.setVisibility(View.GONE);
                LinearLayout llPrinterPaperSizeParams = view.findViewById(R.id.linear_settings_printer_paper_size);
                llPrinterPaperSizeParams.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnPrinterPaperSize = view.findViewById(R.id.et_setting_printer_paperSize);

                try {
                    String printerPaperSize = spnPrinterPaperSize.getSelectedItem().toString();
                    if(printerPaperSize.equals("2 inches")) {
                            printerPaperSize = "2";
                    }else {
                            printerPaperSize = "3";
                    }
                    settingsManager.set(Settings.PRINTER_PAPER_SIZE, printerPaperSize);
                    showMessage(String.format("%s:%s", context.getString(R.string.printer_paper_size), printerPaperSize));
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.printer_paper_size));
                    e.printStackTrace();
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.setting_app_display_on, functionid = 4)
    private void setSettingAppDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_APP_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_app_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_app_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_app_display_off, functionid = 5)
    private void setSettingAppDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_APP_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_app_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_app_display_off));
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.setting_storage_display_on, functionid = 6)
    private void setSettingStorageDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_STORAGE_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_storage_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_storage_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_storage_display_off, functionid = 7)
    private void setSettingStorageDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_STORAGE_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_storage_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_storage_display_off));
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.setting_home_display_on, functionid = 8)
    private void setSettingHomeDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_HOME_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_home_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_home_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_home_display_off, functionid = 9)
    private void setSettingHomeDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_HOME_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_home_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_home_display_off));
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.setting_vpn_display_on, functionid = 10)
    private void setSettingVpnDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_VPN_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_vpn_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_vpn_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_vpn_display_off, functionid = 11)
    private void setSettingVpnDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_VPN_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_vpn_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_vpn_display_off));
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.lockscreen_enable, functionid = 12)
    private void setSettingLockScreenDisplayOn() {
        try {
            settingsManager.set(Settings.LOCKSCREEN_DISABLED, "0");
            showMessage(context.getString(R.string.lockscreen_enable));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.lockscreen_enable));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.lockscreen_disabled, functionid = 13)
    private void setSettingLockScreenDisplayOff() {
        try {
            settingsManager.set(Settings.LOCKSCREEN_DISABLED, "1");
            showMessage(context.getString(R.string.lockscreen_disabled));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.lockscreen_disabled));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_wallpaper_display_on, functionid = 14)
    private void setSettingWallpaperDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_WALLPAPER_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_wallpaper_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_wallpaper_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_wallpaper_display_off, functionid = 15)
    private void setSettingWallpaperDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_WALLPAPER_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_wallpaper_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_wallpaper_display_off));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_notification_items_display_on, functionid = 16)
    private void setSettingNotificationItemsDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_NOTIFICATION_ITEMS_DISPLAY, "00000000");
            showMessage(context.getString(R.string.setting_notification_items_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_notification_items_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_notification_items_display_off, functionid = 17)
    private void setSettingNotificationItemsDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_NOTIFICATION_ITEMS_DISPLAY, "11111111");
            showMessage(context.getString(R.string.setting_notification_items_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_notification_items_display_off));
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.setting_privacy_display_on, functionid = 18)
    private void setSettingPrivacyDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_PRIVACY_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_privacy_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_privacy_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_privacy_display_off, functionid = 19)
    private void setSettingPrivacyDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_PRIVACY_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_privacy_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_privacy_display_off));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.status_bar_show_battery_percent_on, functionid = 20)
    private void setShowBatteryPercentOff() {
        try {
            settingsManager.set(Settings.STATUS_BAR_SHOW_BATTERY_PERCENT, "1");
            showMessage(context.getString(R.string.status_bar_show_battery_percent_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.status_bar_show_battery_percent_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.status_bar_show_battery_percent_off, functionid = 21)
    private void setShowBatteryPercentOn() {
        try {
            settingsManager.set(Settings.STATUS_BAR_SHOW_BATTERY_PERCENT, "0");
            showMessage(context.getString(R.string.status_bar_show_battery_percent_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.status_bar_show_battery_percent_off));
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.setting_apk_need_login_on, functionid = 22)
    private void setSettingApkNeedLoginOn() {
        try {
            settingsManager.set(Settings.SETTING_APK_NEED_LOGIN, "0");
            showMessage(context.getString(R.string.setting_apk_need_login_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_apk_need_login_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_apk_need_login_off, functionid = 23)
    private void setSettingApkNeedLoginOff() {
        try {
            settingsManager.set(Settings.SETTING_APK_NEED_LOGIN, "1");
            showMessage(context.getString(R.string.setting_apk_need_login_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_apk_need_login_off));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_data_usage_display_on, functionid = 24)
    private void setSettingDataUsageDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_DATA_USAGE_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_data_usage_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_data_usage_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_data_usage_display_off, functionid = 25)
    private void setSettingDataUsageDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_DATA_USAGE_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_data_usage_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_data_usage_display_off));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_battery_display_on, functionid = 26)
    private void setSettingBatteryDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_BATTERY_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_battery_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_battery_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_battery_display_off, functionid = 27)
    private void setSettingBatteryDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_BATTERY_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_battery_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_battery_display_off));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_accessibility_settings_display_on, functionid = 28)
    private void setSettingAccessibilitySettingsDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_ACCESSIBILITY_SETTINGS_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_accessibility_settings_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_accessibility_settings_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_accessibility_settings_display_off, functionid = 29)
    private void setSettingAccessibilitySettingsDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_ACCESSIBILITY_SETTINGS_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_accessibility_settings_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_accessibility_settings_display_off));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_development_settings_display_on, functionid = 30)
    private void setSettingDevelopmentSettingsDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_DEVELOPMENT_SETTINGS_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_development_settings_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_development_settings_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_development_settings_display_off, functionid = 31)
    private void setSettingDevelopmentSettingsDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_DEVELOPMENT_SETTINGS_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_development_settings_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_development_settings_display_off));
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.setting_location_settings_display_on, functionid = 32)
    private void setSettingLocationSettingsDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_LOCATION_SETTINGS_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_location_settings_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_location_settings_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_location_settings_display_off, functionid = 33)
    private void setSettingLocationSettingsDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_LOCATION_SETTINGS_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_location_settings_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_location_settings_display_off));
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.setting_security_settings_display_on, functionid = 34)
    private void setSettingSecuritySettingsDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_SECURITY_SETTINGS_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_security_settings_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_security_settings_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_security_settings_display_off, functionid = 35)
    private void setSettingSecuritySettingsDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_SECURITY_SETTINGS_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_security_settings_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_security_settings_display_off));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_print_settings_display_on, functionid = 36)
    private void setSettingPrintSettingsDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_PRINT_SETTINGS_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_print_settings_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_print_settings_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_print_settings_display_off, functionid = 37)
    private void setSettingPrintSettingsDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_PRINT_SETTINGS_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_print_settings_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_print_settings_display_off));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_apk_login_passwd, functionid = 38)
    private void setLoginPassword() {
        try {
            String password = "000000";
            settingsManager.set(Settings.SETTING_APK_LOGIN_PASSWORD, password);
            showMessage(String.format("%s: %s", context.getString(R.string.setting_apk_login_passwd), password));
            showMessage(context.getString(R.string.setting_please_enable_login_password));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_apk_login_passwd));
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.setting_disable_app_switch_key_on, functionid = 39)
    private void setAppSwitchKeyEnabledOn() {
        try {
            settingsManager.set(Settings.SETTING_DISABLE_APP_SWITCH_KEY, "0");
            showMessage(context.getString(R.string.setting_disable_app_switch_key_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_disable_app_switch_key_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_disable_app_switch_key_off, functionid = 40)
    private void setAppSwitchKeyEnabledOff() {
        try {
            settingsManager.set(Settings.SETTING_DISABLE_APP_SWITCH_KEY, "1");
            showMessage(context.getString(R.string.setting_disable_app_switch_key_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_disable_app_switch_key_off));
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.relayout_navigation_bar, functionid = 41)
    private void relayoutNavigationBar() {
        try {
            settingsManager.set(Settings.RELAYOUT_NAVIGATION_BAR, "0");
            showMessage(String.format("%s. %s", context.getString(R.string.relayout_navigation_bar), context.getString(R.string.reboot_required)));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.relayout_navigation_bar));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.relayout_navigation_bar_right, functionid = 42)
    private void relayoutNavigationBarRight() {
        try {
            settingsManager.set(Settings.RELAYOUT_NAVIGATION_BAR, "1");
            showMessage(String.format("%s. %s", context.getString(R.string.relayout_navigation_bar_right), context.getString(R.string.reboot_required)));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.relayout_navigation_bar_right));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_processor_display_on, functionid = 43)
    private void setSettingProcessorDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_PROCESSOR_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_processor_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_processor_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_processor_display_off, functionid = 44)
    private void setSettingProcessorDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_PROCESSOR_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_processor_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_processor_display_off));
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.setting_ota_update_on, functionid = 45)
    private void setSettingOtaUpdateEnabledOn() {
        try {
            settingsManager.set(Settings.SETTING_OTA_UPDATE, "0");
            showMessage(context.getString(R.string.setting_ota_update_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_ota_update_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_ota_update_off, functionid = 46)
    private void setSettingOtaUpdateEnabledOff() {
        try {
            settingsManager.set(Settings.SETTING_OTA_UPDATE, "1");
            showMessage(context.getString(R.string.setting_ota_update_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_ota_update_off));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_language_user_dictionary_display_on, functionid = 47)
    private void setSettingLanguageUserDictionaryDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_LANGUAGE_USER_DICTIONARY_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_language_user_dictionary_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_language_user_dictionary_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_language_user_dictionary_display_off, functionid = 48)
    private void setSettingLanguageUserDictionaryDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_LANGUAGE_USER_DICTIONARY_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_language_user_dictionary_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_language_user_dictionary_display_off));
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.status_bar_enabled_on, functionid = 49)
    private void setStatusBarExpandableOn() {
        try {
            settingsManager.set(Settings.STATUS_BAR_ENABLED, "0");
            showMessage(context.getString(R.string.status_bar_enabled_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.status_bar_enabled_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.status_bar_enabled_off, functionid = 50)
    private void setStatusBarExpandableOff() {
        try {
            settingsManager.set(Settings.STATUS_BAR_ENABLED, "1");
            showMessage(context.getString(R.string.status_bar_enabled_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.status_bar_enabled_off));
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.status_bar_adb_notify_on, functionid = 51)
    private void setStatusBarAdbNotifyOn() {
        try {
            settingsManager.set(Settings.STATUS_BAR_ADB_NOTIFY, "0");
            showMessage(context.getString(R.string.status_bar_adb_notify_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.status_bar_adb_notify_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.status_bar_adb_notify_off, functionid = 52)
    private void setStatusBarAdbNotifyOff() {
        try {
            settingsManager.set(Settings.STATUS_BAR_ADB_NOTIFY, "1");
            showMessage(context.getString(R.string.status_bar_adb_notify_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.status_bar_adb_notify_off));
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.status_bar_setting_enabled_on, functionid = 53)
    private void setStatusBarSettingEnabledOn() {
        try {
            settingsManager.set(Settings.STATUS_BAR_SETTING_ENABLED, "0");
            showMessage(context.getString(R.string.status_bar_setting_enabled_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.status_bar_setting_enabled_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.status_bar_setting_enabled_off, functionid = 54)
    private void setStatusBarSettingEnabledOff() {
        try {
            settingsManager.set(Settings.STATUS_BAR_SETTING_ENABLED, "1");
            showMessage(context.getString(R.string.status_bar_setting_enabled_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.status_bar_setting_enabled_off));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.tether_display_on, functionid = 55)
    private void setTetherDisplayOn() {
        try {
            settingsManager.set(Settings.TETHER_DISPLAY, "0");
            showMessage(context.getString(R.string.tether_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.tether_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.tether_display_off, functionid = 56)
    private void setTetherDisplayOff() {
        try {
            settingsManager.set(Settings.TETHER_DISPLAY, "1");
            showMessage(context.getString(R.string.tether_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.tether_display_off));
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.wifi_install_credentials_display_on, functionid = 57)
    private void setWifiInstallCedentialDisplayOn() {
        try {
            settingsManager.set(Settings.WIFI_INSTALL_CREDENTIALS_DISPLAY, "0");
            showMessage(context.getString(R.string.wifi_install_credentials_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.wifi_install_credentials_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.wifi_install_credentials_display_off, functionid = 58)
    private void setWifiInstallCedentialDisplayOff() {
        try {
            settingsManager.set(Settings.WIFI_INSTALL_CREDENTIALS_DISPLAY, "1");
            showMessage(context.getString(R.string.wifi_install_credentials_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.wifi_install_credentials_display_off));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_bluetooth_file_transfer_on, functionid = 59)
    private void setBluetoothFileTransferOn() {
        try {
            settingsManager.set(Settings.SETTING_BLUETOOTH_FILE_TRANSFER, "0");
            showMessage(context.getString(R.string.setting_bluetooth_file_transfer_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_bluetooth_file_transfer_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_bluetooth_file_transfer_off, functionid = 60)
    private void setBluetoothFileTransferOff() {
        try {
            settingsManager.set(Settings.SETTING_BLUETOOTH_FILE_TRANSFER, "1");
            showMessage(context.getString(R.string.setting_bluetooth_file_transfer_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_bluetooth_file_transfer_off));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_payment_cert_update_display_on, functionid = 61)
    private void setPaymentCertUpdateDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_PAYMENT_CERT_UPDATE_DISPLAY, "0");
            showMessage(context.getString(R.string.setting_payment_cert_update_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_payment_cert_update_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_payment_cert_update_display_off, functionid = 62)
    private void setPaymentCertUpdateDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_PAYMENT_CERT_UPDATE_DISPLAY, "1");
            showMessage(context.getString(R.string.setting_payment_cert_update_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_payment_cert_update_display_off));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_disable_home_key_on, functionid = 63)
    private void setHomeKeyEnabledOn() {
        try {
            settingsManager.set(Settings.SETTING_DISABLE_HOME_KEY, "0");
            showMessage(context.getString(R.string.setting_disable_home_key_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_disable_home_key_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_disable_home_key_off, functionid = 64)
    private void setHomeKeyEnabledOff() {
        try {
            settingsManager.set(Settings.SETTING_DISABLE_HOME_KEY, "1");
            showMessage(context.getString(R.string.setting_disable_home_key_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_disable_home_key_off));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_device_info_items_display_on, functionid = 65)
    private void setSettingDeviceInfoItemsDisplayOn() {
        try {
            settingsManager.set(Settings.SETTING_DEVICE_INFO_ITEMS_DISPLAY, "00000");
            showMessage(context.getString(R.string.setting_device_info_items_display_on));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_device_info_items_display_on));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_device_info_items_display_off, functionid = 66)
    private void setSettingDeviceInfoItemsDisplayOff() {
        try {
            settingsManager.set(Settings.SETTING_DEVICE_INFO_ITEMS_DISPLAY, "11111");
            showMessage(context.getString(R.string.setting_device_info_items_display_off));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_device_info_items_display_off));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_app_signature_scheme, functionid = 67)
    private void setAppSignatureVerificationScheme() {
        try {
            String scheme = "newland,allinpay";
            settingsManager.set(Settings.SETTINGS_APP_SIGNATURE_SCHEME,scheme);
            showMessage(String.format("%s: %s", context.getString(R.string.settings_app_signature_scheme), scheme));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.settings_app_signature_scheme));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.status_bar_qs_tile, functionid = 68)
    private void setStatusBarQsTiles() {
        try {
            String targetTiles = "wifi,bt,airplane,location,cell,inversion,rotation,flashlight,cast,hotspot";
            settingsManager.set(Settings.STATUS_BAR_QS_TILE, targetTiles);
            String result = settingsManager.get(Settings.STATUS_BAR_QS_TILE);
            showMessage(String.format("%s: %s", context.getString(R.string.status_bar_qs_tile), result));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.status_bar_qs_tile));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.setting_locales, functionid = 69)
    private void setSettingLocales() {
        try {
            String targetLocales = "zh-CN,en-US,ja-JP,ko-KR";
            settingsManager.set(Settings.SETTING_LOCALES, targetLocales);
            showMessage(String.format("%s: %s", context.getString(R.string.setting_locales), targetLocales));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.setting_locales));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.default_input_method_pinyin, functionid = 70)
    private void setDefaultInputMethodPinyin() {
        try {
            settingsManager.set(Settings.DEFAULT_INPUT_METHOD, "PinyinIME");
            showMessage(context.getString(R.string.default_input_method_pinyin));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.default_input_method_pinyin));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.default_input_method_latin, functionid = 71)
    private void setDefaultInputMethodLatin() {
        try {
            settingsManager.set(Settings.DEFAULT_INPUT_METHOD, "LatinIME");
            showMessage(context.getString(R.string.default_input_method_latin));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.default_input_method_latin));
            e.printStackTrace();
        }
    }
    @MethodGridEntity(btnnameid = R.string.relayout_navigation_bar_910_pro, functionid = 72)
    private void setN910ProNavigationBar() {
        if (!Build.MODEL.equals("N910 Pro")) {
            showMessage("This setting is supported on N910 Pro device.");
            return;
        }
        String[] modes = {"◁ ○ □", "□ ○ ◁", "◁ ○ □ ↓", "□ ○ ◁ ↓", "∨ ◁ ○ □", "∨ □ ○ ◁", "∨ ◁ ○ □ ↓", "∨ □ ○ ◁ ↓"};
        DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.relayout_navigation_bar_910_pro), modes, id -> {
            try {
                if (id == -1) {
                    return;
                }
                int value = id;
                if(id >= 4) {
                    value = id + 12;
                }
                settingsManager.set(Settings.NAVIGATION_BAR_CONFIG, String.valueOf(value));
                showMessage(context.getString(R.string.relayout_navigation_bar_910_pro));
            } catch (NSDKException e) {
                showErrorMessage(e, context.getString(R.string.relayout_navigation_bar_910_pro));
                e.printStackTrace();
            }
        });
    }
    @MethodGridEntity(btnnameid = R.string.settings_enable_charge_protection, functionid = 73)
    private void enableChargeProtection() {
        try {
            settingsManager.set(Settings.SETTING_PROTECTED_CHARGE_ENABLE, "1");
            showMessage(context.getString(R.string.settings_enable_charge_protection));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.settings_enable_charge_protection));
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_disable_charge_protection, functionid = 74)
    private void disableChargeProtection() {
        try {
            settingsManager.set(Settings.SETTING_PROTECTED_CHARGE_ENABLE, "0");
            showMessage(context.getString(R.string.settings_disable_charge_protection));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.settings_disable_charge_protection));
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_charge_capacity, functionid = 75)
    private void setChargeCapacity() {
        DialogUtils.createCustomDialog(context, R.string.settings_charge_capacity, null, R.layout.dialog_settings_charge, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                LinearLayout llRechargeParams = view.findViewById(R.id.linear_settings_setRechargeCapacity);
                llRechargeParams.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                EditText editCharge = view.findViewById(R.id.edit_settingManager_setChargeCapacity);
                String value = editCharge.getText().toString();
                try {
                    settingsManager.set(Settings.SETTING_PROTECTED_CHARGE_CAPACITY, value);
                    showMessage(context.getString(R.string.settings_charge_capacity));
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.settings_charge_capacity));
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.settings_recharge_capacity, functionid = 76)
    private void setRechargeCapacity() {
        DialogUtils.createCustomDialog(context, R.string.settings_recharge_capacity, null, R.layout.dialog_settings_charge, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                LinearLayout llChargeParams = view.findViewById(R.id.linear_settings_setChargeCapacity);
                llChargeParams.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                EditText editRecharge = view.findViewById(R.id.edit_settingManager_setRechargeCapacity);
                String value = editRecharge.getText().toString();
                try {
                    settingsManager.set(Settings.SETTING_PROTECTED_RECHARGE_CAPACITY, value);
                    showMessage(context.getString(R.string.settings_recharge_capacity));
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.settings_recharge_capacity));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.settings_set_boot_animation, functionid = 77)
    private void setBootAnimation() {
        try {
            String animationFilePath = "/data/bootanimation_signed.zip";
            bootProvider.setCustomBootAnimation(animationFilePath);
            showMessage(context.getString(R.string.settings_set_boot_animation));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.settings_set_boot_animation));
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_remove_boot_animation, functionid = 78)
    private void removeBootAnimation() {
        try {
            bootProvider.removeCustomBootAnimation();
            showMessage(context.getString(R.string.settings_remove_boot_animation));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.settings_remove_boot_animation));
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_set_boot_logo, functionid = 79)
    private void setBootLogo() {
        try {
            String logoFilePath = "/data/splash_1280.img";
            bootProvider.setCustomBootLogo(logoFilePath);
            showMessage(context.getString(R.string.settings_set_boot_logo));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.settings_set_boot_logo));
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_remove_boot_logo, functionid = 80)
    private void removeBootLogo() {
        try {
            bootProvider.removeCustomBootLogo();
            showMessage(context.getString(R.string.settings_remove_boot_logo));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.settings_remove_boot_logo));
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_recovery, functionid = 81)
    private void recovery() {
        try {
            String[] packageNames = new String[] {"com.newland.nsdkdemo", "com.example.testforthsdk"};
            recovery.keepApps(packageNames);
            showMessage(context.getString(R.string.settings_recovery));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.settings_recovery));
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_launcher, functionid = 82)
    private void setLauncher() {
        try {
            String packageName = "com.example.testforthsdk";
            LogUtils.d("PackageName", packageName);
            settingsManager.set(Settings.SETTING_LAUNCHER, packageName);
            showMessage(context.getString(R.string.settings_launcher));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.settings_launcher));
        }
    }

}
