package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.internal.devicemanager.AntiRemovalStatus;
import com.newland.nsdk.core.api.internal.devicemanager.BatteryProperty;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceInfo;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceLight;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceManager;
import com.newland.nsdk.core.api.internal.devicemanager.EthernetMode;
import com.newland.nsdk.core.api.internal.devicemanager.KeyboardButton;
import com.newland.nsdk.core.api.internal.devicemanager.LightMode;
import com.newland.nsdk.core.api.internal.devicemanager.RadarGain;
import com.newland.nsdk.core.api.internal.devicemanager.TamperReason;
import com.newland.nsdk.core.api.internal.devicemanager.TamperStatus;
import com.newland.nsdk.core.api.internal.devicestatisticsmanager.DeviceStatisticsManager;
import com.newland.nsdk.core.external.ExtNSDKModuleManagerImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.EnumUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;
import com.newland.nsdkdemo.internal.utils.SystemPropertyUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DeviceManagerFragment extends InternalBaseFragment {
    private ExtNSDKModuleManagerImpl extNSDKModuleManager = ExtNSDKModuleManagerImpl.getInstance();

    private DeviceManager mDeviceManager;
    private DeviceStatisticsManager mDeviceStatisticsManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;

    public DeviceManagerFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_terminalmanage_f);
    }

    @Override
    public void initData() {
        mDeviceManager = (DeviceManager) moduleManager.getModule(ModuleType.DEVICE_MANAGER);
        mDeviceStatisticsManager = (DeviceStatisticsManager) moduleManager.getModule(ModuleType.DEVICE_STATISTICS_MANAGER);
        sharedPreferences = context.getSharedPreferences(AppConfig.SharedPreferenceConfig.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
    }

    @Override
    public Object getModule() {
        return DeviceManagerFragment.this;
    }

    private static final int INDEX_TERMINAL_SETTIME = 1;
    private static final int INDEX_TERMINAL_GETTIME = 2;
    private static final int INDEX_GET_DEVICEINFO = 3;
    private static final int INDEX_GET_TAMPER_STATUS = 4;
    private static final int INDEX_GET_ERROR_MESSAGE = 5;
    private static final int INDEX_GET_DEVICE_STATISTICS_INFO = 6;
    private static final int INDEX_GET_DEVICE_STATISTICS_INFO_BY_TAG = 7;
    private static final int INDEX_GET_NON_DELETE_APPS_PACKAGE_NAME = 8;
    private static final int INDEX_SET_KEY_VOLUME = 9;
    private static final int INDEX_SET_ETHERNET_MODE = 15;
    private static final int INDEX_GET_ETHERNET_MODE = 16;
    private static final int INDEX_GET_BATTERY_PROPERTY = 17;
    private static final int INDEX_SET_DEVICE_LIGHT_MODE = 18;
    private static final int INDEX_SET_LONG_PRESS_BUTTON = 19;

    @MethodGridEntity(btnnameid = R.string.tv_time_and_date_set, functionid = INDEX_TERMINAL_SETTIME)
    private void setTerminalTime() {
        Calendar calendar = Calendar.getInstance();
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_time_and_date_set), null, R.layout.dialog_devicemanager_set_date, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                DatePicker dpSetDate = view.findViewById(R.id.dpSetDate);
                TimePicker tpSetTime = view.findViewById(R.id.tpSetTime);
                tpSetTime.setIs24HourView(true);
                int year = dpSetDate.getYear();
                int month = dpSetDate.getMonth();
                int day = dpSetDate.getDayOfMonth();
                int hour = tpSetTime.getCurrentHour();
                int minute = tpSetTime.getCurrentMinute();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                Settings.System.putString(context.getContentResolver(), Settings.System.TIME_12_24, "24");
                Date date = calendar.getTime();
                if (date != null) {
                    try {
                        mDeviceManager.setPOSDate(date);
                        showMessage(context.getString(R.string.msg_time_set_success), MessageTag.NORMAL);
                    } catch (Exception e) {
                        showErrorMessage(e, context.getString(R.string.msg_time_set_error));
                    }
                }

            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_time_and_date_get, functionid = INDEX_TERMINAL_GETTIME)
    private void getTerminalTime() {
        try {
            Date getDate = mDeviceManager.getPOSDate();
            showMessage(context.getString(R.string.msg_date_set_success) + formatDate(getDate), MessageTag.NORMAL);
        } catch (Exception e) {
            showErrorMessage(e, context.getString(R.string.msg_time_get_error));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_get_device_info, functionid = INDEX_GET_DEVICEINFO)
    private void getDeviceInfo() {
        try {
            showMessage(context.getString(R.string.msg_get_device_info_begin) + "\r\n", MessageTag.NORMAL);
            DeviceInfo deviceInfo = mDeviceManager.getDeviceInfo();
            showMessage(context.getString(R.string.msg_device_led_config) + deviceInfo.getLEDConfig(), MessageTag.DATA);
            showMessage(context.getString(R.string.msg_devcie_SN_NO) + deviceInfo.getSN(), MessageTag.DATA);
            showMessage(context.getString(R.string.msg_devcie_PN_NO) + deviceInfo.getPN(), MessageTag.DATA);
            showMessage(context.getString(R.string.msg_devcie_model) + deviceInfo.getDeviceModel(), MessageTag.DATA);
            showMessage(context.getString(R.string.msg_devcie_firmware_ver) + deviceInfo.getFirmwareVer(), MessageTag.DATA);
            showMessage(context.getString(R.string.msg_devcie_android_ver) + deviceInfo.getAndroidVersion(), MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_ICcard) + deviceInfo.isSupportICCard() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_MagCard) + deviceInfo.isSupportMagCard() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isDualMsr) + deviceInfo.isDualMsr() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_Offline) + deviceInfo.isSupportOffline() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_Printe) + deviceInfo.isSupportPrint() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_RFCard) + deviceInfo.isSupportContactlessCard() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_HCE) + deviceInfo.isSupportHCE() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_LPCD) + deviceInfo.isSupportLPCD() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_USB) + deviceInfo.isSupportUSB() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.is_sup_gps) + deviceInfo.isSupportGPS() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.is_sup_pinpad) + deviceInfo.isSupportPinpadPort() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.is_sup_rs232) + deviceInfo.isSupport232Port() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.is_sup_ethernet) + deviceInfo.isSupportEthernet() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.is_sup_sam) + deviceInfo.isSupportSam() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.is_sup_cashbox) + deviceInfo.isSupportCashBox() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.is_sup_carama) + deviceInfo.isSupportCamera() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.is_sup_beep) + deviceInfo.isSupportBeep() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_custom_id) + deviceInfo.getCustomerID() + "\r\n", MessageTag.DATA);

            showMessage("Is security module exist = " + mDeviceManager.isExistSecurityModule() + " " + mDeviceManager.getSDKVersion());
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.msg_get_device_info_error));
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    @MethodGridEntity(btnnameid = R.string.tv_get_tamper_status, functionid = INDEX_GET_TAMPER_STATUS)
    private void getTamperStatus() {
        try {
            TamperStatus[] status = mDeviceManager.getTamperStatuses();
            for (TamperStatus s : status) {
                showMessage(String.format("%s: %s", context.getString(R.string.tv_get_tamper_status), s), MessageTag.NORMAL);
            }

        } catch (Exception e) {
            showErrorMessage(e, context.getString(R.string.tv_get_tamper_status));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_get_error_message, functionid = INDEX_GET_ERROR_MESSAGE)
    private void getErrorMessage() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_get_error_message), null, R.layout.dialog_errorcode, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                    String str = " ";
                    if(id == -1) {
                        return;
                    }
                    EditText errCodeInput = dialogView.findViewById(R.id.dialog_errorCodeInput);
                    int errCode = Integer.parseInt(errCodeInput.getText().toString());
                    byte[] msg = new byte[128];
                    errCode = -(Math.abs(errCode));
                    if(Math.abs(errCode) < 10000) {
                        str = moduleManager.getErrMsg(errCode);
                    }

