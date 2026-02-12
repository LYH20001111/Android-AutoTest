package com.newland.sdk.scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.newland.SettingsManager;
import android.newland.content.NlContext;
import android.newland.os.NlBuild;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newland.buildtask.R;
import com.newland.sdk.module.buzzer.BuzzerModule;
import com.newland.sdk.module.buzzer.MeBuzzer;
import com.newland.sdk.module.scanner.DefaultScannerViewParams;
import com.newland.sdk.module.scanner.LightOperType;
import com.newland.sdk.module.scanner.ScanLightType;
import com.newland.sdk.module.scanner.ScannerExtParams;
import com.newland.sdk.module.scanner.ScannerListener;
import com.newland.sdk.module.scanner.ScannerModule;
import com.newland.sdk.module.scanner.ScannerType;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class ScanViewActivity extends Activity {

    private SurfaceView surfaceView;
    private Context context;
    private ScannerType scanType;
    private static DeviceLogger logger = DeviceLoggerFactory.getLogger(ScanViewActivity.class);
    private ImageView scanIV;
    private RelativeLayout frontLL;
    private LinearLayout switch_fr;
    private LinearLayout switch_bc;
    private boolean isFinish = false;
    private boolean isSDKTimerTimeout = false;
    private AnimationDrawable scanAnim;
    private FrameLayout backFL;
    private static final int Code_PERMISSION = 100;
    private TextView picTv, posTv;
    private SettingsManager settingManager;
    BuzzerModule buzzerModule = new MeBuzzer();
    private ScannerModule scannerModule;
    private boolean isSwitch = false;

    // 全局超时计时器
    private Timer timer;
    private int countdown = 0;
    private boolean isBackPress = false;

    private ScanViewBack scanViewBack;
    private LinearLayout lyHardScan;

    @SuppressLint({"WrongAppConfigant", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = this;
        View view = View.inflate(this, R.layout.activity_scanner_view, null);
        setContentView(view);

        init();
        try {
            settingManager = (SettingsManager) getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
            settingManager.setAppSwitchKeyEnabled(false);
            settingManager.setHomeKeyEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, Code_PERMISSION);
            } else {
                startScan();
            }
        } else {
            startScan();
        }
    }

    private void init() {
        scannerModule = DefaultScannerViewParams.getScannerModulel();
        scanType = DefaultScannerViewParams.getScannerType();
        logger.debug("------scanType:" + scanType);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        frontLL = (RelativeLayout) findViewById(R.id.ll_front);
        switch_fr = (LinearLayout) findViewById(R.id.ll_switch_front);
        switch_bc = (LinearLayout) findViewById(R.id.ll_switch_back);
        backFL = (FrameLayout) findViewById(R.id.fl_back);
        scanIV = (ImageView) findViewById(R.id.iv_scan);

        picTv = (TextView) findViewById(R.id.text_pic);
        posTv = (TextView) findViewById(R.id.text_pos);
        lyHardScan = (LinearLayout) findViewById(R.id.ly_back_hard_scan);
        scanViewBack = (ScanViewBack)findViewById(R.id.scanviewback);
        //默认使用900的扫码前置预览界面
        if (NlBuild.VERSION.MODEL.equals("CPOS X5") || Build.MODEL.equals("STAR A-6300")) {
            scanIV.setImageResource(R.drawable.scan_x5_list);
            picTv.setGravity(Gravity.LEFT);
            posTv.setGravity(Gravity.LEFT);
            picTv.setPadding(200, 0, 0, 0);
            posTv.setPadding(200, 0, 0, 0);
        } else if (NlBuild.VERSION.MODEL.equals("CPOS X3")) {
            scanIV.setImageResource(R.drawable.scan_x3_list);
            picTv.setGravity(Gravity.LEFT);
            posTv.setGravity(Gravity.LEFT);
            picTv.setPadding(200, 0, 0, 0);
            posTv.setPadding(200, 0, 0, 0);
        } else if (NlBuild.VERSION.MODEL.startsWith("N910")) {
            scanIV.setImageResource(R.drawable.scan_910_list);
        } else if (NlBuild.VERSION.MODEL.equals("N550")) {
            picTv.setTextSize(25);
            posTv.setTextSize(25);
            scanIV.setImageResource(R.drawable.scan_550_list);
        } else if (NlBuild.VERSION.MODEL.equals("N850")) {
            scanIV.setImageResource(R.drawable.scan_850_list);
        } else if (NlBuild.VERSION.MODEL.equals("N700")) {
            scanIV.setImageResource(R.drawable.scan_700_list);
        } else if (NlBuild.VERSION.MODEL.equals("FPOS F10")) {
            scanIV.setImageResource(R.drawable.scan_f10_list);
        } else if (NlBuild.VERSION.MODEL.equals("FPOS F7")) {
            scanIV.setImageResource(R.drawable.scan_f7_list);
        }else if (NlBuild.VERSION.MODEL.equals("N900")) {
            scanIV.setImageResource(R.drawable.scan_900_list);
        }else if (NlBuild.VERSION.MODEL.equals("U2000")) {
            frontLL.setBackgroundColor(Color.WHITE);
            scanIV.setImageResource(R.drawable.scan_u2000_list);
            scanIV.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }  else {
            frontLL.setBackgroundColor(Color.WHITE);
            scanIV.setPadding(100, 0, 100, 0);
            scanIV.setScaleType(ImageView.ScaleType.FIT_CENTER);
            scanIV.setImageResource(R.drawable.scan_default_list);
        }

        if((NlBuild.VERSION.MODEL.equals("N750") || NlBuild.VERSION.MODEL.equals("N750P") || NlBuild.VERSION.MODEL.equals("N950S-C") || NlBuild.VERSION.MODEL.equals("N950S")) &&  Camera.getNumberOfCameras() == 1){
            findViewById(R.id.llc_switch_back).setVisibility(View.INVISIBLE);
        }

        if(scanType == ScannerType.BACK && NlBuild.VERSION.MODEL.equals("P300") || (NlBuild.VERSION.MODEL.equals("N950S") && Camera.getNumberOfCameras() == 1)){
            findViewById(R.id.llc_switch_front).setVisibility(View.INVISIBLE);
        }

        switch_fr.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.debug("---------------切换前置---------");
                switch_bc.setEnabled(true);
                switch_fr.setEnabled(false);
                if (Build.MODEL.equals("N700")) {
                    scannerModule.operateLight(ScanLightType.LED_LIGHT, LightOperType.CLOSE);
                    scannerModule.operateLight(ScanLightType.RED_LIGHT, LightOperType.CLOSE);
                }
                isSwitch = true;
                scannerModule.stopScan();

                isFinish = true;
                scanType = ScannerType.FRONT;
                startScan();

            }
        });

        switch_bc.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                logger.debug("---------------切换后置---------");
                switch_bc.setEnabled(false);
                switch_fr.setEnabled(true);
                if (Build.MODEL.equals("N700")) {
                    scannerModule.operateLight(ScanLightType.LED_LIGHT, LightOperType.CLOSE);
                    scannerModule.operateLight(ScanLightType.RED_LIGHT, LightOperType.CLOSE);
                }
                isSwitch = true;
                scannerModule.stopScan();
                isFinish = true;
                scanType = ScannerType.BACK;
                startScan();
            }
        });

