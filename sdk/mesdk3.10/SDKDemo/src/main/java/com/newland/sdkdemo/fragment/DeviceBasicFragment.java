package com.newland.sdkdemo.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.newland.os.NlBuild;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.newland.sdk.ModuleManage;
import com.newland.sdk.module.devicebasic.DeviceBasicModule;
import com.newland.sdk.module.devicebasic.DeviceInfo;
import com.newland.sdk.utils.TLVPackage;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

/**
 * Author by bxy, Date on 2019/5/11 0011.
 */
public class DeviceBasicFragment extends BaseFragment {
    private DeviceBasicModule deviceBasicModule;
    private static final int INDEX_TERMINAL_SETTIME = 1;
    private static final int INDEX_TERMINAL_GETTIME = 2;
    private static final int INDEX_TERMINAL_SETPARAM = 3;
    private static final int INDEX_TERMINAL_GETPARAM = 4;
    private static final int INDEX_TERMINAL_RESET = 5;
    private static final int INDEX_TERMINAL_BACKTODESKTOP = 6;
    private static final int INDEX_GET_DEVICEINFO = 7;
    private static final int INDEX_GET_RANDOM = 8;
    private static final int INDEX_GET_TUSN = 9;
    private static final int INDEX_SET_CSN = 10;
    private static final int INDEX_SDK_VERSION = 11;