//                    if(Math.abs(errCode) >= 10000) {
//                        str = extNSDKModuleManager.getErrMsg(errCode);
//                    }
                    showMessage(String.format("[%d]%s", errCode, str), MessageTag.DATA);
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_get_device_statistics_info, functionid = INDEX_GET_DEVICE_STATISTICS_INFO)
    private void getDeviceStatisticsInfo() {
        try {
            String deviceStatisticsInfo = mDeviceStatisticsManager.getDeviceStatisticsInfo();
            showMessage(deviceStatisticsInfo);
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.tv_get_device_statistics_info));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_get_device_statistics_info_by_tag, functionid = INDEX_GET_DEVICE_STATISTICS_INFO_BY_TAG)
    private void getDeviceStatisticsInfoByTag() {
        try {
            String deviceStatisticsInfo = mDeviceStatisticsManager.getDeviceStatisticsInfoByTag("power");
            showMessage(deviceStatisticsInfo);
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.tv_get_device_statistics_info_by_tag));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_get_nonDeleteAppName, functionid = INDEX_GET_NON_DELETE_APPS_PACKAGE_NAME)
    private void getNonDeleteAppsPackageName() {
        try {
            SystemPropertyUtil.setProperty("persist.sys.cantuninstall.0", "com.newland.nsdkdemo");
            List<String> nonDeleteAppsName = mDeviceManager.getNonDeletableAppList(context);
            for(String s : nonDeleteAppsName) {
                showMessage(s);
            }
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.tv_get_nonDeleteAppName));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_set_key_volume, functionid = INDEX_SET_KEY_VOLUME)
    private void setKeyVolume() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_set_key_volume), null, R.layout.dialog_set_key_volume, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                Switch swSetKeyVolume = dialogView.findViewById(R.id.sw_setKeyVolume);
                boolean isOpen = swSetKeyVolume.isChecked();
                try {
                    mDeviceManager.setKeyVolume(isOpen);
                    showMessage(context.getString(R.string.tv_set_key_volume));
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.tv_set_key_volume));
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.uart3port_get_external_power_supply, functionid = 10)
    private void test() {
        try {
            mDeviceManager.setRadarDetectionDistance(RadarGain.RADAR_GAIN_0xAB, 112);
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.msg_get_tamper_reason, functionid = 11)
    private void getTamperReason() {
        try {
            TamperReason[] tamperReason = mDeviceManager.getTamperReason();
            for (TamperReason t : tamperReason) {
                showMessage(context.getString(R.string.msg_tamper_reason) + t.name());
            }

        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.device_manager_enable_radar_heater, functionid = 12)
    private void enableRadarAndHeater() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.device_manager_enable_radar_heater), null, R.layout.dialog_enable_radar_heater, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                Switch swEnableRadarFunction = view.findViewById(R.id.sw_enableRadarFunction);
                Switch swEnableHeaterFunction = view.findViewById(R.id.sw_enableHeaterFunction);
                boolean isEnableRadar = swEnableRadarFunction.isChecked();
                boolean isEnableHeater = swEnableHeaterFunction.isChecked();
                try {
                    mDeviceManager.enableRadarAndHeater(isEnableRadar, isEnableHeater);
                    showMessage(context.getString(R.string.device_manager_enable_radar_heater));
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.device_manager_enable_radar_heater));
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.device_manager_get_anti_removal_status, functionid = 13)
    private void getDeviceStatus() {
        try {
            AntiRemovalStatus status = mDeviceManager.getAntiRemovalStatus();
            showMessage("Anti-removal status: " + status.name());
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.device_manager_set_anti_removal_status, functionid = 14)
    private void setDeviceStatus() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.device_manager_set_anti_removal_status), null, R.layout.dialog_set_anti_removal_status, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                Spinner spnAntiRemovalStatus = view.findViewById(R.id.spn_antiRemovalStatus);
                AntiRemovalStatus antiRemovalStatus = EnumUtils.getAntiRemovalStatus(spnAntiRemovalStatus.getSelectedItem().toString());
                try {
                    mDeviceManager.setAntiRemovalStatus(antiRemovalStatus);
                    showMessage("Set " + antiRemovalStatus.name() + " success.");
                } catch (NSDKException e) {
                    showErrorMessage(e, e.getMessage());
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.tv_set_ethernet_mode, functionid = INDEX_SET_ETHERNET_MODE)
    private void setEthernetMode() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_get_ethernet_mode), null, R.layout.dialog_set_ethernet_mode, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                Spinner spnSetEthernetMode = view.findViewById(R.id.spn_setEthernetMode);
                EthernetMode mode = EthernetMode.ALL_ON;
                String ethernetModeStr = spnSetEthernetMode.getSelectedItem().toString();
                mode = ethernetModeStr.equals("ALL_OFF") ? EthernetMode.ALL_OFF : EthernetMode.CONFIGURABLE;

                try {
                    mDeviceManager.setEthernetMode(mode);
                    showMessage(context.getString(R.string.tv_get_ethernet_mode));
                } catch (NSDKException e) {
                    showErrorMessage(e, e.getMessage());
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_get_ethernet_mode, functionid = INDEX_GET_ETHERNET_MODE)
    private void getEthernetMode() {
        try {
            EthernetMode mode = mDeviceManager.getEthernetMode();
            showMessage("EthernetMode: " + mode.name());
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_get_battery_property, functionid = INDEX_GET_BATTERY_PROPERTY)
    private void getBatteryProperty() {
        try {
            BatteryProperty batteryProperty = mDeviceManager.getBatteryProperty();
            showMessage(context.getString(R.string.msg_get_battery_property_isSupport_get_battery_temperature) + batteryProperty.isSupportGetBatteryTemperature(), MessageTag.DATA);
            showMessage(context.getString(R.string.msg_get_battery_propery_isSupportGetChargeCurrent) + batteryProperty.isSupportGetChargeCurrent(), MessageTag.DATA);
            showMessage(context.getString(R.string.msg_get_battery_property_battery_temperature) + batteryProperty.getTemperature(), MessageTag.DATA);
            showMessage(context.getString(R.string.msg_get_battery_property_voltage) + batteryProperty.getAdapterVoltage(), MessageTag.DATA);
            showMessage(context.getString(R.string.msg_get_battery_property_current) + batteryProperty.getChargeCurrent(), MessageTag.DATA);
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_set_device_light_mode, functionid = INDEX_SET_DEVICE_LIGHT_MODE)
    private void setDeviceLightMode() {
        DialogUtils.createCustomDialog(context, R.string.tv_set_device_light_mode, null, R.layout.dialog_set_device_light_mode, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Spinner spnDeviceLight = view.findViewById(R.id.spn_setDeviceLight_light);
                spnDeviceLight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.DEVICE_MANAGER_SET_DEVICE_LIGHT, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spnDeviceLight.setSelection(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.DEVICE_MANAGER_SET_DEVICE_LIGHT, 0));
                Spinner spnDeviceLightMode = view.findViewById(R.id.spn_setDeviceLight_mode);
                spnDeviceLightMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.DEVICE_MANAGER_SET_DEVICE_LIGHT_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spnDeviceLightMode.setSelection(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.DEVICE_MANAGER_SET_DEVICE_LIGHT_MODE, 0));
                EditText editDeviceLightFlashingInterval = view.findViewById(R.id.edit_setDeviceLight_flashingInterval);
                editDeviceLightFlashingInterval.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.DEVICE_MANAGER_SET_DEVICE_LIGHT_FLASHING_INTERVAL, "400"));
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnDeviceLight = view.findViewById(R.id.spn_setDeviceLight_light);
                Spinner spnDeviceLightMode = view.findViewById(R.id.spn_setDeviceLight_mode);
                DeviceLight deviceLight = "MAG_STRIPE_LIGHT".equalsIgnoreCase(spnDeviceLight.getSelectedItem().toString()) ? DeviceLight.MAG_STRIPE : null;
                LightMode lightMode = "ON".equalsIgnoreCase(spnDeviceLightMode.getSelectedItem().toString()) ? LightMode.ON : LightMode.BLINK;
                EditText editDeviceLightFlashingInterval = view.findViewById(R.id.edit_setDeviceLight_flashingInterval);
                mEditor.putString(AppConfig.SharedPreferenceConfig.DEVICE_MANAGER_SET_DEVICE_LIGHT_FLASHING_INTERVAL, editDeviceLightFlashingInterval.getText().toString());
                mEditor.commit();
                int flashingInterval = Integer.parseInt(editDeviceLightFlashingInterval.getText().toString());

                try {
                    mDeviceManager.setDeviceLightMode(deviceLight, lightMode, flashingInterval);
                    showMessage(context.getString(R.string.msg_set_device_light_mode_success));
                } catch (NSDKException e) {
                    showErrorMessage(e, e.getMessage());
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_set_long_press_buttons, functionid = INDEX_SET_LONG_PRESS_BUTTON)
    private void setLongPressButtons() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_set_long_press_buttons), null, R.layout.dialog_set_long_press, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                RadioButton rbEnable = view.findViewById(R.id.rb_enable);
                boolean isLongPress = rbEnable.isChecked();

                int keyMask = 0;
                CheckBox cbAll = view.findViewById(R.id.cb_key_all);

                if (cbAll.isChecked()) {
                    keyMask = KeyboardButton.SYS_KEY_ALL;
                } else {
                    if (isChecked(view, R.id.cb_key_0)) keyMask |= KeyboardButton.SYS_KEY_0;
                    if (isChecked(view, R.id.cb_key_1)) keyMask |= KeyboardButton.SYS_KEY_1;
                    if (isChecked(view, R.id.cb_key_2)) keyMask |= KeyboardButton.SYS_KEY_2;
                    if (isChecked(view, R.id.cb_key_3)) keyMask |= KeyboardButton.SYS_KEY_3;
                    if (isChecked(view, R.id.cb_key_4)) keyMask |= KeyboardButton.SYS_KEY_4;
                    if (isChecked(view, R.id.cb_key_5)) keyMask |= KeyboardButton.SYS_KEY_5;
                    if (isChecked(view, R.id.cb_key_6)) keyMask |= KeyboardButton.SYS_KEY_6;
                    if (isChecked(view, R.id.cb_key_7)) keyMask |= KeyboardButton.SYS_KEY_7;
                    if (isChecked(view, R.id.cb_key_8)) keyMask |= KeyboardButton.SYS_KEY_8;
                    if (isChecked(view, R.id.cb_key_9)) keyMask |= KeyboardButton.SYS_KEY_9;
                    if (isChecked(view, R.id.cb_key_enter)) keyMask |= KeyboardButton.SYS_KEY_ENTER;
                    if (isChecked(view, R.id.cb_key_back)) keyMask |= KeyboardButton.SYS_KEY_BACK;
                }

                if (keyMask == 0) {
                    showMessage("Please select at least one key.");
                    return;
                }

                try {
                    mDeviceManager.setLongPressButtons(keyMask, isLongPress);
                    String statusStr = isLongPress ? "Enable" : "Disable";
                    String keyStr = cbAll.isChecked() ? "ALL KEYS" : String.format("Mask: 0x%04X", keyMask);
                    showMessage("Set LongPress Success.\nStatus: " + statusStr + "\nKeys: " + keyStr);
                } catch (NSDKException e) {
                    showErrorMessage(e, e.getMessage());
                }
            }

            private boolean isChecked(View parent, int viewId) {
                CheckBox cb = parent.findViewById(viewId);
                return cb != null && cb.isChecked();
            }
        });
    }

}
