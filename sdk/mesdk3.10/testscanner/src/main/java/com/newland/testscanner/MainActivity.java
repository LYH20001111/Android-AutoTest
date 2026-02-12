package com.newland.testscanner;

import android.app.Activity;
import android.content.Intent;
import android.newland.os.NlBuild;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.newland.sdk.me.module.scanner.MEScanner;
import com.newland.sdk.module.scanner.LightOperType;
import com.newland.sdk.module.scanner.ScanLightType;
import com.newland.sdk.module.scanner.ScannerType;
import com.newland.testscanner.util.AppConfig;
import com.newland.testscanner.util.DialogUtils;

/**
 * @author youjf
 * @description
 * @date 2019/8/1
 * @since 3.10.01
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private static Handler scanEventHandler;
    private TextView tvOperationMessage;
    private String newMessage = "", message = "";
    public static MEScanner meScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        meScanner = new MEScanner(null,getApplicationContext());

        tvOperationMessage = findViewById(R.id.id_info);
        findViewById(R.id.id_startscan).setOnClickListener(this);
        findViewById(R.id.id_issupport_scanner).setOnClickListener(this);
        findViewById(R.id.id_clear).setOnClickListener(this);
        findViewById(R.id.id_decode).setOnClickListener(this);
        findViewById(R.id.id_openlight).setOnClickListener(this);
        findViewById(R.id.id_closelight).setOnClickListener(this);
        if(NlBuild.VERSION.MODEL.equals("N700")){
            findViewById(R.id.id_openlight).setVisibility(View.VISIBLE);
            findViewById(R.id.id_closelight).setVisibility(View.VISIBLE);
        }
        scanEventHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case AppConfig.ScanResult.SCAN_FINISH: {
                        showMessage(getString(R.string.msg_sanner_stop) + "\r\n", MessageTag.NORMAL);
                        break;

                    }
                    case AppConfig.ScanResult.SCAN_RESPONSE: {
                        Bundle bundle = msg.getData();
                        String[] barcodes = bundle.getStringArray("barcodes");
                        showMessage(getString(R.string.msg_scan_result) + barcodes[0] + "\r\n", MessageTag.NORMAL);
                        break;
                    }
                    case AppConfig.ScanResult.SCAN_ERROR: {
                        Bundle bundle = msg.getData();
                        int errorCode = bundle.getInt("errorCode");
                        String errorMess = bundle.getString("errormessage");
                        showMessage(getString(R.string.msg_scanner_error) + errorCode + getString(R.string.msg_error_info) + errorMess + "\r\n", MessageTag.NORMAL);
                        break;
                    }
                    case AppConfig.ScanResult.SCAN_TIMEOUT: {
                        Bundle bundle = msg.getData();
                        showMessage(getString(R.string.msg_scan_timeout) + "\r\n", MessageTag.NORMAL);
                        break;
                    }
                    case AppConfig.ScanResult.SCAN_CANCEL: {
                        Bundle bundle = msg.getData();
                        showMessage(getString(R.string.msg_scan_cancel) + "\r\n", MessageTag.NORMAL);
                        break;
                    }
                    default:
                        break;
                }
            }

        };
    }

    public void showMessage(final String mess, final int messageType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (messageType) {
                    case MessageTag.NORMAL:
                        message = "<font color='black'>" + mess + "</font>";
                        break;
                    case MessageTag.ERROR:
                        message = "<font color='red'>" + mess + "</font>";
                        break;
                    case MessageTag.TIP:
                        message = "<font color='green'>" + mess + "</font>";
                        break;
                    case MessageTag.DATA:
                        message = "<font color='blue'>" + mess + "</font>";
                        break;
                    case MessageTag.WARN:
                        message = "<u><font color='red'>" + mess + "</font></u>";
                        break;
                    default:
                        break;
                }
                newMessage = message + "<br>" + newMessage;
                tvOperationMessage.setText(Html.fromHtml(newMessage, null, null));
            }
        });
    }

    public static Handler getScanEventHandler() {
        return scanEventHandler;
    }

    public static MEScanner getMeScanner() {
        return meScanner;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_startscan:
                DialogUtils.createCustomDialog(this, getString(R.string.msg_scanner_choice), null, R.layout.dialog_scan, new DialogUtils.CustomDialogCallback() {
                    @Override
                    public void onResult(int id, View dialogView) {
                        try{
                            RadioGroup group = (RadioGroup) dialogView.findViewById(R.id.radioGroup_scan);
                            int chectedID = group.getCheckedRadioButtonId();
                            if (chectedID == R.id.radio_hardware) {
                                showMessage(getString(R.string.msg_choice_font_scanner_mode) + "\r\n", MessageTag.TIP);
                                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                                intent.putExtra("scanType", 0x01);
                                startActivity(intent);
                            } else {
                                showMessage(getString(R.string.msg_choice_back_scanner_mode) + "\r\n", MessageTag.TIP);
                                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                                intent.putExtra("scanType", 0x00);
                                startActivity(intent);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            showMessage(getString(R.string.msg_error_info),MessageTag.ERROR);
                        }
                    }
                });
            break;
            case R.id.id_issupport_scanner:
                DialogUtils.createCustomDialog(this, getString(R.string.msg_scanner_choice), null, R.layout.dialog_scan, new DialogUtils.CustomDialogCallback() {
                    @Override
                    public void onResult(int id, View dialogView) {
                        try{
                            RadioGroup group = (RadioGroup) dialogView.findViewById(R.id.radioGroup_scan);
                            int chectedID = group.getCheckedRadioButtonId();
                            if (chectedID == R.id.radio_hardware) {
                                boolean isSupFront = meScanner.isSupScanCode(ScannerType.FRONT);
                                showMessage(getString(R.string.is_sup_front)+isSupFront,MessageTag.DATA);
                            } else {
                                boolean isSupBack = meScanner.isSupScanCode(ScannerType.BACK);
                                showMessage(getString(R.string.is_sup_back)+isSupBack,MessageTag.DATA);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            showMessage(getString(R.string.msg_error_info),MessageTag.ERROR);
                        }
                    }
                });
                break;
            case R.id.id_decode:
                try {
                    int version = Build.VERSION.SDK_INT;
                    if(version>=24){//A7以上支持解码
                        Intent intent = new Intent(MainActivity.this, YUVDecodeActivity.class);
                        MainActivity.this.startActivity(intent);
                    }else {
                        showMessage("unsupport decode operation",MessageTag.ERROR);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    showMessage("Exception:"+e,MessageTag.ERROR);
                }
                break;
            case R.id.id_openlight:
                String[] items = new String[]{"LED LIGHT","RED LIGHT"};
                DialogUtils.createSingleChoiceDialog(this,"select light type",items,new DialogUtils.SingleChoiceDialogCallback(){
                    @Override
                    public void onResult(int id) {
                        try {
                            MEScanner MEScanner = new MEScanner(null,MainActivity.this);
                            if(id==0){
                                boolean openRslt = MEScanner.operateLight(ScanLightType.LED_LIGHT,LightOperType.OPEN);
                                showMessage("result:"+openRslt,MessageTag.DATA);
                            }else {
                                boolean openRslt = MEScanner.operateLight(ScanLightType.RED_LIGHT,LightOperType.OPEN);
                                showMessage("result:"+openRslt,MessageTag.DATA);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            showMessage("Exception:"+e,MessageTag.ERROR);
                        }
                    }
                });
                break;
            case R.id.id_closelight:
                String[] selItems = new String[]{"LED LIGHT","RED LIGHT"};
                DialogUtils.createSingleChoiceDialog(this,"select light type",selItems,new DialogUtils.SingleChoiceDialogCallback(){
                    @Override
                    public void onResult(int id) {
                        try {
                            MEScanner MEScanner = new MEScanner(null,MainActivity.this);
                            if(id==0){
                                boolean openRslt = MEScanner.operateLight(ScanLightType.LED_LIGHT,LightOperType.CLOSE);
                                showMessage("result:"+openRslt,MessageTag.DATA);
                            }else {
                                boolean openRslt = MEScanner.operateLight(ScanLightType.RED_LIGHT,LightOperType.CLOSE);
                                showMessage("result:"+openRslt,MessageTag.DATA);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            showMessage("Exception:"+e,MessageTag.ERROR);
                        }
                    }
                });
                break;
            case R.id.id_clear:
                newMessage = "";
                tvOperationMessage.setText("");
                break;
        }
    }

    class MessageTag {
        /**
         * 正常消息<tt>tag</tt>
         */
        public static final int NORMAL = 0;
        /**
         * 错误消息<tt>tag</tt>
         */
        public static final int ERROR = 1;
        /**
         * 提示消息<tt>tag</tt>
         */
        public static final int TIP = 2;
        /**
         * 数据<tt>tag</tt>
         */
        public static final int DATA = 3;
        /**
         * 警告<tt>tag</tt>
         */
        public static final int WARN = 4;
    }
}
