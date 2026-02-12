package com.newland.testscanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.newland.SettingsManager;
import android.newland.content.NlContext;
import android.newland.os.NlBuild;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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

import com.newland.sdk.me.module.scanner.MEScanner;
import com.newland.sdk.module.scanner.LightOperType;
import com.newland.sdk.module.scanner.ScanLightType;
import com.newland.sdk.module.scanner.ScannerExtParams;
import com.newland.sdk.module.scanner.ScannerListener;
import com.newland.sdk.module.scanner.ScannerType;
import com.newland.testscanner.util.AppConfig;
import com.newland.testscanner.util.SoundPoolImpl;


public class ScannerActivity extends Activity {
    private String TAG = "ScannerActivity";
    private SurfaceView surfaceView;
    private ScannerType scanType;
    private ImageView scanIV;
    private SoundPoolImpl spi;
    private RelativeLayout frontLL;
    private LinearLayout switch_fr;
    private LinearLayout switch_bc;
    private boolean isFinish = false;
    private AnimationDrawable scanAnim;
    private FrameLayout backFL;
    private static final int Code_PERMISSION=100;
    private TextView picTv,posTv;
    private MEScanner MEScanner = MainActivity.getMeScanner();
    private SettingsManager settingManager;

    @SuppressLint({"WrongAppConfigant", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = View.inflate(this, R.layout.sacn_view, null);
        setContentView(view);


        spi = SoundPoolImpl.getInstance();
        spi.initLoad(this);
        init();
        try {
            settingManager = (SettingsManager) getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
            settingManager.setAppSwitchKeyEnabled(false);
            settingManager.setHomeKeyEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        int type=getIntent().getIntExtra("scanType", 0x01);//Front default
        if(type == 0x01){
            scanType = ScannerType.FRONT;
        }else{
            scanType = ScannerType.BACK;
        }
        surfaceView=(SurfaceView) findViewById(R.id.surfaceView);
        frontLL=(RelativeLayout) findViewById(R.id.ll_front);
        switch_fr = (LinearLayout) findViewById(R.id.ll_switch_front);
        switch_bc=(LinearLayout)findViewById(R.id.ll_switch_back);
        backFL=(FrameLayout) findViewById(R.id.fl_back);
        scanIV=(ImageView) findViewById(R.id.iv_scan);

        picTv=(TextView) findViewById(R.id.text_pic);
        posTv=(TextView) findViewById(R.id.text_pos);

        //默认使用900的扫码前置预览界面
        if(NlBuild.VERSION.MODEL.equals("CPOS X5")|| android.os.Build.MODEL.equals("STAR A-6300")){
            scanIV.setImageResource(R.drawable.scan_x5_list);
            picTv.setGravity(Gravity.LEFT);
            posTv.setGravity(Gravity.LEFT);
            picTv.setPadding(200,0,0,0);
            posTv.setPadding(200,0,0,0);
        }else if(NlBuild.VERSION.MODEL.equals("CPOS X3")){
            scanIV.setImageResource(R.drawable.scan_x3_list);
            picTv.setGravity(Gravity.LEFT);
            posTv.setGravity(Gravity.LEFT);
            picTv.setPadding(200,0,0,0);
            posTv.setPadding(200,0,0,0);
        }else if(NlBuild.VERSION.MODEL.equals("N910")){
            scanIV.setImageResource(R.drawable.scan_910_list);
        }else if(NlBuild.VERSION.MODEL.equals("N550")){
            picTv.setTextSize(25);
            posTv.setTextSize(25);
            scanIV.setImageResource(R.drawable.scan_550_list);
        }else if(NlBuild.VERSION.MODEL.equals("N850")){
            scanIV.setImageResource(R.drawable.scan_850_list);
        }else if(NlBuild.VERSION.MODEL.equals("N700")){
            scanIV.setImageResource(R.drawable.scan_700_list);

        }

        switch_fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("---------------切换前置---------");
                switch_bc.setEnabled(true);
                switch_fr.setEnabled(false);
                if(Build.MODEL.equals("N700")){
                    MEScanner.operateLight(ScanLightType.LED_LIGHT,LightOperType.CLOSE);
                    MEScanner.operateLight(ScanLightType.RED_LIGHT,LightOperType.CLOSE);
                }
                MEScanner.stopScan();
                isFinish = true;
                scanType=ScannerType.FRONT;
                startScan(scanType);
            }
        });

