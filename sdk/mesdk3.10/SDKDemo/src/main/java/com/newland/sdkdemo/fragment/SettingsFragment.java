package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.newland.telephony.ApnEntity;
import android.view.View;
import android.widget.CheckBox;

import com.newland.sdk.common.RunningModel;
import com.newland.sdk.module.settings.ApnUtil;
import com.newland.sdk.module.settings.InfoItem;
import com.newland.sdk.module.settings.NavigationKey;
import com.newland.sdk.module.settings.SettingsItems;
import com.newland.sdk.module.settings.SettingsModule;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>SettingsFragment</p>
 *
 * @author linsi
 */
public class SettingsFragment extends BaseFragment {

    private SettingsModule settingsModule;

    public SettingsFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    private static final int INDEX_SET_BRIGHTNESS = 1;
    private static final int INDEX_SET_SETTINGS_ITEMS_VISIBLE = 2;
    private static final int INDEX_SET_BATTERY_PERCENT_VISIBLE = 3;
    private static final int INDEX_SET_STATUS_BAR_DROP_DOWN = 4;
    private static final int INDEX_SET_NAVIGATION_KEY_VALID = 5;
    private static final int INDEX_SET_MOBILE_DATA_VALID = 6;
    private static final int INDEX_GET_MOBILE_DATA_STATUS = 7;
    private static final int INDEX_GET_CERTIFICATE_INFO = 8;
    private static final int INDEX_GET_INFO = 9;
    private static final int INDEX_GET_APN_UTIL = 10;

    @Override
    public String title() {
        return context.getString(R.string.settings_title);
    }

    @Override
    public void initData() {
        settingsModule = moduleManage.getSettingsModule();
    }

