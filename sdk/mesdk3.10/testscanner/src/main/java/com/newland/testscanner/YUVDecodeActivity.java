package com.newland.testscanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.newland.SettingsManager;
import android.newland.content.NlContext;
import android.newland.os.NlBuild;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.newland.sdk.me.module.scanner.MEScanner;
import com.newland.sdk.module.scanner.DecodeListener;
import com.newland.sdk.module.scanner.LightOperType;
import com.newland.sdk.module.scanner.ScanLightType;
import com.newland.sdk.module.scanner.ScannerModule;
import com.newland.sdk.module.scanner.ScannerType;
import com.newland.testscanner.util.AppConfig;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author youjf
 * @description
 * @date 2020/5/20
 * @since V3.10.01
 */
public class YUVDecodeActivity extends Activity {
    private AnimationDrawable scanAnim;
    private ScannerType scanType;
    private Context context;
    ScannerModule scannerModule;
    private SoundPoolImpl spi;
    private FrameLayout backFL;
    private SurfaceView surfaceView;
    private RelativeLayout frontLL;
    private LinearLayout switch_fr;
    private LinearLayout switch_bc;
    private ImageView scanIV;
    int cameraID = 0;
    private SurfaceHolder holder;
    int previewWidth = 1280;
    int previewHeight = 720;
    private Point screenResolution;
    private Point cameraResolution=new Point(1280,720);
    private byte[] mBuffer;
    private int mBufferSize;
    private Camera mCamera;
    private int resultRotation;
    private int previewFormat;
    private String previewFormatString;
    private static final int TEN_DESIRED_ZOOM = 27;
    boolean isCameraPreview = false;
    private SurfaceTexture surfaceTexture  = new SurfaceTexture(10);
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    private SettingsManager settingManager;
    private String TAG = "YUVDecodeActivity";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        context=this;
        cameraID = getCameraNumInHardwareConfig();

        View view = View.inflate(this, R.layout.scan_view, null);
        setContentView(view);
        scannerModule = new MEScanner(null,getApplicationContext());