    public DeviceBasicFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_terminalmanage_f);
    }

    @Override
    public void initData() {
        deviceBasicModule = moduleManage.getDeviceBasicModule();
    }

    @Override
    public Object getModule() {
        return DeviceBasicFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_time_and_date_set, functionid = INDEX_TERMINAL_SETTIME)
    private void setTerminalTime() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date date = formatter.parse("2019-11-11 11:11:11");
            deviceBasicModule.setDeviceDate(date);
            showMessage(context.getString(R.string.msg_time_set_success), MessageTag.NORMAL);
        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_time_set_error) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_time_and_date_get, functionid = INDEX_TERMINAL_GETTIME)
    private void getTerminalTime() {
        try {
            Date getDate = deviceBasicModule.getDeviceDate();
            showMessage(context.getString(R.string.msg_date_set_success) + formatDate(getDate), MessageTag.NORMAL);
        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_time_get_error) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_terminal_parameter_set, functionid = INDEX_TERMINAL_SETPARAM)
    private void setParam() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_terminal_parameter_set), null, R.layout.dialog_terminal_param, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    String tag = ((EditText) dialogView.findViewById(R.id.edit_param_tag)).getText().toString();
                    String data = ((EditText) dialogView.findViewById(R.id.edit_param_value)).getText().toString();
                    int param_tag = Integer.parseInt(tag.substring(2), 16);
                    showMessage("param_tag:" + param_tag, MessageTag.NORMAL);

                    byte[] param_value = data.getBytes("gbk");
                    TLVPackage tlvpackage = ISOUtils.newTlvPackage();
                    tlvpackage.append(param_tag, param_value);
                    deviceBasicModule.setDeviceParams(tlvpackage);
                    showMessage(context.getString(R.string.msg_set_parameters_success), MessageTag.NORMAL);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_parameters_input_error) + e.getMessage(), MessageTag.ERROR);
                }

            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_terminal_parameter_get, functionid = INDEX_TERMINAL_GETPARAM)
    private void getTerminalParam() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_terminal_parameter_get), null, R.layout.dialog_get_terminal_param, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if (id == -1) {//cancel
                        return;
                    }
                    String tag = ((EditText) dialogView.findViewById(R.id.edit_get_param_tag)).getText().toString();
                    int param_tag = Integer.parseInt(tag.substring(2), 16);
                    TLVPackage pack = (TLVPackage) deviceBasicModule.getDeviceParams(param_tag);
                    byte[] param_value = pack.getValue(getOrginTag(param_tag));
                    if (param_value != null) {
                        showMessage(context.getString(R.string.msg_parameters_get_success), MessageTag.TIP);
                        showMessage(context.getString(R.string.common_tag) + tag, MessageTag.DATA);
                        showMessage(context.getString(R.string.common_value) + new String(param_value, context.getString(R.string.msg_charsetName_gbk)), MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.msg_parameter_not_exist), MessageTag.TIP);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_parameter_get_error) + e.getMessage(), MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_cancel_and_reset_operation, functionid = INDEX_TERMINAL_RESET)
    private void reset() {
        try {
            BaseFragment.setFunRunning(false);
            deviceBasicModule.reset();
            showMessage(context.getString(R.string.msg_revoke_current_order_success), MessageTag.NORMAL);

        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_revoke_current_order_error), MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_backtodesktop, functionid = INDEX_TERMINAL_BACKTODESKTOP)
    private void backToDesktop() {
        ComponentName cName;
        if (NlBuild.VERSION.MODEL.equals("CPOS X5")||
                android.os.Build.MODEL.equals("STAR A-6300") ||
                NlBuild.VERSION.MODEL.equals("N910 Pro")) {
            cName = new ComponentName("com.android.launcher3", "com.android.launcher3.Launcher");
        } else {
            cName = new ComponentName("com.android.launcher", "com.android.launcher2.Launcher");
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cName);
        context.startActivity(intent);
    }

    @MethodGridEntity(btnnameid = R.string.tv_get_device_info, functionid = INDEX_GET_DEVICEINFO)
    private void getDeviceInfo() {
        try {
            showMessage(context.getString(R.string.msg_get_device_info_begin) + "\r\n", MessageTag.NORMAL);
            DeviceInfo deviceInfo = deviceBasicModule.getDeviceInfo();
            showMessage("PN:"+deviceInfo.getPN(),MessageTag.DATA);
            showMessage("custom_id:" + deviceInfo.getCustomerID() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_devcie_CSN_NO) + (deviceInfo.getCSN() == null ? null : new String(deviceInfo.getCSN(), "gbk")) + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_app_version) + deviceInfo.getAppVer() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_BOOT_version_NO) + deviceInfo.getBootVersion() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_devcie_type) + deviceInfo.getModel() + "\r\n", MessageTag.DATA);
            showMessage("tusn:" + deviceBasicModule.getTusn(), MessageTag.DATA);

            showMessage(context.getString(R.string.msg_devcie_SN_NO) + deviceInfo.getSN(), MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_audio) + deviceInfo.isSupportAudio() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_Bluetooth) + deviceInfo.isSupportBlueTooth() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_ICcard) + deviceInfo.isSupportICCard() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_LCD) + deviceInfo.isSupportLCD() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_MagCard) + deviceInfo.isSupportMagCard() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_Offline) + deviceInfo.isSupportOffLine() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_Printe) + deviceInfo.isSupportPrint() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_RFCard) + deviceInfo.isSupportQuickPass() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_device_isSupport_USB) + deviceInfo.isSupportUSB() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.is_sup_gps) + deviceInfo.isSupportGPS() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.is_sup_pinpad) + deviceInfo.isSupportPinpadPort() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.is_sup_rs232) + deviceInfo.isSupport232Port() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.is_sup_ethernet) + deviceInfo.isSupportEthernet() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.is_sup_sam) + deviceInfo.isSupportSam() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.is_sup_cashbox) + deviceInfo.isSupportCashBox() + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.is_sup_carama) + deviceInfo.isSupportCamera() + "\r\n", MessageTag.DATA);
            showMessage("PCI verison:" + deviceInfo.getPCIVersion()+ "\r\n", MessageTag.DATA);

            showMessage("NL_HARDWARE_ID:" + NlBuild.VERSION.NL_HARDWARE_ID + "\r\n", MessageTag.DATA);
            showMessage("NL_FIRMWARE:" + NlBuild.VERSION.NL_FIRMWARE + "\r\n", MessageTag.DATA);
            showMessage("NlBuild.VERSION.NL_HARDWARE_CONFIG:"+NlBuild.VERSION.NL_HARDWARE_CONFIG);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_get_device_info_error) + e + "\r\n", MessageTag.ERROR);
        }
    }


    @MethodGridEntity(btnnameid = R.string.tv_get_random, functionid = INDEX_GET_RANDOM)
    private void getRandom() {
        showMessage(context.getString(R.string.msg_random) + "\r\n", MessageTag.NORMAL);
        DialogUtils.createCustomDialog(context, context.getString(R.string.dialog_set_random_len), null, R.layout.dialog_get_random, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                try {
                    EditText editText = view.findViewById(R.id.edit_len);
                    int len = Integer.valueOf(editText.getText().toString());
                    byte[] randomData = deviceBasicModule.getRandom(len);
                    showMessage(context.getString(R.string.msg_random_result) + (randomData == null ? null : Dump.getHexDump(randomData)) + "\r\n", MessageTag.DATA);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_error) + e + "\r\n", MessageTag.ERROR);
                }

            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_get_tusn, functionid = INDEX_GET_TUSN)
    private void getTusn() {
        try {
            showMessage(context.getString(R.string.msg_get_tusn) + "\r\n", MessageTag.NORMAL);
            String tusn = deviceBasicModule.getTusn();
            showMessage(context.getString(R.string.msg_get_tusn_result) + tusn + "\r\n", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e + "\r\n", MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_set_csn, functionid = INDEX_SET_CSN)
    private void setCsn() {
        try {
            try {
                showMessage(context.getString(R.string.msg_random) + "\r\n", MessageTag.NORMAL);
                DialogUtils.createCustomDialog(context, context.getString(R.string.dialog_set_csn), null, R.layout.dialog_set_csn, new DialogUtils.CustomDialogCallback() {
                    @Override
                    public void onResult(int id, View view) {
                        try {
                            EditText editText = view.findViewById(R.id.edit_csn);
                            String csn = editText.getText().toString();
                            deviceBasicModule.setCSN(csn);
                            showMessage(context.getString(R.string.msg_set_csn_complete) + "\r\n", MessageTag.DATA);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(context.getString(R.string.msg_error) + e + "\r\n", MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e + "\r\n", MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_get_sdkversion, functionid = INDEX_SDK_VERSION)
    private void getSDKVersion() {
        try {
            String version = deviceBasicModule.getSDKVersion();
            showMessage("version:"+version);
        }catch (Exception e){
            e.printStackTrace();
            showMessage("exception:"+e,MessageTag.ERROR);
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String dateString = formatter.format(date);
        return dateString;
    }

    private int getOrginTag(int tag) {
        if ((tag & 0xFF0000) == 0xFF0000) {
            return tag & 0xFFFF;
        } else if ((tag & 0xFF00) == 0xFF00) {
            return tag & 0xFF;
        }
        return tag;
    }
}
