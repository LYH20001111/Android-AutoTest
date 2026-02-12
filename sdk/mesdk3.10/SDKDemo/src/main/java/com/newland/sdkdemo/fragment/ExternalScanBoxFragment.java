package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.newland.ndk.NdkApiManager;
import com.newland.sdk.module.externalScan.ExtScanBoxModule;
import com.newland.sdk.module.externalScan.ResultListener;
import com.newland.sdk.module.externalScan.ScanBoxDevParams;
import com.newland.sdk.module.externalScan.ScanBoxInitExtParams;
import com.newland.sdk.module.externalScan.ScanBoxLight;
import com.newland.sdk.module.externalScan.ScanBoxParams;
import com.newland.sdk.module.externalScan.StartScanExtParams;
import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.PortType;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Author by bxy, Date on 2019/5/11 0011.
 */
public class ExternalScanBoxFragment extends BaseFragment {

    private ExtScanBoxModule scanBoxModule;
    public ExternalScanBoxFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_external_scan);
    }

    @Override
    public void initData() {
        scanBoxModule = moduleManage.getExtScanBoxModule();
    }

    @Override
    public Object getModule() {
        return ExternalScanBoxFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_init,functionid = 1)
    private void init(){
        showMessage(context.getString(R.string.set_comm_param), MessageTag.TIP);
        DialogUtils.createSingleChoiceDialog(context, "select device type", new String[]{"pos UART port", "POS USB port","bluetoothBase device USB1 port","bluetoothBase device USB2 port"}, new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                if(id<0){
                    return;
                }
                if(id==0){
                    boolean init = scanBoxModule.init(new ScanBoxInitExtParams(ScanBoxInitExtParams.CommMode.UART, PortType.RS232, Baudrate.BPS115200));
                    if (init) {
                        showMessage(context.getString(R.string.set_comm_param_sucess), MessageTag.DATA);
                    }else {
                        showMessage(context.getString(R.string.set_comm_param_fail), MessageTag.ERROR);
                    }
                }else if(id==1){
                    boolean init = scanBoxModule.init(new ScanBoxInitExtParams(ScanBoxInitExtParams.CommMode.USB, PortType.RS232, Baudrate.BPS115200));
                    if (init) {
                        showMessage(context.getString(R.string.set_comm_param_sucess), MessageTag.DATA);
                    }else {
                        showMessage(context.getString(R.string.set_comm_param_fail), MessageTag.ERROR);
                    }
                }else if(id==2){
                    ScanBoxInitExtParams scanBoxInitExtParams =new ScanBoxInitExtParams("","",PortType.BLEBASE_USB1);
                    boolean init = scanBoxModule.init(scanBoxInitExtParams);
                    if (init) {
                        showMessage(context.getString(R.string.set_comm_param_sucess), MessageTag.DATA);
                    }else {
                        showMessage(context.getString(R.string.set_comm_param_fail), MessageTag.ERROR);
                    }

                }else if(id==3){
                    ScanBoxInitExtParams scanBoxInitExtParams =new ScanBoxInitExtParams("","",PortType.BLEBASE_USB2);
                    boolean init = scanBoxModule.init(scanBoxInitExtParams);
                    if (init) {
                        showMessage(context.getString(R.string.set_comm_param_sucess), MessageTag.DATA);
                    }else {
                        showMessage(context.getString(R.string.set_comm_param_fail), MessageTag.ERROR);
                    }
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.tv_set_system_params,functionid = 2)
    private void setParams(){
        try {
            boolean result = false;
            showMessage(context.getString(R.string.set_sanner_device_param), MessageTag.TIP);
//            result = scanBoxModule.setParams(ScanBoxDevParams.SN, "12345678扫码");
//            if (result) {
//                showMessage(context.getString(R.string.set_sn_param_sucess), MessageTag.DATA);
//            } else {
//                showMessage(context.getString(R.string.set_sn_param_fail), MessageTag.ERROR);
//            }
//            result = scanBoxModule.setParams(ScanBoxDevParams.PN, "12345678扫码123");
//
//            if (result) {
//                showMessage(context.getString(R.string.set_pn_param_sucess), MessageTag.DATA);
//            } else {
//                showMessage(context.getString(R.string.set_pn_param_fail), MessageTag.ERROR);
//            }

            result = scanBoxModule.setParams(ScanBoxDevParams.CSN, "12345678扫码16");
            if (result) {
                showMessage(context.getString(R.string.set_cn_param_sucess), MessageTag.DATA);
            } else {
                showMessage(context.getString(R.string.set_cn_param_fail), MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error)+e,MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_get_system_params,functionid = 3)
    private void getParams(){
        try {
            showMessage(context.getString(R.string.get_scan_device_param), MessageTag.TIP);
            Map<String, String> params = scanBoxModule.getParams(new ScanBoxDevParams[]{ScanBoxDevParams.SN, ScanBoxDevParams.PN, ScanBoxDevParams.CSN,
                    ScanBoxDevParams.PID, ScanBoxDevParams.VID, ScanBoxDevParams.APP, ScanBoxDevParams.MASTER, ScanBoxDevParams.BOOT});
            showMessage(context.getString(R.string.scan_device_sn) + params.get("SN"), MessageTag.DATA);
            showMessage(context.getString(R.string.scan_device_pn) + params.get("PN"), MessageTag.DATA);
            showMessage(context.getString(R.string.scan_device_csn) + params.get("CSN"), MessageTag.DATA);
            showMessage(context.getString(R.string.scan_device_pid) + params.get("PID"), MessageTag.DATA);
            showMessage(context.getString(R.string.scan_device_vid) + params.get("VID"), MessageTag.DATA);
            showMessage(context.getString(R.string.scan_device_app) + params.get("APP"), MessageTag.DATA);
            showMessage(context.getString(R.string.scan_device_master) + params.get("MASTER"), MessageTag.DATA);
            showMessage(context.getString(R.string.scan_device_boot) + params.get("BOOT"), MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error)+e,MessageTag.ERROR);
        }
    }
    @MethodGridEntity(btnnameid = R.string.tv_scan_set_param,functionid = 4)
    private void setScanParams(){
        DialogUtils.createCustomDialog(context, R.string.tv_scan_set_param, null, R.layout.dialog_ext_scan_setparam, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
            }

            @Override
            public void onResult(int id, View view) {
                CheckBox cb_backLight = view.findViewById(R.id.cb_backlight);
                EditText et_setVolume = view.findViewById(R.id.et_setvolume);
                CheckBox cb_lightSwitch = view.findViewById(R.id.cb_light_switch);
                CheckBox cb_red = view.findViewById(R.id.cb_red);
                CheckBox cb_green = view.findViewById(R.id.cb_green);
                CheckBox cb_blue = view.findViewById(R.id.cb_blue);
                CheckBox cb_yellow = view.findViewById(R.id.cb_yellow);

                try {
                    ScanBoxParams params = new ScanBoxParams();
                    params.setBackLight(cb_backLight.isChecked());
                    params.setVolume(Integer.parseInt(et_setVolume.getText().toString()));
                    List<ScanBoxLight> lights = new ArrayList<>();
                    if (cb_red.isChecked()) {
                        lights.add(ScanBoxLight.RED);
                    }
                    if (cb_blue.isChecked()) {
                        lights.add(ScanBoxLight.BLUE);
                    }
                    if (cb_green.isChecked()) {
                        lights.add(ScanBoxLight.GREEN);
                    }
                    if (cb_yellow.isChecked()) {
                        lights.add(ScanBoxLight.YELLOW);
                    }
                    if (lights.size() != 0) {
                        ScanBoxParams.ScanLightStatus status = new ScanBoxParams.ScanLightStatus();
                        status.setLightColor(lights.toArray(new ScanBoxLight[lights.size()]));
                        status.setTurnOn(cb_lightSwitch.isChecked());
                        params.setScanLightStatus(status);
                        showMessage(context.getString(R.string.ext_scan_light) + "：" + (cb_lightSwitch.isChecked() ? context.getString(R.string.ext_keyboard_turn_on) : context.getString(R.string.ext_keyboard_turn_off))
                                + "，" +Arrays.toString(lights.toArray(new ScanBoxLight[lights.size()])), MessageTag.DATA);
                    }

                    params.setSuffix("END");
                    params.setPrefix("START");
                    params.setEnter(false);
                    params.setSuccessVoicePrompt(context.getString(R.string.set_sucess_tip));

                    showMessage(context.getString(R.string.ext_scan_setbacklignt) +"：" +cb_backLight.isChecked(), MessageTag.DATA);
                    showMessage(context.getString(R.string.ext_scan_setvolumn) + "：" + et_setVolume.getText().toString(), MessageTag.DATA);
                    boolean result = scanBoxModule.setScanParams(params);
                    if (result) {
                        showMessage(context.getString(R.string.msg_ext_scan_set_params_success));
                    } else {
                        showMessage(context.getString(R.string.msg_ext_scan_set_params_success));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_start_scanner,functionid = 5)
    private void startScan(){
        showMessage(context.getString(R.string.start_external_sacan), MessageTag.TIP);
        StartScanExtParams params = new StartScanExtParams();
        params.setOnce(true);
        params.setInterval(1000);
        params.setTurnOffAmountDisplay(true);
//        params.setScanVoicePrompt(context.getString(R.string.set_sucess_tip));
        scanBoxModule.startScan("0.01", 60*1000, new ResultListener() {
            @Override
            public void onSuccess(String data) {
                showMessage(context.getString(R.string.external_scan_sucess) + data, MessageTag.DATA);
            }

            @Override
            public void onTimeOut() {
                showMessage(context.getString(R.string.external_scan_timeout), MessageTag.WARN);
            }

            @Override
            public void onError(int errorCode, String message) {
                showMessage(context.getString(R.string.external_scan_fail_rslt) + errorCode + ", " + message, MessageTag.ERROR);
            }
        }, params);
    }

    @MethodGridEntity(btnnameid = R.string.tv_stop_scanner, functionid = 6)
    private void stopScan(){
        showMessage(context.getString(R.string.stop_external_scan), MessageTag.TIP);
        boolean result = scanBoxModule.stopScan();
        if (result) {
            showMessage(context.getString(R.string.stop_external_scan_success), MessageTag.DATA);
        } else {
            showMessage(context.getString(R.string.stop_external_scan_fail) ,MessageTag.DATA);
        }
    }
}