        spi = SoundPoolImpl.getInstance(1);
        spi.initLoad(YUVDecodeActivity.this,1);
        init();
        try {
            settingManager = (SettingsManager) getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
            settingManager.setAppSwitchKeyEnabled(false);
            settingManager.setHomeKeyEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(){
        int type=getIntent().getIntExtra("scanType", 0x00);//Front default
        if(type == 0x01){
            scanType = ScannerType.FRONT;
        }else{
            scanType = ScannerType.BACK;
        }
        surfaceView=(SurfaceView) findViewById(R.id.surfaceView);
        frontLL=(RelativeLayout) findViewById(R.id.ll_front);
        backFL=(FrameLayout) findViewById(R.id.fl_back);
        frontLL=(RelativeLayout) findViewById(R.id.ll_front);
        switch_fr = (LinearLayout) findViewById(R.id.ll_switch_front);
        switch_fr = (LinearLayout) findViewById(R.id.ll_switch_front);
        switch_bc=(LinearLayout)findViewById(R.id.ll_switch_back);
        backFL=(FrameLayout) findViewById(R.id.fl_back);
        scanIV=(ImageView) findViewById(R.id.iv_scan);


        scannerModule.initDecode(this);

        if(scanType == ScannerType.FRONT){
            System.out.print("------------前置---------");
            scanAnim = (AnimationDrawable)scanIV.getDrawable();
            if (scanAnim != null && !scanAnim.isRunning()) {
                scanAnim.start();
            }
        }else{
            System.out.print("------------后置---------");

        }
        switch_fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("---------------切换前置---------");
                switch_bc.setEnabled(true);
                switch_fr.setEnabled(false);
                scannerModule.stopDecode();
                destoryCamera();
                scanType = ScannerType.FRONT;
                startScan(scanType);


            }
        });

        switch_bc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("YUVDecode","---------------切换后置---------");
                switch_bc.setEnabled(false);
                switch_fr.setEnabled(true);
                if(Build.MODEL.equals("N700")){
                    scannerModule.operateLight(ScanLightType.LED_LIGHT,LightOperType.CLOSE);
                    scannerModule.operateLight(ScanLightType.RED_LIGHT,LightOperType.CLOSE);
                }
                scannerModule.stopDecode();
                destoryCamera();
                scanType = ScannerType.BACK;
                startScan(scanType);
            }
        });
        startScan(scanType);

    }

    private void startScan(ScannerType scanType){
        try{
            Log.d("---------","--------startScan,scanType:"+scanType);
            if(scanType==ScannerType.BACK){//后置的
                surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
                surfaceView.setVisibility(View.VISIBLE);
                frontLL.setVisibility(View.GONE);
                backFL.setVisibility(View.VISIBLE);
                boolean resutl = scannerModule.isSupScanCode(ScannerType.FRONT);
                switch_fr.setVisibility(View.GONE);
               if(resutl){
                    switch_fr.setVisibility(View.VISIBLE);
               }
                holder = surfaceView.getHolder();
                holder.addCallback(mcaCallback);
                holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            }else if(scanType==ScannerType.FRONT){
                surfaceView.setVisibility(View.GONE);
                surfaceView = null;
                backFL.setVisibility(View.GONE);
                frontLL.setVisibility(View.VISIBLE);
                scanAnim = (AnimationDrawable)scanIV.getDrawable();
                if (scanAnim != null && !scanAnim.isRunning()) {
                    scanAnim.start();
                }
                boolean resutl = scannerModule.isSupScanCode(ScannerType.BACK);
                switch_bc.setVisibility(View.GONE);
                if(resutl){
                    switch_bc.setVisibility(View.VISIBLE);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initCamera(null);
                    }
                }).start();

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(scannerModule!=null){
            scannerModule.stopDecode();
            destoryCamera();
        }
    }


    /**************************************/
    /*********** 摄像头处理模块 ************/
    /*************************************/
    private SurfaceHolder.Callback mcaCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            destoryCamera();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            System.out.println("------surfaceCreated------");
            initCamera(surfaceView);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            System.out.println("------surfaceChanged------");

            if (mCamera != null) {
                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void destoryCamera() {
        Log.d("","------destoryCamera------mCamera:"+mCamera);

        if (mCamera != null) {

            if (isCameraPreview) {
                mCamera.stopPreview();
                isCameraPreview = false;
            }
            mCamera.setPreviewCallback(null);
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.release();
            mCamera = null;
        }
    }

    public Camera.PreviewCallback MyPreviewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Log.d("---------","=============onPreviewFrame==========");
            if (scannerModule != null) {
                Log.d("YUVDecode", "onPreviewFrame,data's length=" + data.length + ",width="
                        + previewWidth + ",height=" + previewHeight+";mBufferSize:"+mBufferSize);
                if(scanType == ScannerType.FRONT){
                    if(mBuffer==null){
                        Log.d("---","mBuffer==null");
                        mBuffer=new byte[mBufferSize];
                    }
                    camera.addCallbackBuffer(mBuffer);
                }

                scannerModule.startYUVDecode(data, previewWidth, previewHeight, new DecodeListener() {
                    @Override
                    public void onResult(byte[] decodeResult) {
                        Log.d("---onResult--","decodeResult:"+(decodeResult==null?null:new String(decodeResult)));
                        scannerModule.stopDecode();

                        Message scanMsg = new Message();
                        scanMsg.what = AppConfig.ScanResult.SCAN_RESPONSE;
                        Bundle scanBundle = new Bundle();
                        scanBundle.putStringArray("barcodes", new String[]{new String (decodeResult)});
                        scanMsg.setData(scanBundle);
                        MainActivity.getScanEventHandler().sendMessage(scanMsg);
                        finish();
                    }

                    @Override
                    public void onError(int errorCode, String errMsg) {
                        Log.e("onError","-----------errorCode:"+errorCode+";errMsg:"+errMsg);
                        scannerModule.stopDecode();
                        Message scanMsg = new Message();
                        scanMsg.what = AppConfig.ScanResult.SCAN_ERROR;
                        Bundle scanBundle = new Bundle();
                        scanMsg.setData(scanBundle);
                        MainActivity.getScanEventHandler().sendMessage(scanMsg);
                        finish();

                    }
                });
                try{
                    Thread.sleep(10);
                    if(scanType == ScannerType.FRONT){
                        requestPreviewFrame();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        }
    };


    private void initCamera(SurfaceView surfaceView) {
        try{
            Log.d("-------------","---------initCamera-----surfaceView:"+surfaceView);
            if(surfaceView!=null){
                mCamera = Camera.open(cameraID);
                Camera.Parameters parameters = mCamera.getParameters();
                // parameters.setExposureCompensation(value);
                // mCamera.setParameters(parameters);
                // previewHeight = parameters.getPreviewSize().height;
                // previewWidth = parameters.getPreviewSize().width;

                List<Camera.Size> supportPreviewSizes = parameters.getSupportedPreviewSizes();
                for (Camera.Size s : supportPreviewSizes) {
                    Log.d("weicx", "supportPreviewSizes=" + s.width + "," + s.height);
                }
                Log.d("YUVDecodeActityty","initCamera width = " + previewWidth + ",height = "
                        + previewHeight);

                // setCameraDisplayOrientation(cameraID, parameters);
                // weicx modify
                // 1、设置预览分辨率
                parameters.setPreviewSize(previewWidth, previewHeight);
                //
                // 2、设置角度
                // parameters.set("orientation", "portrait");
                // parameters.set("rotation", 90);
                // mCamera.setDisplayOrientation(90);
                // 设置镜像
                // parameters.set("preview-flip", "flip-h");

                // 3、设置属性
                // setUsbCameraParamters(parameters);

                mCamera.setParameters(parameters);

                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.setPreviewCallback(MyPreviewCallback);
                mCamera.startPreview();
                isCameraPreview = true;
                Log.d("YUVDecodeActityty","start preview");
            }else{
                System.out.println("----------开前置照相机---------");
                mCamera = Camera.open(cameraID);// 支付摄像头CameraID  = 2....如果三个摄像头都有的话。0是副屏。。1是主屏。 2是扫码
                mCamera.setPreviewTexture(surfaceTexture);
                Camera.Parameters parameters = mCamera.getParameters();
                previewFormat = parameters.getPreviewFormat();//Preview预览区域
                previewFormatString = parameters.get("preview-format");
                WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                screenResolution = new Point(display.getWidth(), display.getHeight());

                Point screenResolutionForCamera = new Point();
                screenResolutionForCamera.x = screenResolution.x;
                screenResolutionForCamera.y = screenResolution.y;

                if (screenResolution.x < screenResolution.y) {
                    screenResolutionForCamera.x = screenResolution.y;
                    screenResolutionForCamera.y = screenResolution.x;
                }
               // cameraResolution = getCameraResolution(parameters, screenResolutionForCamera);

                setDesiredCameraParameters(mCamera,cameraID);
                mCamera.setPreviewCallbackWithBuffer(MyPreviewCallback);

              //  mCamera.setPreviewCallback(MyPreviewCallback);
                mCamera.startPreview();
//                mCamera.autoFocus(new Camera.AutoFocusCallback() {
//                    @Override
//                    public void onAutoFocus(boolean success, Camera camera) {
//                        if (isCameraPreview) {
//                            CameraManager.get().requestAutoFocus(this, ScanState.auto_focus);
//                        }
//                    }
//                });
                isCameraPreview = true;
            }

        }catch (Exception e){
            e.printStackTrace();
            Message scanMsg = new Message();
            scanMsg.what = AppConfig.ScanResult.SCAN_ERROR;
            Bundle scanBundle = new Bundle();
            scanMsg.setData(scanBundle);
            MainActivity.getScanEventHandler().sendMessage(scanMsg);
            finish();
        }


    }

    private void requestPreviewFrame(){
        Log.d("------","---------requestPreviewFrame-------");
        if(mCamera!=null){
            mCamera.setOneShotPreviewCallback(MyPreviewCallback);
        }
    }

    private static Point getCameraResolution(Camera.Parameters parameters,
                                             Point screenResolution) {

        String previewSizeValueString = parameters.get("preview-size-values");
        if (previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }

        Point cameraResolution = null;

        if (previewSizeValueString != null) {
            cameraResolution = findBestPreviewSizeValue(previewSizeValueString,
                    screenResolution);
        }

        if (cameraResolution == null) {
            cameraResolution = new Point((screenResolution.x >> 3) << 3,
                    (screenResolution.y >> 3) << 3);
        }
        return cameraResolution;
    }

    private static Point findBestPreviewSizeValue(
            CharSequence previewSizeValueString, Point screenResolution) {
        int bestX = 0;
        int bestY = 0;
        int diff = Integer.MAX_VALUE;
        for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {

            previewSize = previewSize.trim();
            int dimPosition = previewSize.indexOf('x');
            if (dimPosition < 0) {
                continue;
            }

            int newX;
            int newY;
            try {
                newX = Integer.parseInt(previewSize.substring(0, dimPosition));
                newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
            } catch (NumberFormatException nfe) {
                nfe.getStackTrace();
                continue;
            }

            int newDiff = Math.abs(newX - screenResolution.x)
                    + Math.abs(newY - screenResolution.y);
            if (newDiff == 0) {
                bestX = newX;
                bestY = newY;
                break;
            } else if (newDiff < diff) {
                bestX = newX;
                bestY = newY;
                diff = newDiff;
            }

        }

        if (bestX > 0 && bestY > 0) {
            return new Point(bestX, bestY);
        }
        return null;
    }

    void setDesiredCameraParameters(Camera camera,int cameraId) {
        Camera.Parameters parameters = camera.getParameters();
        Log.d("setPreviewSize","--------,setPreviewSize,cameraResolution.x:"+cameraResolution.x+";cameraResolution.y:"+cameraResolution.y);

        parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);

//        setCameraDisplayOrientation(cameraId,camera);
//        setFlash(parameters);
//        setZoom(parameters);
        camera.setParameters(parameters);

        mBufferSize=cameraResolution.x*cameraResolution.y;
//        mBufferSize=mBufferSize* ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888)/8;
        mBufferSize=mBufferSize*3/2;

        mBuffer= new byte[mBufferSize];
        camera.addCallbackBuffer(mBuffer);
    }
    public void setCameraDisplayOrientation(int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int rotation =display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            resultRotation = (info.orientation + degrees) % 360;
            resultRotation = (360 - resultRotation) % 360; // compensate the
            // mirror
        } else { // back-facing
            resultRotation = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(resultRotation);
    }

    private void setFlash(Camera.Parameters parameters) {
        parameters.set("flash-value", 2);
        parameters.set("flash-mode", "off");
    }

    private void setZoom(Camera.Parameters parameters) {

        String zoomSupportedString = parameters.get("zoom-supported");
        if (zoomSupportedString != null
                && !Boolean.parseBoolean(zoomSupportedString)) {
            return;
        }

        int tenDesiredZoom = TEN_DESIRED_ZOOM;

        String maxZoomString = parameters.get("max-zoom");//得到的缩放所允许的最大值为快照
        if (maxZoomString != null) {
            try {
                int tenMaxZoom = (int) (10.0 * Double
                        .parseDouble(maxZoomString));
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                Log.d("YUVDecode","Bad max-zoom:"+maxZoomString);
            }
        }

        String takingPictureZoomMaxString = parameters
                .get("taking-picture-zoom-max");
        if (takingPictureZoomMaxString != null) {
            try {
                int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                Log.d("","Bad taking-picture-zoom-max: "
                        + takingPictureZoomMaxString);

            }
        }

        String motZoomValuesString = parameters.get("mot-zoom-values");
        if (motZoomValuesString != null) {
            tenDesiredZoom = findBestMotZoomValue(motZoomValuesString,
                    tenDesiredZoom);
        }

        String motZoomStepString = parameters.get("mot-zoom-step");
        if (motZoomStepString != null) {
            try {
                double motZoomStep = Double.parseDouble(motZoomStepString
                        .trim());
                int tenZoomStep = (int) (10.0 * motZoomStep);
                if (tenZoomStep > 1) {
                    tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
                }
            } catch (NumberFormatException nfe) {
                // continue
            }
        }

        // Set zoom. This helps encourage the user to pull back.
        // Some devices like the Behold have a zoom parameter
        if (maxZoomString != null || motZoomValuesString != null) {
            parameters.set("zoom", String.valueOf(tenDesiredZoom / 10.0));
        }

        // Most devices, like the Hero, appear to expose this zoom parameter.
        // It takes on values like "27" which appears to mean 2.7x zoom
        if (takingPictureZoomMaxString != null) {
            parameters.set("taking-picture-zoom", tenDesiredZoom);
        }
    }

    private static int findBestMotZoomValue(CharSequence stringValues,
                                            int tenDesiredZoom) {
        int tenBestValue = 0;
        for (String stringValue : COMMA_PATTERN.split(stringValues)) {
            stringValue = stringValue.trim();
            double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException nfe) {
                return tenDesiredZoom;
            }
            int tenValue = (int) (10.0 * value);
            if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom
                    - tenBestValue)) {
                tenBestValue = tenValue;
            }
        }
        return tenBestValue;
    }

    /**
     * 获取硬件配置码里面照相机个数
     * @return
     */
    private int getCameraNumInHardwareConfig(){
        String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;// 硬件配置码
        Log.d(TAG,"[getCameraNumInHardwareConfig] config:"+config);
        if (config != null && config.length() >= 26) {
            String camereParam = config.substring(24, 26);
            Log.d(TAG,"[getCameraNumInHardwareConfig] camereParam:"+camereParam);
            if ("FF".equals(camereParam)) {
                return 0;
            }else{
                int numbers = Integer.parseInt(camereParam);
                return numbers;
            }
        }
        return 0;
    }
}
