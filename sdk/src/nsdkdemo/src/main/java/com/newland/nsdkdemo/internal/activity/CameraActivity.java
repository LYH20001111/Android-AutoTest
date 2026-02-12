package com.newland.nsdkdemo.internal.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.internal.NSDKModuleManager;
import com.newland.nsdk.core.api.internal.barcodedecoder.DecodingCallback;
import com.newland.nsdk.core.api.internal.barcodescanner.BarcodeScanner;
import com.newland.nsdk.core.api.internal.barcodescanner.ScanParameters;
import com.newland.nsdk.core.api.internal.barcodescanner.ScanSettings;
import com.newland.nsdk.core.api.internal.barcodescanner.ScannerType;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.MessageEvent;

public class CameraActivity extends AppCompatActivity {
    private static final int NORMAL = 0;
    private static final int TIP = 1;
    private static final int ERROR = 2;
    private TextView tvCamera;
    private SurfaceView surfaceView;
    private Button btnStart, btnStop;
    private ImageView ivLight;
    private LinearLayout llContinuousButtons;
    private NSDKModuleManager nsdkModuleManager;
    private BarcodeScanner barcodeScanner;
    private boolean isContinuous = false;
    private boolean isStart = false;
    private int focusMode = 0;
    private int cameraId = 0;
    private int timeout = 15000;
    private String newMessage = "";
    private boolean isLightOn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        isContinuous = getIntent().getBooleanExtra("isContinuous", false);
        focusMode = getIntent().getIntExtra("focusMode", 0);
        cameraId = getIntent().getIntExtra("cameraId", 0);
        timeout = getIntent().getIntExtra("timeout", 15000);
        nsdkModuleManager = NSDKModuleManagerImpl.getInstance();
        barcodeScanner = (BarcodeScanner) nsdkModuleManager.getModule(ModuleType.BARCODE_SCANNER);
        initViews();
    }

    private void initViews() {
        llContinuousButtons = findViewById(R.id.linear_continuous_button);
        tvCamera = findViewById(R.id.tv_camera);
        tvCamera.setMovementMethod(ScrollingMovementMethod.getInstance());
        btnStart = findViewById(R.id.btn_startScan);
        btnStop = findViewById(R.id.btn_stopScan);
        surfaceView = findViewById(R.id.surfaceView);
        initScanParameters();
        btnStart.setOnClickListener(l-> {
            startScan();
        });
        btnStop.setOnClickListener(l-> {
            stopScan();
        });
        ivLight = findViewById(R.id.iv_light);
        ivLight.setOnClickListener(l-> {
            operateLight();
        });
        if (isContinuous) {
            tvCamera.setVisibility(View.VISIBLE);
            llContinuousButtons.setVisibility(View.VISIBLE);
        } else {
            tvCamera.setVisibility(View.GONE);
            llContinuousButtons.setVisibility(View.GONE);
            startScan();
        }
    }

    private void initScanParameters() {
        try {
            ScanParameters scanParameters = new ScanParameters();
            scanParameters.setSurfaceView(surfaceView);
            scanParameters.setFocusMode(focusMode);
            scanParameters.setSoundSwitcher(true);
            scanParameters.setTimeout(timeout);
            scanParameters.setScannerType(getScannerType(cameraId));
            ScanSettings scanSettings = new ScanSettings();
            scanSettings.setUPCEANSwitch(true);
            barcodeScanner.setDecodingCallback(callback);
            barcodeScanner.initScan(scanParameters);
            barcodeScanner.set(scanSettings);
        } catch (NSDKException e) {
            showErrorMessage(e.getMessage());
        }
    }

    private void startScan() {
        try {
            barcodeScanner.startScan();
            if (cameraId == 1 && isContinuous) {
                ivLight.setVisibility(View.VISIBLE);
            }
            isStart = true;
        } catch (NSDKException e) {
            showErrorMessage(e.getMessage());
        }
    }

    private void operateLight() {
        try {
            if (isLightOn) {
                isLightOn = false;
                ivLight.setImageResource(R.drawable.light_off);
                barcodeScanner.closeLight();
            } else {
                isLightOn = true;
                ivLight.setImageResource(R.drawable.light_on);
                barcodeScanner.openLight();
            }
        } catch (NSDKException e) {
            showErrorMessage(e.getMessage());
        }
    }

    private void stopScan() {
        try {
            barcodeScanner.stopScan();
        } catch (NSDKException e) {
            showErrorMessage(e.getMessage());
        }
    }


    private DecodingCallback callback = new DecodingCallback() {
        @Override
        public void onDecodingCallback(int eventCode, String result) {
            if (isContinuous) {
                if (eventCode == ErrorCode.OK) {
                    showMessage("Result: " + result, NORMAL);
                } else {
                    showErrorMessage(nsdkModuleManager.getErrMsg(eventCode));
                }
                stopScan();
            } else {
                Intent responseIntent = new Intent();
                responseIntent.putExtra(AppConfig.SharedPreferenceConfig.SINGLE_SCANNING_RESULT, result);
                setResult(Activity.RESULT_OK, responseIntent);
                CameraActivity.this.finish();
            }

        }
    };

    private void showMessage(String message, int messageType) {
        switch (messageType) {
            case NORMAL:
                String message1 = "<font color = 'black'>" + message + "</font>";
                break;
            case TIP:
                message1 = "<font color = 'green'>" + message + "</font>";
                break;
            case ERROR:
                message1 = "<font color = 'red'>" + message + "</font>";
                break;
        }
        newMessage = message + "<br/>" + newMessage;
        tvCamera.setText(Html.fromHtml(newMessage, null, null));
    }

    private void showErrorMessage(String message) {
        showMessage(message, ERROR);
    }

    private ScannerType getScannerType(int cameraId) {
        switch (cameraId) {
            case 1:
                return ScannerType.BACK_CAMERA;
            case 2:
                return ScannerType.PAYMENT_CAMERA;
            case 3:
                return ScannerType.HARDWARE_SCANNER;
            default:
                return ScannerType.FRONT_CAMERA;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (barcodeScanner != null && isStart) {
                barcodeScanner.releaseScan();
                barcodeScanner = null;
                isStart = false;
            }
        } catch (NSDKException e) {
            showErrorMessage(e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (barcodeScanner == null) {
            initScanParameters();
        }
    }
}