package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.content.Intent;
import android.newland.os.NlBuild;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.RadioGroup;

import com.newland.sdk.module.scanner.DefaultScannerLayout;
import com.newland.sdk.module.scanner.ScannerExtParams;
import com.newland.sdk.module.scanner.ScannerListener;
import com.newland.sdk.module.scanner.ScannerModule;
import com.newland.sdk.module.scanner.ScannerType;
import com.newland.sdk.module.scanner.StartStopCapability;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;
import com.newland.sdkdemo.view.ScanViewActivity;
import com.newland.sdkdemo.view.YUVDecodeActivity;

/**
 * Author by bxy, Date on 2019/5/11 0011.
 */
public class ScannerFragment extends BaseFragment {
    public static ScannerModule scanner = null;
    private static final int INDEX_STARTSCAN = 1;
    private static final int INDEX_STOPSCAN = 2;

    private static Handler scanEventHandler;

    public ScannerFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_scanner_f);
    }

    @Override
    public void initData() {
        scanner = moduleManage.getScannerModule();
        scanEventHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case AppConfig.ScanResult.SCAN_FINISH: {
                        showMessage(context.getString(R.string.msg_sanner_stop) + "\r\n", MessageTag.NORMAL);
                        break;

                    }
                    case AppConfig.ScanResult.SCAN_RESPONSE: {
                        Bundle bundle = msg.getData();
                        String[] barcodes = bundle.getStringArray("barcodes");
                        showMessage(context.getString(R.string.msg_scan_result)+ barcodes[0] + "\r\n", MessageTag.NORMAL);
                        break;
                    }
                    case AppConfig.ScanResult.SCAN_ERROR: {
                        Bundle bundle = msg.getData();
                        int errorCode = bundle.getInt("errorCode");
                        String errorMess = bundle.getString("errormessage");
                        showMessage(context.getString(R.string.msg_scanner_error) + errorCode + context.getString(R.string.msg_error_info) + errorMess+ "\r\n", MessageTag.NORMAL);
                        break;
                    }
                    case AppConfig.ScanResult.SCAN_TIMEOUT: {
                        Bundle bundle = msg.getData();
                        showMessage(context.getString(R.string.msg_scan_timeout)+ "\r\n", MessageTag.NORMAL);
                        break;
                    }
                    case AppConfig.ScanResult.SCAN_CANCEL: {
                        Bundle bundle = msg.getData();
                        showMessage(context.getString(R.string.msg_scan_cancel)+ "\r\n", MessageTag.NORMAL);
                        break;
                    }
                    default:
                        break;
                }
            }

        };
    }

    @Override
    public Object getModule() {
        return ScannerFragment.this;
    }

    @Override
    public int getSpanCount() {
        return 2;
    }

    @MethodGridEntity(btnnameid = R.string.msg_start_scanner,functionid = INDEX_STARTSCAN)
    private void startScan(){
        DialogUtils.createCustomDialog(context, context.getString(R.string.msg_scanner_choice), null, R.layout.dialog_scan, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try{
                    if(id == -1 || NlBuild.VERSION.MODEL == null){//cancel
                        return;
                    }
                    RadioGroup group = (RadioGroup) dialogView.findViewById(R.id.radioGroup_scan);
                    int chectedID = group.getCheckedRadioButtonId();
                    ScannerExtParams scannerExtParams = new ScannerExtParams();
                    DefaultScannerLayout defaultScannerLayout = new DefaultScannerLayout();
                    defaultScannerLayout.setEnableSound(true);
                    scannerExtParams.setOnce(true);
                    scannerExtParams.setDefaultScannerLayout(defaultScannerLayout);

                    RadioGroup type = dialogView.findViewById(R.id.radioGroup_scantype);
                    int typeID = type.getCheckedRadioButtonId();
                    if (typeID == R.id.radio_normal) {
                        scannerExtParams.setStartStopCapability(StartStopCapability.ENABLE_NORMAL);
                    } else if (typeID == R.id.radio_neverse) {
                        scannerExtParams.setStartStopCapability(StartStopCapability.ENABLE_REVERSE);
                    } else if (typeID == R.id.radio_normal_neverse) {
                        scannerExtParams.setStartStopCapability(StartStopCapability.ENABLE_NORMAL_REVERSE);
                    }

                    ScannerType scannerType = ScannerType.FRONT;
                    if (chectedID == R.id.radio_hardware) {
                        showMessage(context.getString(R.string.msg_choice_font_scanner_mode) + "\r\n", MessageTag.TIP);
                        scannerType = ScannerType.FRONT;

                        //Using the activity in the demo, you can modify it according to your needs.
//                        Intent intent = new Intent(context, ScanViewActivity.class);
//                        intent.putExtra("scanType", 0x01);
//                        context.startActivity(intent);
//                        return;


                    } else if(chectedID == R.id.radio_sofeware){
                        showMessage(context.getString(R.string.msg_choice_back_scanner_mode) + "\r\n", MessageTag.TIP);
                        scannerType = ScannerType.BACK;

                         //Using the activity in the demo, you can modify it according to your needs.
//                        Intent intent = new Intent(context, ScanViewActivity.class);
//                        intent.putExtra("scanType", 0x00);
//                        context.startActivity(intent);
//                        return;

                    }else {
                        showMessage(context.getString(R.string.msg_choice_back_scanner_mode) + "\r\n", MessageTag.TIP);
                        boolean isSupport =  scanner.isSupScanCode(ScannerType.CUSTOMER_DISPLAY);
                        if(!isSupport){
                            showMessage("Not support Customer Display Scanner",MessageTag.ERROR);
                            return;
                        }
                        scannerType = ScannerType.CUSTOMER_DISPLAY;

                        //Using the activity in the demo, you can modify it according to your needs.
//                        Intent intent = new Intent(context, ScanViewActivity.class);
//                        intent.putExtra("scanType", 0x00);
//                        context.startActivity(intent);

                    }

                    scanner.startScan(context, scannerType, null, 30, new ScannerListener() {
                        @Override
                        public void onTimeout() {
                            showMessage("------onTimeout------");
                        }

                        @Override
                        public void onResponse(String[] scanResults) {
                            showMessage("------onResponse------"+scanResults[0]);
                        }

                        @Override
                        public void onFinish() {
                            showMessage("------onFinish------");

                        }

                        @Override
                        public void onError(int errorCode, String message) {
                            showMessage("------onError-----message:"+message);

                        }

                        @Override
                        public void onCancel() {
                            showMessage("------onCancel------");

                        }
                    },scannerExtParams);
                }catch (Exception e){
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_error_info),MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_stop_scanner,functionid = INDEX_STOPSCAN)
    private void stopScan(){
        try {
            showMessage(context.getString(R.string.msg_stop_scan_begin) + "\r\n", MessageTag.NORMAL);
            if(NlBuild.VERSION.MODEL == null){
                showMessage(context.getString(R.string.msg_stop_scan_error)+NlBuild.VERSION.MODEL, MessageTag.ERROR);
                return;
            }
            scanner.stopScan();
            showMessage(context.getString(R.string.msg_stop_scan_success) + "\r\n", MessageTag.NORMAL);
        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_stop_scan_error) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_yuv_decode,functionid = 3)
    private void yuvDecode(){
        try {
            int version = Build.VERSION.SDK_INT;
            if(version>=24){//A7 supports decoding
                Intent intent = new Intent(context, YUVDecodeActivity.class);
                //intent.putExtra("scanType",0x01);//front:0x01; back:0x00;
                context.startActivity(intent);
            }else {
                showMessage("unsupport decode operation",MessageTag.ERROR);
            }

        }catch (Exception e){
            e.printStackTrace();
            showMessage("Exception:"+e,MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnname = "isSupScanCode",functionid = 4)
    private void isSupScanCode(){
        try {
            boolean isSupport = false;
            String[] items = new String[]{"Front","Back","CUSTOMER_DISPLAY"};
            DialogUtils.createSingleChoiceDialog(context, "isSupScanCode", items, new DialogUtils.SingleChoiceDialogCallback() {
                @Override
                public void onResult(int id) {
                    if(id<0){
                        return;
                    }
                    switch (id){
                        case 0:
                           showMessage("is support front scanner:"+scanner.isSupScanCode(ScannerType.FRONT));

                            break;
                        case 1:
                           showMessage("is support Back scanner:"+scanner.isSupScanCode(ScannerType.BACK));

                            break;
                        case 2:
                            showMessage("is support  Customer Display Screen scanner:"+scanner.isSupScanCode(ScannerType.CUSTOMER_DISPLAY));
                            break;
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            showMessage("isSupScanCode exception:"+e);
        }
    }

    public static Handler getScanEventHandler() {
        return scanEventHandler;
    }

    public static void setScanEventHandler(Handler scanEventHandler) {
        ScannerFragment.scanEventHandler = scanEventHandler;
    }
}
