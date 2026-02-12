package com.newland.nsdkdemo.external.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.external.devicemanager.BaudRateMode;
import com.newland.nsdk.core.api.external.devicemanager.BeeperControl;
import com.newland.nsdk.core.api.external.devicemanager.BluetoothInfo;
import com.newland.nsdk.core.api.external.devicemanager.DecryptionMode;
import com.newland.nsdk.core.api.external.devicemanager.DeviceConfiguration;
import com.newland.nsdk.core.api.external.devicemanager.DeviceConnectMode;
import com.newland.nsdk.core.api.external.devicemanager.ExtDeviceInfo;
import com.newland.nsdk.core.api.external.devicemanager.ExtDeviceManager;
import com.newland.nsdk.core.api.external.devicemanager.FileInfo;
import com.newland.nsdk.core.api.external.devicemanager.LogoType;
import com.newland.nsdk.core.api.external.devicemanager.TimeConfiguration;
import com.newland.nsdk.core.api.external.devicemanager.UpdateFiles;
import com.newland.nsdk.core.api.external.devicemanager.UpdateListener;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class ExtDeviceManagerFragment extends ExtBaseFragment {

    ExtDeviceManager mExtDeviceManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;

    private static final int INDEX_GET_VERSION = 1;
    private static final int INDEX_GET_SERIAL = 2;

    private static final int INDEX_GET_CONFIGURATION = 3;
    private static final int INDEX_SET_CONFIGURATION = 4;


    private static final int INDEX_SET_CONNECT_MODE = 5;
    private static final int INDEX_UPDATA_APP = 6;
    private static final int INDEX_UPDATA_FIRMWARE = 7;
    private static final int INDEX_REBOOT = 8;
    private static final int INDEX_GET_BATTERY_PER = 9;
    private static final int INDEX_SET_DATETIME = 10;
    private static final int INDEX_GET_DATETIME = 11;
    private static final int INDEX_GET_DEVICE_INFO = 12;
    private static final int INDEX_GET_BT_INFO = 13;
    private static final int INDEX_SET_BT_NAME = 14;
    private static final int INDEX_SET_POWER_ON_ICON = 15;
    private static final int INDEX_SET_POWER_OFF_ICON = 16;
    private static final int INDEX_SET_CHARGING_ICON = 17;
    private static final int INDEX_SET_TIME_CONFIG = 18;
    private static final int INDEX_GET_TIME_CONFIG = 19;
    private static final int INDEX_SET_LANGUAGE = 20;
    private static final int INDEX_GET_LANGUAGE = 21;
    private static final int INDEX_GET_FILE_LIST = 22;

    private BaudRateMode baudRateMode;
    private DecryptionMode decryptionMode;
    private BeeperControl beeperControl;

    public ExtDeviceManagerFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_extdevicebasic_f);
    }

    @Override
    public void initData() {
        mExtDeviceManager = (ExtDeviceManager) moduleManager.getModule(ModuleType.EXT_DEVICE_MANAGER);
        sharedPreferences = context.getSharedPreferences("EXT_DEVICEMANAGER", Context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
    }

    @Override
    public Object getModule() {
        return ExtDeviceManagerFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_getversionnumber, functionid = INDEX_GET_VERSION)
    private void getVersionNum() {
        try {
            String result = mExtDeviceManager.getVersionNumber();
            showMessage(context.getString(R.string.msg_extdevicebasic_getversionsuccess) + result, MessageTag.NORMAL);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.msg_extdevicebasic_getversionfail));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_getserialnumber, functionid = INDEX_GET_SERIAL)
    private void getSerialNum() {
        try {
            String result = mExtDeviceManager.getSerialNumber();
            showMessage(context.getString(R.string.msg_extdevicebasic_getserialnumbersuccess) + result, MessageTag.NORMAL);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "get serial number");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_getconfiguration, functionid = INDEX_GET_CONFIGURATION)
    private void getConfiguration() {
        try {
            DeviceConfiguration configuration = mExtDeviceManager.getDeviceConfiguration();
            BaudRateMode baud = configuration.getBaudRateMode();
            BeeperControl beeperControl = configuration.getBeeperControl();
            DecryptionMode decryptionMode = configuration.getWorkingKeyDecryptionMode();
            showMessage(context.getString(R.string.msg_extdevicebasic_getconfigurationsuccess), MessageTag.NORMAL);
            showMessage(context.getString(R.string.msg_extdevicebasic_getbaudrate) + baud, MessageTag.NORMAL);
            showMessage(context.getString(R.string.msg_extdevicebasic_getbeepcontrol) + beeperControl, MessageTag.NORMAL);
            showMessage(context.getString(R.string.msg_extdevicebasic_getdecryptionmodel) + decryptionMode, MessageTag.NORMAL);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "get configuration");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_setconfiguration, functionid = INDEX_SET_CONFIGURATION)
    private void setConfiguration() {
        DialogUtils.createCustomDialog(context, R.string.tv_extdevicebasic_setconfiguration, null, R.layout.dialog_ext_devicemanager, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Spinner spnBeeperControl = view.findViewById(R.id.dialog_ext_devicemanager_beeperControl);
                Spinner spnDecryptionMode = view.findViewById(R.id.dialog_ext_devicemanager_decryptionMode);
                Spinner spnBaudRateMode = view.findViewById(R.id.dialog_ext_devicemanager_baudRateMode);

                spnBeeperControl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DEVICEMANAGER_BEEPER_CONTROL, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int beeperControl = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DEVICEMANAGER_BEEPER_CONTROL, 0);
                spnBeeperControl.setSelection(beeperControl);

                spnDecryptionMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DEVICEMANAGER_DECRYPTION_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int decryptionMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DEVICEMANAGER_DECRYPTION_MODE, 0);
                spnDecryptionMode.setSelection(decryptionMode);

                spnBaudRateMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DEVICEMANAGER_BAUD_RATE_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int baudRateMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DEVICEMANAGER_BAUD_RATE_MODE, 0);
                spnBaudRateMode.setSelection(baudRateMode);
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnBeeperControl = view.findViewById(R.id.dialog_ext_devicemanager_beeperControl);
                String tmpStr = spnBeeperControl.getSelectedItem().toString();
                BeeperControl beeperControl = null;
                for (BeeperControl bc : BeeperControl.values()) {
                    if(bc.name().equals(tmpStr)) {
                        beeperControl = bc;
                        break;
                    }
                }


                Spinner spnDecryptionMode = view.findViewById(R.id.dialog_ext_devicemanager_decryptionMode);
                tmpStr = spnDecryptionMode.getSelectedItem().toString();
                DecryptionMode decryptionMode = null;
                for (DecryptionMode dm : DecryptionMode.values()) {
                    if(dm.name().equals(tmpStr)) {
                        decryptionMode = dm;
                        break;
                    }
                }

                Spinner spnBaudRateMode = view.findViewById(R.id.dialog_ext_devicemanager_baudRateMode);
                tmpStr = spnBaudRateMode.getSelectedItem().toString();
                BaudRateMode baudRateMode = null;
                for (BaudRateMode brm : BaudRateMode.values()) {
                    if(brm.name().equals(tmpStr)) {
                        baudRateMode = brm;
                        break;
                    }
                }


                try {
                    DeviceConfiguration configuration = new DeviceConfiguration();
                    configuration.setBaudRateMode(baudRateMode);
                    configuration.setBeeperControl(beeperControl);
                    configuration.setWorkingKeyDecryptionMode(decryptionMode);
                    mExtDeviceManager.setDeviceConfiguration(configuration);
                    showMessage(String.format("%s: %s, %s, %s", context.getString(R.string.tv_extdevicebasic_setconfiguration), baudRateMode, beeperControl, decryptionMode), MessageTag.NORMAL);
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, "set configuration");
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_setconnectmode, functionid = INDEX_SET_CONNECT_MODE)
    private void setConnectMode() {
        String[] connectModes = {"USB", "JustWork", "SSP", "Passkey"};
        DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.tv_extdevicebasic_setconnectmode), connectModes, id -> {
            try {
                DeviceConnectMode[] modes = DeviceConnectMode.values();
                for (DeviceConnectMode m: modes) {
                    if (m.ordinal() == id) {
                        mExtDeviceManager.setConnectMode(m);
                        showMessage(String.format("%s is set successfully.", connectModes[id]));
                        break;
                    }
                }
            } catch (NSDKException e) {
                e.printStackTrace();
                showErrorMessage(e, context.getString(R.string.tv_extdevicebasic_setconnectmode));
            }
            showMessage("Device will be disconnected if mode is changed and set successfully. Please check device.");
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_updateapp, functionid = INDEX_UPDATA_APP)
    private void updateApp() {
        AssetManager assetManager = context.getAssets();
        // ME30S
        String appName = "app/mapp_ME30S_PinPad_Dev.NLP";
//        String firmwareName = "app/master_thm3682_me30su_20210708_14.NLP";
        try {
            showMessage(String.format("Loading app: %s ...", appName));
            InputStream is = assetManager.open(appName);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);

//            showMessage(String.format("Loading firmware: %s ...", firmwareName));
//            is = assetManager.open(firmwareName);
//            length = is.available();
//            byte[] firmwareBuffer = new byte[length];
//            is.read(firmwareBuffer);

            UpdateFiles updateFiles = new UpdateFiles();
            updateFiles.setApplicationFile(buffer);
//            updateFiles.setFirmwareFile(firmwareBuffer);
            mExtDeviceManager.update(updateFiles, new UpdateListener() {
                @Override
                public void onError(int i, String s) {
                    showMessage(String.format("Failed to load file: %s(%d)", s, i), MessageTag.ERROR);
                }

                @Override
                public void onFileTransferProgress(int i) {
                    updateFirstLineMessage(String.format("File is loading: %d%%", i));
                    if (i == 100) {
                        showMessage(String.format("%s is loaded successfully.", appName));
                    }
                }

                @Override
                public void onComplete() {
                    showMessage("Update is started. Please check device to see if it is updated successfully.");
                }
            });
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, String.format("update app: %s", appName));
        } catch (IOException e) {
            e.printStackTrace();
            showMessage(String.format("Failed to update app: %s", appName));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_updatefirmware, functionid = INDEX_UPDATA_FIRMWARE)
    private void updateFirmware() {
        AssetManager assetManager = context.getAssets();
        // ME30S
        String firmwareName = "app/master_thm3682_me30su_20210708_14.NLP";
        try {
            showMessage(String.format("Loading firmware: %s ...", firmwareName));
            InputStream is = assetManager.open(firmwareName);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            is.reset();

            UpdateFiles updateFiles = new UpdateFiles();
            updateFiles.setFirmwareFile(buffer);
            mExtDeviceManager.update(updateFiles, new UpdateListener() {
                @Override
                public void onError(int i, String s) {
                    showMessage(String.format("Failed to load file: %s(%d)", s, i), MessageTag.ERROR);
                }

                @Override
                public void onFileTransferProgress(int i) {
                    updateFirstLineMessage(String.format("File is loading: %d%%", i));
                    if (i == 100) {
                        showMessage(String.format("%s is loaded successfully.", firmwareName));
                    }
                }

                @Override
                public void onComplete() {
                    showMessage("Update is started. Please check device to see if it is updated successfully.");
                }
            });
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, String.format("update firmware: %s", firmwareName));
        } catch (IOException e) {
            e.printStackTrace();
            showMessage(String.format("Failed to update firmware: %s", firmwareName));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_reboot, functionid = INDEX_REBOOT)
    private void reboot() {
        try {
            mExtDeviceManager.reboot();
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "reboot");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_getbattery, functionid = INDEX_GET_BATTERY_PER)
    private void getBatteryPert() {
        try {
            int pert = mExtDeviceManager.getBatteryPercentage();
            showMessage(context.getString(R.string.tv_extdevicebasic_getbattery)+":"+pert, MessageTag.NORMAL);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "get battery percent");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_setdatetime, functionid = INDEX_SET_DATETIME)
    private void setDatetime() {
        try {
            mExtDeviceManager.setDatetime("20210915152924");
            showMessage("Datetime is set successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "set datetime");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_getdatetime, functionid = INDEX_GET_DATETIME)
    private void getDatetime() {
        try {
            String datetime = mExtDeviceManager.getDatetime();
            showMessage(String.format("Datetime is %s", datetime));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "get datetime");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_getdeviceinfo, functionid = INDEX_GET_DEVICE_INFO)
    private void getDeviceInfo() {
        try {
            ExtDeviceInfo deviceInfo = mExtDeviceManager.getDeviceInfo();
            showMessage(String.format("Software Version:\n %s", deviceInfo.getSoftwareVersion()));
            showMessage(String.format("POS SN:\n %s", deviceInfo.getPosSN()));
            showMessage(String.format("POS PN:\n %s", deviceInfo.getPosPN()));
            showMessage(String.format("Build OS Version:\n %s", deviceInfo.getBuildOSVersion()));
            showMessage(String.format("Hardware:\n %s", deviceInfo.getHardware()));
            showMessage(String.format("NAPI API Version:\n %s", deviceInfo.getNapiAPIVersion()));
            showMessage(String.format("NAPI Lib Version:\n %s", deviceInfo.getNapiLibVersion()));
            showMessage(String.format("Build Boot Version:\n %s", deviceInfo.getBuildBootVersion()));
            showMessage(String.format("Build DevCFG Version:\n %s", deviceInfo.getBuildDevCFGVersion()));
            showMessage(String.format("Model:\n %s", deviceInfo.getModel()));
            showMessage(String.format("Build PCI Firmware Version:\n %s", deviceInfo.getBuildPCIFirmwareVersion()));
            showMessage(String.format("Build PCI Hardware Version:\n %s", deviceInfo.getBuildPCIHardwareVersion()));
            showMessage(String.format("POS CPU Type:\n %s", deviceInfo.getPosCPUType()));
            showMessage(String.format("POS Board Version:\n %s", deviceInfo.getPosBoardVersion()));
            showMessage(String.format("POS Board Number:\n %s", deviceInfo.getPosBoardNumber()));
            showMessage(String.format("RF Type:\n %s", deviceInfo.getRfType()));
            showMessage(String.format("RF Version:\n %s", deviceInfo.getRfVersion()));
            showMessage(String.format("WIFI Driver Version:\n %s", deviceInfo.getWifiDrvVersion()));
            showMessage("Support Smart Card:" + deviceInfo.getDeviceAttribute().isSupportSmartCard());
            showMessage("Support Contactless Card:" + deviceInfo.getDeviceAttribute().isSupportContactlessCard());
            showMessage("Support Magnetic Card:" + deviceInfo.getDeviceAttribute().isSupportMagCard());
            showMessage("Support Graphical Display:" + deviceInfo.getDeviceAttribute().isSupportGraphicalDisplay());
            showMessage("Support Colour Display:" + deviceInfo.getDeviceAttribute().isSupportColourDisplay());
            showMessage("Support Beeper:" + deviceInfo.getDeviceAttribute().isSupportBeeper());
            showMessage("Support Back Light:" + deviceInfo.getDeviceAttribute().isSupportBacklight());
            showMessage(String.format(Locale.US, "PCIFirmwareID:%s", deviceInfo.getDeviceAttribute().getPciFirmwareID()));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "get device info");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_getbtinfo, functionid = INDEX_GET_BT_INFO)
    private void getBTInfo() {
        try {
            BluetoothInfo info = mExtDeviceManager.getBluetoothInfo();
            showMessage(String.format("BT Name: %s", info.getName()));
            showMessage(String.format("BT MAC Address: %s", info.getMacAddress()));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "get bluetooth info");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_setbtname, functionid = INDEX_SET_BT_NAME)
    private void setBTName() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_extdevicebasic_setbtname), null, R.layout.dialog_bt_name, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                EditText etBTName = dialogView.findViewById(R.id.et_set_bt_name);
                try {
                    String btName = etBTName.getText().toString();
                    mExtDeviceManager.setBluetoothName(btName);
                    showMessage("Please check external device if bluetooth name is set successfully.");
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, context.getString(R.string.tv_extdevicebasic_setbtname));
                }
            }
        });
    }
    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_setfullychargedicon, functionid = INDEX_SET_POWER_ON_ICON)
    private void setFullyChargedIcon(){
        AssetManager assetManager = context.getAssets();
        String iconPath = "icon/desligando";
        try {
            showMessage(String.format("Loading icon: %s ...", iconPath));
            InputStream is = assetManager.open(iconPath);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            is.close();

            mExtDeviceManager.setLogoIcon(LogoType.POWER_OFF_FULLY_CHARGED, buffer, new UpdateListener() {
                @Override
                public void onError(int i, String s) {
                    showMessage(String.format("Failed to load file: %s(%d)", s, i), MessageTag.ERROR);
                }

                @Override
                public void onFileTransferProgress(int i) {
                    updateFirstLineMessage(String.format("File is loading: %d%%", i));
                    if (i == 100) {
                        showMessage(String.format("%s is loaded successfully.", iconPath));
                    }
                }

                @Override
                public void onComplete() {
                    showMessage("Please check device to see if it is set successfully.");
                }
            });
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, String.format("set power on icon: %s", iconPath));
        } catch (IOException e) {
            e.printStackTrace();
            showMessage(String.format("Failed to set power on icon: %s", iconPath));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_setpowerofficon, functionid = INDEX_SET_POWER_OFF_ICON)
    private void setPowerOffIcon(){
        AssetManager assetManager = context.getAssets();
        String iconPath = "icon/pwroffbmp";
        try {
            showMessage(String.format("Loading icon: %s ...", iconPath));
            InputStream is = assetManager.open(iconPath);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            is.close();

            mExtDeviceManager.setLogoIcon(LogoType.POWER_OFF, buffer, new UpdateListener() {
                @Override
                public void onError(int i, String s) {
                    showMessage(String.format("Failed to load file: %s(%d)", s, i), MessageTag.ERROR);
                }

                @Override
                public void onFileTransferProgress(int i) {
                    updateFirstLineMessage(String.format("File is loading: %d%%", i));
                    if (i == 100) {
                        showMessage(String.format("%s is loaded successfully.", iconPath));
                    }
                }

                @Override
                public void onComplete() {
                    showMessage("Please check device to see if it is set successfully.");
                }
            });
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, String.format("set power off icon: %s", iconPath));
        } catch (IOException e) {
            e.printStackTrace();
            showMessage(String.format("Failed to set power off icon: %s", iconPath));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_setchargingicon, functionid = INDEX_SET_CHARGING_ICON)
    private void setChargingIcon(){
        AssetManager assetManager = context.getAssets();
        String iconPath = "icon/chargingbmp";
        try {
            showMessage(String.format("Loading icon: %s ...", iconPath));
            InputStream is = assetManager.open(iconPath);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            is.close();

            mExtDeviceManager.setLogoIcon(LogoType.POWER_OFF_CHARGING, buffer, new UpdateListener() {
                @Override
                public void onError(int i, String s) {
                    showMessage(String.format("Failed to load file: %s(%d)", s, i), MessageTag.ERROR);
                }

                @Override
                public void onFileTransferProgress(int i) {
                    updateFirstLineMessage(String.format("File is loading: %d%%", i));
                    if (i == 100) {
                        showMessage(String.format("%s is loaded successfully.", iconPath));
                    }
                }

                @Override
                public void onComplete() {
                    showMessage("Please check device to see if it is set successfully.");
                }
            });
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, String.format("set charging icon: %s", iconPath));
        } catch (IOException e) {
            e.printStackTrace();
            showMessage(String.format("Failed to set charging icon: %s", iconPath));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_settimeconfig, functionid = INDEX_SET_TIME_CONFIG)
    private void setTimeConfig(){
        try {
            TimeConfiguration configuration = new TimeConfiguration();
            configuration.setAutoBacklightOffTime(15);
            configuration.setAutoSleepTime(60);
            configuration.setAutoTurnOffTime(300);
            mExtDeviceManager.setTimeConfiguration(configuration);
            showMessage("Time configuration is set successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "set time configuration");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_gettimeconfig, functionid = INDEX_GET_TIME_CONFIG)
    private void getTimeConfig(){
        try {
            TimeConfiguration configuration = mExtDeviceManager.getTimeConfiguration();
            showMessage(String.format("Auto backlight-off time: %s", configuration.getAutoBacklightOffTime()));
            showMessage(String.format("Auto sleep time: %s", configuration.getAutoSleepTime()));
            showMessage(String.format("Auto turn-off time: %s", configuration.getAutoTurnOffTime()));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "get time configuration");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_getlanguage, functionid = INDEX_GET_LANGUAGE)
    private void getLanguage() {
        try {
            String language = mExtDeviceManager.getLanguage();
            showMessage(String.format("Current language: %s", language));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "get language");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_setlanguage, functionid = INDEX_SET_LANGUAGE)
    private void setLanguage(){
        AssetManager assetManager = context.getAssets();
        String filePath = "language/String.xml";
        try {
            showMessage(String.format("Loading language file: %s ...", filePath));
            InputStream is = assetManager.open(filePath);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            is.close();

            mExtDeviceManager.setLanguage(buffer, new UpdateListener() {
                @Override
                public void onError(int i, String s) {
                    showMessage(String.format("Failed to load file: %s(%d)", s, i), MessageTag.ERROR);
                }

                @Override
                public void onFileTransferProgress(int i) {
                    updateFirstLineMessage(String.format("File is loading: %d%%", i));
                    if (i == 100) {
                        showMessage(String.format("%s is loaded successfully.", filePath));
                    }
                }

                @Override
                public void onComplete() {
                    showMessage("Please check device to see if it is set successfully.");
                }
            });
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, String.format("set language: %s", filePath));
        } catch (IOException e) {
            e.printStackTrace();
            showMessage(String.format("Failed to set language: %s", filePath));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extdevicebasic_getfilelist, functionid = INDEX_GET_FILE_LIST)
    private void getFileList() {
        try {
            ArrayList<FileInfo> fileInfos = mExtDeviceManager.getFileList(".png", null);
            for (FileInfo fileInfo : fileInfos) {
                showMessage("FileName:" + fileInfo.getName());
                showMessage("Content:" + new String(fileInfo.getInfo()));
            }
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.tv_extdevicebasic_getfilelist));
        }
    }

}