        switch_bc.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG,"---------------切换后置---------");
                switch_bc.setEnabled(false);
                switch_fr.setEnabled(true);
                if(Build.MODEL.equals("N700")){
                    MEScanner.operateLight(ScanLightType.LED_LIGHT,LightOperType.CLOSE);
                    MEScanner.operateLight(ScanLightType.RED_LIGHT,LightOperType.CLOSE);
                }

                MEScanner.stopScan();
                isFinish = true;
                scanType=ScannerType.BACK;
                startScan(scanType);
            }
        });

        if (Build.VERSION.SDK_INT>22){
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},Code_PERMISSION);
            }else {
                startScan(scanType);
            }
        } else {
            startScan(scanType);
        }


    }

    private void startScan(ScannerType scanType){
        try{
            isFinish = false;
            if(scanType==ScannerType.BACK){//后置的
                surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
                frontLL.setVisibility(View.GONE);
                backFL.setVisibility(View.VISIBLE);
                boolean resutl = MEScanner.isSupScanCode(ScannerType.FRONT);
                switch_fr.setVisibility(View.GONE);
                if(resutl){
                    switch_fr.setVisibility(View.VISIBLE);
                }


            }else if(scanType==ScannerType.FRONT){
                surfaceView = null;
                backFL.setVisibility(View.GONE);
                frontLL.setVisibility(View.VISIBLE);
                scanAnim = (AnimationDrawable)scanIV.getDrawable();
                if (scanAnim != null && !scanAnim.isRunning()) {
                    scanAnim.start();
                }
                boolean resutl = MEScanner.isSupScanCode(ScannerType.BACK);
                switch_bc.setVisibility(View.GONE);
                if(resutl){
                    switch_bc.setVisibility(View.VISIBLE);
                }
                if(Build.MODEL.equals("N700")){
                    MEScanner.operateLight(ScanLightType.LED_LIGHT,LightOperType.OPEN);
                    MEScanner.operateLight(ScanLightType.RED_LIGHT,LightOperType.OPEN);
                }
            }
            ScannerExtParams scannerExtParams = new ScannerExtParams();
            scannerExtParams.setOnce(true);
            MEScanner.startScan(getApplicationContext(),scanType,surfaceView,30, new ScannerListener() {

                @Override
                public void onTimeout() {
                    isFinish = true;
                    finish();
                    Message scanMsg = new Message();
                    scanMsg.what = AppConfig.ScanResult.SCAN_TIMEOUT;
                    MainActivity.getScanEventHandler().sendMessage(scanMsg);
                }

                @Override
                public void onResponse(String[] barcodes) {
                    Log.d(TAG,"---------------onResponse------barcodes---"+barcodes.length+"--"+barcodes[0]);
                    spi.play();
                    Message scanMsg = new Message();
                    scanMsg.what = AppConfig.ScanResult.SCAN_RESPONSE;
                    Bundle scanBundle = new Bundle();
                    scanBundle.putStringArray("barcodes", barcodes);
                    scanMsg.setData(scanBundle);
                    MainActivity.getScanEventHandler().sendMessage(scanMsg);

                }

                @Override
                public void onFinish() {
                    Log.d(TAG,"---------------onFinish---------"+isFinish);
                    isFinish = true;
                    Message scanMsg = new Message();
                    scanMsg.what = AppConfig.ScanResult.SCAN_FINISH;
                    MainActivity.getScanEventHandler().sendMessage(scanMsg);
                    finish();
                }

                @Override
                public void onError(int i, String s) {
                    Log.e(TAG,"-----onError--errorCode:"+i+";message:"+s);
                    isFinish = true;
                    Message scanMsg = new Message();
                    scanMsg.what = AppConfig.ScanResult.SCAN_ERROR;
                    Bundle scanBundle = new Bundle();
                    scanBundle.putInt("errorCode", i);
                    scanBundle.putString("errormessage", s);
                    MainActivity.getScanEventHandler().sendMessage(scanMsg);
                    finish();
                }

                @Override
                public void onCancel() {
                    isFinish = false;
                    Message scanMsg = new Message();
                    scanMsg.what = AppConfig.ScanResult.SCAN_CANCEL;
                    MainActivity.getScanEventHandler().sendMessage(scanMsg);
                }
            },scannerExtParams);


//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(3000);
//                        MEScanner.operateLight(ScanLightType.FLASH_LIGHT,LightOperType.OPEN);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
        }catch(Exception e){
            e.printStackTrace();
            isFinish = true;
            Log.d(TAG,"---------------Exception---------"+e.getMessage());
            finish();
            e.getStackTrace();
            Message scanMsg = new Message();
            scanMsg.what = AppConfig.ScanResult.SCAN_ERROR;
            Bundle scanBundle = new Bundle();
            scanBundle.putInt("errorCode", 0);
            scanBundle.putString("errormessage", e.getMessage());
            scanMsg.setData(scanBundle);
            MainActivity.getScanEventHandler().sendMessage(scanMsg);
        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(5000);
//                    MEScanner.operateLight(ScanLightType.FLASH_LIGHT,LightOperType.OPEN);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"------onPause--------isFinish：" + isFinish);
        if(!isFinish){
            MEScanner.stopScan();
        }

        if (scanAnim != null && scanAnim.isRunning()) {
            scanAnim.stop();
        }
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.d(TAG,"---------------keyCode---------"+keyCode);//700S设备 左边是24 右边是25
        Log.d(TAG,"---------------event---------"+event);
//		if((keyCode==KeyEvent.KEYCODE_VOLUME_UP&& event.getRepeatCount() == 0)){
//			logger.debug("发起700扫码");
//			startScan();
//		}else if((keyCode==KeyEvent.KEYCODE_VOLUME_DOWN&& event.getRepeatCount() == 0)){
//			logger.debug("发起700扫码");
//			startScan();
//		}

        if(keyCode==KeyEvent.KEYCODE_BACK){
            Log.d(TAG,"回退键");
            isFinish=false;
            finish();


        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"---------------onDestroy---------");
        try {
            spi.release();
            settingManager.setAppSwitchKeyEnabled(true);
            settingManager.setHomeKeyEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try{
            if (requestCode == Code_PERMISSION) {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //权限被用户同意,做相应的事情
                    startScan(scanType);
                } else {
                    //权限被用户拒绝，做相应的事情
                    Message scanMsg = new Message();
                    scanMsg.what = AppConfig.ScanResult.SCAN_ERROR;
                    Bundle scanBundle = new Bundle();
                    scanBundle.putInt("errorCode", 0);
                    scanBundle.putString("errormessage","摄像头动态授权失败");
                    scanMsg.setData(scanBundle);
                    MainActivity.getScanEventHandler().sendMessage(scanMsg);
                    finish();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            finish();

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