//		if (Build.VERSION.SDK_INT>22){
//			if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
//				//先判断有没有权限 ，没有就在这里进行权限的申请
//				ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},Code_PERMISSION);
//			}else {
//				startScan();
//			}
//		} else {
//			startScan();
//		}

        startSDKTimer();


    }


    private void startScan() {
        isFinish = false;
        if (scanType == ScannerType.BACK) {//后置的
            surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
            surfaceView.setVisibility(View.VISIBLE);
            frontLL.setVisibility(View.GONE);
            backFL.setVisibility(View.VISIBLE);
            boolean resutl = scannerModule.isSupScanCode(ScannerType.FRONT);
            switch_fr.setVisibility(View.GONE);
            if (resutl) {
                switch_fr.setVisibility(View.VISIBLE);
            }

            String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;
            if (Build.MODEL.equals("N950") && config != null && NlBuild.VERSION.NL_HARDWARE_CONFIG.length() >= 10 && config.substring(8, 10).equals("15")) {
                lyHardScan.setVisibility(View.VISIBLE);
                scanViewBack.setVisibility(View.GONE);
            }
        } else if (scanType == ScannerType.FRONT) {
            surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
            surfaceView.setVisibility(View.GONE);
            surfaceView = null;
            backFL.setVisibility(View.GONE);
            frontLL.setVisibility(View.VISIBLE);
            scanAnim = (AnimationDrawable) scanIV.getDrawable();
            if (scanAnim != null && !scanAnim.isRunning()) {
                scanAnim.start();
            }
            boolean resutl = scannerModule.isSupScanCode(ScannerType.BACK);
            switch_bc.setVisibility(View.GONE);
            if (resutl) {
                switch_bc.setVisibility(View.VISIBLE);
            }
            if (Build.MODEL.equals("N700")) {
                scannerModule.operateLight(ScanLightType.LED_LIGHT, LightOperType.OPEN);
                scannerModule.operateLight(ScanLightType.RED_LIGHT, LightOperType.OPEN);
            }
        } else if (scanType == ScannerType.CUSTOMER_DISPLAY) {//副屏的
            surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
            surfaceView.setVisibility(View.VISIBLE);
            frontLL.setVisibility(View.GONE);
            backFL.setVisibility(View.VISIBLE);
            boolean resutl = scannerModule.isSupScanCode(ScannerType.FRONT);
            switch_fr.setVisibility(View.GONE);
            if (resutl) {
                switch_fr.setVisibility(View.VISIBLE);
            }

        } else {
            if (DefaultScannerViewParams.getScannerListener() != null) {
                DefaultScannerViewParams.getScannerListener().onError(ErrorCode.SCANNER_UNSUPPORT, "");
            }
            finish();
        }
        try {

            ScannerExtParams scannerExtParams = new ScannerExtParams();

            scannerExtParams.setOnce(DefaultScannerViewParams.getScannerExtParams().isOnce());
            scannerExtParams.setStartStopCapability(DefaultScannerViewParams.getScannerExtParams().getStartStopCapability());
            scannerModule.startScan(ScanViewActivity.this, scanType, surfaceView, DefaultScannerViewParams.getTimeOut(), new ScannerListener() {

                @Override
                public void onTimeout() {
                    isFinish = true;
                    logger.debug("[onTimeout]" + DefaultScannerViewParams.getScannerListener());
                    if (DefaultScannerViewParams.getScannerListener() != null) {
                        DefaultScannerViewParams.getScannerListener().onTimeout();
                    }
                    finish();
                }

                @Override
                public void onResponse(String[] barcodes) {
                    logger.debug("[onResponse] code:" + barcodes[0]);
                    boolean isEnableSound = DefaultScannerViewParams.isEnableSound();
                    logger.debug("[onResponse] isEnableSound:" + isEnableSound + "; " + DefaultScannerViewParams.getScannerListener());
                    if (isEnableSound) {
                        buzzerModule.play(1, 5, 10);
                    }
                    if (DefaultScannerViewParams.getScannerListener() != null) {
                        DefaultScannerViewParams.getScannerListener().onResponse(barcodes);
                    }

                }

                @Override
                public void onFinish() {
                    logger.debug("[onFinish] " + isFinish + ";" + DefaultScannerViewParams.getScannerListener());
                    isFinish = true;
                    finish();
                    if (DefaultScannerViewParams.getScannerListener() != null) {
                        DefaultScannerViewParams.getScannerListener().onFinish();
                    }
                }

                @Override
                public void onError(int errCoce, String errMsg) {
                    logger.debug("[onError] errorCode:" + errCoce + ";message:" + errMsg + ";" + DefaultScannerViewParams.getScannerListener());
                    isFinish = true;
                    if (DefaultScannerViewParams.getScannerListener() != null) {
                        DefaultScannerViewParams.getScannerListener().onError(ErrorCode.SCANNER_INIT_FAILED, "" + errMsg);
                    }
                    finish();
                }

                @Override
                public void onCancel() {
                    isFinish = true;
                    logger.debug("[onCancel] " + isSwitch + "," + isSDKTimerTimeout + "," + isFinish + "," + isBackPress + ";" + DefaultScannerViewParams.getScannerListener());
                    if (isSwitch){
                        logger.debug("[onCancel] switch camera.");
                        isFinish = false;
                        isSwitch = false;
                    } else {
                        if (isBackPress || (!isSwitch && !isSDKTimerTimeout && !isFinish)) {
                            if (DefaultScannerViewParams.getScannerListener() != null) {
                                DefaultScannerViewParams.getScannerListener().onCancel();
                            }
                        } else if (isSDKTimerTimeout && isFinish) {
                            if (DefaultScannerViewParams.getScannerListener() != null) {
                                DefaultScannerViewParams.getScannerListener().onTimeout();
                            }
                        }
                        finish();
                    }



                }
            }, scannerExtParams);

        } catch (Exception e) {
            e.printStackTrace();
            isFinish = true;
            logger.debug("[Exception] " + e.getMessage() + ";Listener()" + DefaultScannerViewParams.getScannerListener());
            if (DefaultScannerViewParams.getScannerListener() != null) {
                DefaultScannerViewParams.getScannerListener().onError(ErrorCode.SCANNER_INIT_FAILED, "" + e);
            }
            finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        logger.debug("[onPause] isFinish：" + isFinish);
//		if(!isFinish&&!back){
//			isSwitch =false;
//			scannerModule.stopScan();
//		}
        if (scanAnim != null && scanAnim.isRunning()) {
            scanAnim.stop();
        }
        try {
            if (Build.MODEL.equals("N700")) {
                scannerModule.operateLight(ScanLightType.LED_LIGHT, LightOperType.CLOSE);
                scannerModule.operateLight(ScanLightType.RED_LIGHT, LightOperType.CLOSE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        isBackPress = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        logger.debug("[onKeyDown] keyCode:" + keyCode + ",event:" + event);//700S设备 左边是24 右边是25
//		if((keyCode==KeyEvent.KEYCODE_VOLUME_UP&& event.getRepeatCount() == 0)){
//			logger.debug("发起700扫码");
//			startScan();
//		}else if((keyCode==KeyEvent.KEYCODE_VOLUME_DOWN&& event.getRepeatCount() == 0)){
//			logger.debug("发起700扫码");
//			startScan();
//		}

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            logger.debug("回退键");
            isFinish = false;
            isBackPress = true;
            finish();
        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onStop() {
        super.onStop();
        logger.debug("[onStop] isFinish:" + isFinish);
        if (!isFinish) {
            isSwitch = false;
            scannerModule.stopScan();
        }
    }

    @Override
    protected void onDestroy() {
        logger.debug("[onDestroy]");
        try {
            settingManager.setAppSwitchKeyEnabled(true);
            settingManager.setHomeKeyEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }

        super.onDestroy();
//        if (!isFinish) {
//            logger.debug("---------------onDestroy cancel---------isFinish:" + isFinish);
//            isSwitch = false;
//            scannerModule.stopScan();
//        }
        stopSDKTimer();
        try {
            DefaultScannerViewParams.setScannerExtParams(null);
            DefaultScannerViewParams.setScannerListener(null);
            DefaultScannerViewParams.setScannerType(null);
            DefaultScannerViewParams.setScannerModulel(null);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error r) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            if (requestCode == Code_PERMISSION) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //权限被用户同意,做相应的事情
                    startScan();
                } else {
                    //权限被用户拒绝，做相应的事情
                    if (DefaultScannerViewParams.getScannerListener() != null) {
                        DefaultScannerViewParams.getScannerListener().onError(ErrorCode.SCANNER_UNSUPPORT, "PERMISSION GRANTED FAILED");
                    }
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void startSDKTimer() {
        logger.debug("[startSDKTimer]");
        timer = new Timer();
        countdown = DefaultScannerViewParams.getTimeOut() + 1;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                logger.debug("[startSDKTimer] countdown:" + countdown);
                logger.debug("[startSDKTimer] isFinish:" + isFinish + ",isBackPress:" + isBackPress + ",isTimeout:" + isSDKTimerTimeout);

                countdown--;
                if (countdown <= 0 && !isFinish && !isBackPress && !isSDKTimerTimeout) {
                    logger.info("[startSDKTimer] timeout.");
                    isSDKTimerTimeout = true;
                    isFinish = true;
                    if (timer != null) {
                        stopSDKTimer();
                        try {
                            scannerModule.stopScan();
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    private void stopSDKTimer() {
        logger.debug("[stopSDKTimer]");
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

}