    @Override
    public Object getModule() {
        return SettingsFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.settings_set_brightness, functionid = INDEX_SET_BRIGHTNESS)
    private void setScreenBrightness() {
        try {
            String[] brightness = new String[256];
            for (int i = 0; i < brightness.length; i++) {
                brightness[i] = String.valueOf(i);
            }
            DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.tv_settings_set_brightness), brightness, new DialogUtils.SingleChoiceDialogCallback() {
                @Override
                public void onResult(int id) {
                    int bright = id;
                    boolean rslt = settingsModule.setScreenBrightness(bright);
                    if (rslt) {
                        showMessage("Set brightness successfully." + ", brightness:" + bright + "\n", MessageTag.TIP);
                    } else {
                        showMessage("Set brightness failed." + ", brightness:" + bright + "\n", MessageTag.ERROR);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("setScreenBrightness exception." + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_set_setttings_items_visible, functionid = INDEX_SET_SETTINGS_ITEMS_VISIBLE)
    private void setSettingsItemsVisible() {
        try {
            String[] settingsItems = new String[]{"Storage", "Battery", "Home", "Backup & reset",
                    "Data usage", "Accessibility", "Developer options", "Location", "Security", "Print", "Vpn", "Screen lock"};
            DialogUtils.createMultiChoiceDialog(context, context.getString(R.string.tv_settings_settings_items), settingsItems, new DialogUtils.MultiChoiceDialogCallback() {

                @Override
                public void onResult(ArrayList<Integer> yourChoices) {

                    ArrayList<SettingsItems> list = new ArrayList<>();
                    for (Integer id : yourChoices) {
                        if (id == 0) {
                            list.add(SettingsItems.STORAGE);
                        } else if (id == 1) {
                            list.add(SettingsItems.BATTERY);
                        } else if (id == 2) {
                            list.add(SettingsItems.HOME);
                        } else if (id == 3) {
                            list.add(SettingsItems.BACKUP_RESET);
                        }
//                        else if (id == 4) {
//                            list.add(SettingsItems.APPS);
//                        }
                        else if (id == 4) {
                            list.add(SettingsItems.DATA_USAGE);
                        } else if (id == 5) {
                            list.add(SettingsItems.ACCESSIBILITY);
                        } else if (id == 6) {
                            list.add(SettingsItems.DEVELOPER_OPTIONS);
                        } else if (id == 7) {
                            list.add(SettingsItems.LOCATION);
                        } else if (id == 8) {
                            list.add(SettingsItems.SECURITY);
                        } else if (id == 9) {
                            list.add(SettingsItems.PRINT);
                        } else if (id == 10) {
                            list.add(SettingsItems.VPN);
                        } else if (id == 11) {
                            list.add(SettingsItems.SCREEN_LOCK);
                        }
                    }
                    final SettingsItems settingsItems[] = new SettingsItems[list.size()];
                    list.toArray(settingsItems);
                    String[] visibles = new String[]{"true", "false"};
                    DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.tv_settings_set_visible), visibles, new DialogUtils.SingleChoiceDialogCallback() {
                        @Override
                        public void onResult(int id) {
                            boolean isVisible = false;
                            if (id == 0) {
                                isVisible = true;
                            } else {
                                isVisible = false;
                            }
                            settingsModule.setSettingsItemsVisible(settingsItems, isVisible);
                            showMessage("setSettingsItemsVisible done." + "\r" + "settingsItems:" + Arrays.toString(settingsItems) + ", isVisible:" + isVisible, MessageTag.TIP);

                        }
                    });

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("setSettingsItemsVisible exception." + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_set_battery_percent_visible, functionid = INDEX_SET_BATTERY_PERCENT_VISIBLE)
    private void setBatteryPercentVisible() {
        try {

            String[] visibles = new String[]{"true", "false"};
            DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.tv_settings_set_visible), visibles, new DialogUtils.SingleChoiceDialogCallback() {
                @Override
                public void onResult(int id) {
                    boolean isVisible = false;
                    if (id == 0) {
                        isVisible = true;
                    } else {
                        isVisible = false;
                    }
                    settingsModule.setBatteryPercentVisible(isVisible);
                    showMessage("setBatteryPercentVisible done." + ", isVisible:" + isVisible, MessageTag.TIP);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("setBatteryPercentVisible exception." + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_set_status_bar_drop_down, functionid = INDEX_SET_STATUS_BAR_DROP_DOWN)
    private void setStatusBarDropDown() {
        try {

            String[] visibles = new String[]{"true", "false"};
            DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.tv_settings_set_drop_down), visibles, new DialogUtils.SingleChoiceDialogCallback() {
                @Override
                public void onResult(int id) {
                    boolean isDrop = false;
                    if (id == 0) {
                        isDrop = true;
                    } else {
                        isDrop = false;
                    }
                    settingsModule.setStatusBarDropDown(isDrop);
                    showMessage("setStatusBarDropDown done." + ", isDrop:" + isDrop, MessageTag.TIP);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("setStatusBarDropDown exception." + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_set_navigation_key_valid, functionid = INDEX_SET_NAVIGATION_KEY_VALID)
    private void setNavigationKeyValid() {
        try {

            DialogUtils.createCustomDialog(context, R.string.tv_settings_valid, null, R.layout.dialog_set_navikey_valid, new DialogUtils.CustomDialogCallback2() {
                @Override
                public void onInit(View view) {
                    CheckBox home = view.findViewById(R.id.checkbox_home_valid);
//                    CheckBox recents = view.findViewById(R.id.checkbox_recents_valid);
                    home.setChecked(true);
//                    recents.setChecked(true);
                }

                @Override
                public void onResult(int id, View view) {
                    CheckBox home = view.findViewById(R.id.checkbox_home_valid);
//                    CheckBox recents = view.findViewById(R.id.checkbox_recents_valid);
                    boolean isValid = false;
                    if (home.isChecked()) {
                        isValid = settingsModule.setNavigationKeyValid(NavigationKey.HOME, true);
                    } else {
                        isValid = settingsModule.setNavigationKeyValid(NavigationKey.HOME, false);
                    }
                    showMessage("home key." + " isValid " + home.isChecked(), MessageTag.DATA);
                    if (isValid) {
                        showMessage("set home key successfully.", MessageTag.TIP);
                    } else {
                        showMessage("set home key failed.", MessageTag.ERROR);
                    }
//                    if (recents.isChecked()) {
//                        isValid = settingsModule.setNavigationKeyValid(NavigationKey.RECENTS_KEY, true);
//                    } else {
//                        isValid = settingsModule.setNavigationKeyValid(NavigationKey.RECENTS_KEY, false);
//                    }
//                    showMessage("recents key." + " isValid " + recents.isChecked(), MessageTag.DATA);
//                    if (isValid) {
//                        showMessage("set recents key successfully.", MessageTag.TIP);
//                    } else {
//                        showMessage("set recents key failed.", MessageTag.ERROR);
//                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("setNavigationKeyValid exception." + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_set_mobile_data_valid, functionid = INDEX_SET_MOBILE_DATA_VALID)
    private void setMobileDataValid() {
        try {

            String[] valids = new String[]{"true", "false"};
            DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.dialog_settings_navi_key_valid), valids, new DialogUtils.SingleChoiceDialogCallback() {
                @Override
                public void onResult(int id) {
                    boolean isValid = false;
                    if (id == 0) {
                        isValid = true;
                    } else {
                        isValid = false;
                    }
                    settingsModule.setMobileDataValid(isValid);
                    showMessage("setMobileDataValid done." + ", isValid:" + isValid, MessageTag.TIP);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("setMobileDataValid exception." + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_get_mobile_data_status, functionid = INDEX_GET_MOBILE_DATA_STATUS)
    private void getMobileDataStatus() {
        try {

            boolean isValid = settingsModule.getMobileDataStatus();
            showMessage("getMobileDataStatus done." + ", isValid:" + isValid, MessageTag.TIP);

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("getMobileDataStatus exception." + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_get_cert_info, functionid = INDEX_GET_CERTIFICATE_INFO)
    private void getCertificateInfo() {
        try {

            X509Certificate x509Certificate = settingsModule.getCertificateInfo();
            showMessage("info:" + (x509Certificate == null ? "null" : new String(x509Certificate.getEncoded())), MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("getCertificateInfo exception." + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_get_info, functionid = INDEX_GET_INFO)
    private void getInfo() {
        try {
            showMessage("imei" + ":" + settingsModule.getInfo(InfoItem.IMEI), MessageTag.DATA);
            showMessage("meid" + ":" + settingsModule.getInfo(InfoItem.MEID), MessageTag.DATA);
            showMessage("Firmware version" + ":" + settingsModule.getInfo(InfoItem.FIRMWARE), MessageTag.DATA);
            showMessage("Firmware id" + ":" + settingsModule.getInfo(InfoItem.FIRMWARE_ID), MessageTag.DATA);
            showMessage("Hardware id" + ":" + settingsModule.getInfo(InfoItem.HARDWARE_ID), MessageTag.DATA);
            showMessage("Hardware config" + ":" + settingsModule.getInfo(InfoItem.HARDWARE_CONFIG), MessageTag.DATA);
            showMessage("Model" + ":" + settingsModule.getInfo(InfoItem.MODEL), MessageTag.DATA);
            showMessage("Serial number" + ":" + settingsModule.getInfo(InfoItem.SERIAL_NUMBER), MessageTag.DATA);
            showMessage("Baseband version" + ":" + settingsModule.getInfo(InfoItem.BASEBAND), MessageTag.DATA);
            showMessage("Customer id" + ":" + settingsModule.getInfo(InfoItem.CUSTOMER_ID), MessageTag.DATA);
            showMessage("Bootloader version" + ":" + settingsModule.getInfo(InfoItem.BOOTLOADER_VERSION), MessageTag.DATA);
            showMessage("Touchscreen name" + ":" + settingsModule.getInfo(InfoItem.TOUCHSCREEN_NAME), MessageTag.DATA);
            showMessage("Touchscreen resolution" + ":" + settingsModule.getInfo(InfoItem.TOUCHSCREEN_RESOLUTION), MessageTag.DATA);
            showMessage("Touchscreen version" + ":" + settingsModule.getInfo(InfoItem.TOUCHSCREEN_VERSION), MessageTag.DATA);
            showMessage("Processor info" + ":" + settingsModule.getInfo(InfoItem.PROCESSOR_INFO), MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("getInfo exception." + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.settings_get_apn_util, functionid = INDEX_GET_APN_UTIL)
    private void getApnUtil() {
        try {
            ApnUtil apnUtil = settingsModule.getApnUtil();
            ApnEntity apnEntity = new ApnEntity(); //name,apn,mnc,mcc must set up
            apnEntity.setName("NPT SDK");
            apnEntity.setApn("sdkapn");
            apnEntity.setMcc("460");
            apnEntity.setMnc("04");
            apnEntity.setType("default,supl");
            int row = apnUtil.addNewApn(apnEntity);
            if (row == -1) {// add new apn failed.
                showMessage("add new apn failed.", MessageTag.ERROR);
            } else {// add new apn successfully.
                showMessage("add new apn successfully." + "id:" + row, MessageTag.TIP);
                int rslt = apnUtil.setDefaultApn(row);
                if (rslt == -1) { // failed
                    showMessage("setDefaultApn failed.", MessageTag.ERROR);
                } else { // successfully.
                    showMessage("setDefaultApn successfully." + "row:" + rslt, MessageTag.TIP);
                    ApnEntity currentApn = apnUtil.getCurrentApn();
                    showMessage("getCurrentApn done.", MessageTag.TIP);
                    showMessage("name:" + currentApn.getName(), MessageTag.DATA);
                    showMessage("apn:" + currentApn.getApn(), MessageTag.DATA);
                    showMessage("mnc:" + currentApn.getMnc(), MessageTag.DATA);
                    showMessage("mcc:" + currentApn.getMcc(), MessageTag.DATA);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("getApnUtil exception." + e, MessageTag.ERROR);
        }
    }
}
