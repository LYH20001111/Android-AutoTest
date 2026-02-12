package com.newland.sdkdemo.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
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

import com.newland.sdk.ModuleManage;
import com.newland.sdk.module.scanner.DecodeListener;
import com.newland.sdk.module.scanner.LightOperType;
import com.newland.sdk.module.scanner.ScanLightType;
import com.newland.sdk.module.scanner.ScannerModule;
import com.newland.sdk.module.scanner.ScannerType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.fragment.ScannerFragment;
import com.newland.sdkdemo.utils.SoundPoolImpl;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author youjf
 * @description
 * @date 2020/5/20
 * @since V3.10.01
 */
public class YUVDecodeActivity  extends Activity {
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
    int previewWidth = 640;
    int previewHeight = 480;
    private Point screenResolution;
    private Point cameraResolution=new Point(640,480);
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
    private static DeviceLogger logger = DeviceLoggerFactory.getLogger(YUVDecodeActivity.class);

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
        scannerModule = ModuleManage.getInstance().getScannerModule();


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

    public void init(){
        int type=getIntent().getIntExtra("scanType", 0x00);//Front default
        if(type == 0x01){
            scanType = ScannerType.FRONT;
        }else{
            scanType = ScannerType.BACK;
        }
        if(Build.MODEL.equals("N750P")){
            scanType = ScannerType.FRONT;
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
            logger.debug("---------------switch front---------");
            scanAnim = (AnimationDrawable)scanIV.getDrawable();
            if (scanAnim != null && !scanAnim.isRunning()) {
                scanAnim.start();
            }
        }else{
            logger.debug("---------------switch back---------");

        }
        switch_fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.debug("---------------switch front---------");
                switch_bc.setEnabled(true);
                switch_fr.setEnabled(false);
                ScannerFragment.scanner.stopDecode();
                destoryCamera();
                scanType = ScannerType.FRONT;
                startScan(scanType);


            }
        });

        switch_bc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logger.debug("---------------switch back---------");
                switch_bc.setEnabled(false);
                switch_fr.setEnabled(true);
                if(Build.MODEL.equals("N700")){
                    ScannerFragment.scanner.operateLight(ScanLightType.LED_LIGHT,LightOperType.CLOSE);
                    ScannerFragment.scanner.operateLight(ScanLightType.RED_LIGHT,LightOperType.CLOSE);
                }
                ScannerFragment.scanner.stopDecode();
                destoryCamera();
                scanType = ScannerType.BACK;
                startScan(scanType);
            }
        });
        startScan(scanType);

    }

    private void startScan(ScannerType scanType){
        try{
            logger.debug("--------startScan,scanType:"+scanType);
            if(scanType==ScannerType.BACK){
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
        ScannerModule scannerModule = ModuleManage.getInstance().getScannerModule();
        if(scannerModule!=null){
            scannerModule.stopDecode();
            destoryCamera();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            settingManager = (SettingsManager) getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
            settingManager.setAppSwitchKeyEnabled(true);
            settingManager.setHomeKeyEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**************************************/
    /*********** Camera processing module ************/
    /*************************************/
    private SurfaceHolder.Callback mcaCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            destoryCamera();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            logger.debug("------surfaceCreated------");
            initCamera(surfaceView);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            logger.debug("------surfaceChanged------");

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
        logger.debug("------destoryCamera------mCamera:"+mCamera);

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
            logger.debug("=============onPreviewFrame==========");
            if (scannerModule != null) {
                logger.debug("onPreviewFrame,data's length=" + data.length + ",width="
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
                        logger.debug("decodeResult:"+(decodeResult==null?null:new String(decodeResult)));
                        scannerModule.stopDecode();

                        Message scanMsg = new Message();
                        scanMsg.what = AppConfig.ScanResult.SCAN_RESPONSE;
                        Bundle scanBundle = new Bundle();
                        scanBundle.putStringArray("barcodes", new String[]{new String (decodeResult)});
                        scanMsg.setData(scanBundle);
                        ScannerFragment.getScanEventHandler().sendMessage(scanMsg);
                        finish();
                    }

                    @Override
                    public void onError(int errorCode, String errMsg) {
                        logger.debug("-----------errorCode:"+errorCode+";errMsg:"+errMsg);
                        scannerModule.stopDecode();
                        Message scanMsg = new Message();
                        scanMsg.what = AppConfig.ScanResult.SCAN_ERROR;
                        Bundle scanBundle = new Bundle();
                        scanMsg.setData(scanBundle);
                        ScannerFragment.getScanEventHandler().sendMessage(scanMsg);
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
            logger.debug("---------initCamera-----surfaceView:"+surfaceView);
            if(surfaceView!=null){//后置
                previewWidth = 640;
                previewHeight = 480;
                cameraResolution = new Point(previewWidth, previewHeight);

                String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;

                if (Build.MODEL.equals("N950") && config != null && NlBuild.VERSION.NL_HARDWARE_CONFIG.length() >= 10 && config.substring(8, 10).equals("15")) {
                    //N950 hard scanner not support yue mode.
                    Message scanMsg = new Message();
                    scanMsg.what = AppConfig.ScanResult.SCAN_ERROR;
                    Bundle scanBundle = new Bundle();
                    scanBundle.putString("errormessage","Not support.");
                    scanMsg.setData(scanBundle);
                    ScannerFragment.getScanEventHandler().sendMessage(scanMsg);
                    finish();
                    return;
                }else {
                    cameraID = 0;
                }

                mCamera = Camera.open(cameraID);
                Camera.Parameters parameters = mCamera.getParameters();
                // parameters.setExposureCompensation(value);
                // mCamera.setParameters(parameters);
                // previewHeight = parameters.getPreviewSize().height;
                // previewWidth = parameters.getPreviewSize().width;

                int minWidth = 0,minHeight = 0;boolean hasDefaultSize = false;
                List<Camera.Size> supportPreviewSizes = parameters.getSupportedPreviewSizes();
                for (Camera.Size s : supportPreviewSizes) {
                    logger.debug( "supportPreviewSizes=" + s.width + "," + s.height);
                    minWidth = s.width;
                    minHeight = s.height;
                    if(s.width == 640 && s.height == 480){
                        hasDefaultSize = true;
                    }
                }
                if(!hasDefaultSize){
                    previewWidth = minWidth;
                    previewHeight = minHeight;
                    cameraResolution = new Point(previewWidth, previewHeight);
                }

                logger.debug("initCamera width = " + previewWidth + ",height = " + previewHeight);

                parameters.setPreviewSize(previewWidth, previewHeight);
                setCameraDisplayOrientation(cameraID,parameters);
                mCamera.setParameters(parameters);

                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.setPreviewCallback(MyPreviewCallback);
                mCamera.startPreview();
                isCameraPreview = true;
                logger.debug("start preview");
            }else{//前置
                logger.debug("----------Open front camera---------");
                previewWidth = 640;
                previewHeight = 480;
                cameraResolution = new Point(previewWidth, previewHeight);
                String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                if (Build.MODEL.equals("N950") && config != null && NlBuild.VERSION.NL_HARDWARE_CONFIG.length() >= 10 && config.substring(8, 10).equals("15")) {
                    cameraID = 0;
                }else {
                    cameraID = 1;
                }
                if(Build.MODEL.equals("N750P")){
                    cameraID = 0;
                }
                mCamera = Camera.open(cameraID);// Payment camera CameraID  = 2. If device has three cameras.  0 is the secondary screen.  1 is the home screen.  2 is a code camera
                mCamera.setPreviewTexture(surfaceTexture);
                Camera.Parameters parameters = mCamera.getParameters();

                int minWidth = 0,minHeight = 0;boolean hasDefaultSize = false;
                List<Camera.Size> supportPreviewSizes = parameters.getSupportedPreviewSizes();
                for (Camera.Size s : supportPreviewSizes) {
                    logger.debug( "supportPreviewSizes=" + s.width + "," + s.height);
                    minWidth = s.width;
                    minHeight = s.height;
                    if(s.width == 640 && s.height == 480){
                        hasDefaultSize = true;
                    }
                }
                if(!hasDefaultSize){
                    previewWidth = minWidth;
                    previewHeight = minHeight;
                    cameraResolution = new Point(previewWidth, previewHeight);
                }

                previewFormat = parameters.getPreviewFormat();//Preview area
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
            ScannerFragment.getScanEventHandler().sendMessage(scanMsg);
            finish();
        }


    }

    private void requestPreviewFrame(){
        logger.debug("---------requestPreviewFrame-------");
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
        logger.debug("--------,setPreviewSize,cameraResolution.x:"+cameraResolution.x+";cameraResolution.y:"+cameraResolution.y);

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
     * Get the number of cameras in the hardware configuration code
     * @return
     */
    private int getCameraNumInHardwareConfig(){
        String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;// hardware configuration code
        logger.debug("[getCameraNumInHardwareConfig] config:"+config);
        if (config != null && config.length() >= 26) {
            String camereParam = config.substring(24, 26);
            logger.debug("[getCameraNumInHardwareConfig] camereParam:"+camereParam);
            if ("FF".equals(camereParam)) {
                return 0;
            }else if(android.os.Build.MODEL.startsWith("F")){
                int numbers = Integer.parseInt(camereParam);
                return numbers;
            }
        }
        return 0;
    }

    /**
     * Set the camera rotation Angle
     */
    private void setCameraDisplayOrientation(int cameraId, Camera.Parameters params) {
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            params.set("orientation", "portrait");
            if (cameraId >= Camera.CameraInfo.CAMERA_FACING_FRONT) {
                params.set("rotation", 270);
                mCamera.setDisplayOrientation(270);
            } else {
                params.set("rotation", 90);
                mCamera.setDisplayOrientation(90);
            }
        } else {
            params.set("orientation", "landscape");
            if (cameraId >= Camera.CameraInfo.CAMERA_FACING_FRONT) {
                params.set("rotation", 180);
                mCamera.setDisplayOrientation(180);
            } else {
                params.set("rotation", 0);
                mCamera.setDisplayOrientation(0);
            }
        }
    }
}
