package com.newland.sdk.me.module.scanner.util;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.newland.os.NdkApi;
import android.newland.os.NlBuild;
import android.newland.scan.ScanUtil;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.newland.sdk.me.module.scanner.MEScanner;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("deprecation")
public class CameraManagerUtil {
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("CameraManagerUtil");

    private boolean isDEBUG = true;
    private String TAG = CameraManagerUtil.class.getName();
    // 屏幕的宽
    private int SCREEN_WIDTH;
    // 屏幕的高
    private int SCREEN_HEIGHT;

    private Context mContext;
    private Camera mCamera;

    // USB亮度
    String USB_BRIGHT = "luma-adaptation";
    String USB_BRIGHT_MIN = "min-brightness";
    String USB_BRIGHT_MAX = "max-brightness";
    // USB锐化
    String USB_SHARPESS = "sharpness";
    String USB_SHARPESS_MIN = "min-sharpness";
    String USB_SHARPESS_MAX = "max-sharpness";
    // USB对比
    String USB_CONTRAST = "contrast";
    String USB_CONTRAST_MIN = "min-contrast";
    String USB_CONTRAST_MAX = "max-contrast";

    //usb摄像头默认参数
    String defSharpness, defBright, defContrast;
    int defExposureCompensation;

    // 预览UI界面大小,预览帧界面大小
    private Point cameraResolution, screenResolution;
    private Rect cameraRect, screenRect;
    int previewWidth = 1280;
    int previewHeight = 720;
    private final String HARDWARE_CONFIG = "NL_HARDWARE_CONFIG";
    /**
     * 1-华捷 2-云从 3-奥比
     */
    private int cameraType = 0;
    private  TextureView textureView;
    public CameraManagerUtil(Context context, Camera camera, TextureView textureView) {
        this.mContext = context;
        this.mCamera = camera;
        this.textureView = textureView;
    }


    /**
     * 摄像头
     * @return
     */
    public boolean isCameraType(){
        String hardwareConfig = "Unknow";
//        if (NdkApi.filedExist(HARDWARE_CONFIG)) {
            try {
                hardwareConfig = NlBuild.VERSION.NL_HARDWARE_CONFIG.substring(44, 46);
                switch (hardwareConfig){
                    case "01":
                        cameraType = 1;
                        break;
                    case "02":
                        cameraType = 2;
                        break;
                    case "03":
                        cameraType = 3;
                        break;
                    default:
                        break;
                }
            } catch (StringIndexOutOfBoundsException e) {
                return false;
            }
  //      }
        return !"FF".equals(hardwareConfig);
    }
    /**
     * 设置相机的相关属性
     * @return
     */
    public void setCameraParams() {
        screenResolution = getFullScreentPoint();
        SCREEN_WIDTH = screenResolution.x;
        SCREEN_HEIGHT = screenResolution.y;
        isCameraType();
        Parameters params = mCamera.getParameters();
        params.setPreviewFormat(ImageFormat.NV21); //设置返回的格式

        setSupportPreviewSize(params);
        setPreviewSize(mCamera);
//        Camera.Parameters parameters = mCamera.getParameters();
//        List<Camera.Size> preSize = parameters.getSupportedPreviewSizes();
//        for (Camera.Size sizi:preSize) {
//            deviceLogger.debug("setSupportPreviewSize: " + sizi.width + sizi.height );
//        }
//        if(preSize!=null&&preSize.size()>0){
//            previewWidth = preSize.get(0).width;
//            previewHeight = preSize.get(0).height;
//        }
        deviceLogger.debug("cameraType:"+cameraType+"previewWidth: " + previewWidth + ";previewHeight:"+previewHeight );
        switch (cameraType){
            case 1:
                params.setPreviewSize(previewWidth, previewHeight);
                setUsbCameraParamters(params);
                break;
            case 2:
                params.setPreviewSize(previewWidth, previewHeight);

                 params.setPreviewSize(1280, 960);
                mCamera.setDisplayOrientation(90);
                if(textureView!=null){
                    Matrix matrix = textureView.getTransform(new Matrix());
                    matrix.setScale(-1, 1);
                    int width1 = textureView.getWidth();
                    matrix.postTranslate(width1, 0);
                    textureView.setTransform(matrix);
                }
                break;
            case 3:
                params.setPreviewSize(previewWidth, previewHeight);
                break;
            default:
                break;
        }
        mCamera.setParameters(params);
    }

    /**
     * 获取全屏幕大小
     */
    private Point getFullScreentPoint() {
        WindowManager windowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        if (Build.VERSION.SDK_INT >= 19) {
            // 可能有虚拟按键的情况
            display.getRealSize(outPoint);
        } else {
            // 不可能有虚拟按键
            display.getSize(outPoint);
        }
        return new Point(outPoint.x, outPoint.y);
    }

    private int NLS_WIDTH = 640;
    private int NLS_HEIGHT = 480;
    // normal screen
    private static final int MIN_PREVIEW_PIXELS = 640 * 480;
    private static final double MAX_ASPECT_DISTORTION = 0.15;
    /**
     * 选择和屏幕匹配的预览大小
     *
     * @param params
     */
    private void setSupportPreviewSize(Parameters params) {
        List<Size> preSize = params.getSupportedPreviewSizes();
        // 1、保持屏幕宽高和分辨率方向一致
        List<Size> supportSize = new ArrayList<Size>();
        for (Size sizi:preSize) {
            Log.e(TAG, "setSupportPreviewSize: " + sizi.width + sizi.height );
        }
        int mPreviewWidth = 0;
        int mPreviewHeight = 0;

        if ((SCREEN_WIDTH - SCREEN_HEIGHT) * (preSize.get(0).width - preSize.get(0).height) < 0) {
            int temp = SCREEN_WIDTH;
            SCREEN_WIDTH = SCREEN_HEIGHT;
            SCREEN_HEIGHT = temp;
        }


        // 2、筛选要求范围内的分辨率
        double screenAspectRatio = (double) SCREEN_WIDTH
                / (double) SCREEN_HEIGHT;

        for (int i = 0; i < preSize.size(); i++) {
            // 大于NLS解码库最大分辨率不添加
            if (preSize.get(i).width > NLS_WIDTH
                    || preSize.get(i).height > NLS_HEIGHT) {
                continue;
            }
            // 低于设定的预览帧不添加
            if (preSize.get(i).width * preSize.get(i).height < MIN_PREVIEW_PIXELS) {
                continue;
            }

            // 分辨率和设定分辨率一致最优,且保证小于nls要求的尺寸
            if (preSize.get(i).width == SCREEN_WIDTH
                    && SCREEN_HEIGHT == preSize.get(i).height) {
                mPreviewHeight = SCREEN_HEIGHT;
                mPreviewWidth = SCREEN_WIDTH;
                break;
            }

            // 分辨率宽高比 与要求宽高比在一定范围内选择
            double similarity = (double) preSize.get(i).width
                    / preSize.get(i).height;
            if (Math.abs(similarity - screenAspectRatio) <= MAX_ASPECT_DISTORTION) {
                supportSize.add(preSize.get(i));
            }
        }
        // 3、分辨率按降序排列
        Collections.sort(supportSize, new Comparator<Object>() {
            @Override
            public int compare(Object obj1, Object obj2) {
                return (((Size) obj2).width - ((Size) obj1).width);
            }
        });

        // 4、选择最优分辨率
        if (mPreviewHeight != SCREEN_HEIGHT && mPreviewWidth != SCREEN_WIDTH) {
            if (supportSize.size() != 0) {
                mPreviewHeight = supportSize.get(0).height;
                mPreviewWidth = supportSize.get(0).width;
            } else {
                for (int i = 0; i < preSize.size(); i++) {
                    if (preSize.get(i).width > NLS_WIDTH
                            || preSize.get(i).height > NLS_HEIGHT) {
                        continue;
                    }
                    mPreviewHeight = preSize.get(i).height;
                    mPreviewWidth = preSize.get(i).width;
                    break;
                }
            }
        }
        DEBUG("mPrevireWidth:" + mPreviewWidth + "mPreviewHeight:"
                + mPreviewHeight);
//        params.setPreviewSize(mPreviewWidth, mPreviewHeight);
        if (Build.MODEL.equals("FPOS F10")){//F10设备
            mPreviewWidth = 800;
            mPreviewHeight = 600;
        }

        if(textureView!=null){
            ViewGroup.LayoutParams layoutParams = textureView.getLayoutParams();
            layoutParams.width = mPreviewWidth;
            layoutParams.height = mPreviewHeight;
            textureView.setLayoutParams(layoutParams);
        }
        cameraResolution = new Point(mPreviewWidth, mPreviewHeight);
    }

    /**
     * 设置USB摄像头参数
     *
     * @param parameters
     */
    private void setUsbCameraParamters(Parameters parameters) {

        defSharpness = parameters.get(USB_SHARPESS);
        defBright = parameters.get(USB_BRIGHT);
        defContrast = parameters.get(USB_CONTRAST);
        defExposureCompensation = parameters.getExposureCompensation();

        DEBUG("default  sharpness=" + defSharpness);
        DEBUG("default  bright=" + defBright);
        DEBUG("default  contrast=" + defContrast);
        DEBUG("default  exposure-compensation=" + defExposureCompensation);

        String minSharpness = parameters.get(USB_SHARPESS_MIN);
        String maxSharpness = parameters.get(USB_SHARPESS_MAX);

        String minBright = parameters.get(USB_BRIGHT_MIN);
        String maxBright = parameters.get(USB_BRIGHT_MAX);

        String minContrast = parameters.get(USB_CONTRAST_MIN);
        String maxContrast = parameters.get(USB_CONTRAST_MAX);

        int minExposureCompensation = parameters.getMinExposureCompensation();
        int maxExposureCompensation = parameters.getMaxExposureCompensation();

        if (minExposureCompensation == 0 && maxExposureCompensation == 0) {
            defExposureCompensation = -1;
        }

        DEBUG("sharpness :" + "[" + minSharpness + "," + maxSharpness + "]");
        DEBUG("bright :" + "[" + minBright + "," + maxBright + "]");
        DEBUG("contrast :" + "[" + minContrast + "," + maxContrast + "]");
        DEBUG("exposure-compensation :" + "[" + minExposureCompensation + ","
                + maxExposureCompensation + "]");


        //parameters.set(USB_BRIGHT, minBright);// 亮度最小
//        parameters.set(USB_SHARPESS, minSharpness);// 关闭锐化
//        parameters.set(USB_CONTRAST, maxContrast);// 对比度最大
//        if (defExposureCompensation != -1) {
//            parameters.setExposureCompensation(maxExposureCompensation);// 背光补偿最大
//        }
    }

    /**
     * 释放调用
     * @param camera
     */
    public void restoreUsbCameraParams(Camera camera) {
        if (camera != null) {
            switch (cameraType){
                case 1:
                    Parameters parameters = camera.getParameters();
                    if (defBright != null) {
                        parameters.set(USB_BRIGHT, defBright);
                    }
                    if (defContrast != null) {
                        parameters.set(USB_CONTRAST, defContrast);
                    }
                    if (defSharpness != null) {
                        parameters.set(USB_SHARPESS, defSharpness);
                    }
                    if (defExposureCompensation != -1) {
                        parameters.setExposureCompensation(defExposureCompensation);
                    }
                    camera.setParameters(parameters);
                    break;
                case 3:
                    deviceLogger.error("[restoreUsbCameraParams] camera reset");
   //                 UsbCamera.jniReset();
                    break;
                default:
                    break;
            }
        }


    }


    /**
     * 设置camera旋转角度
     */
    private void setCameraDisplayOrientation(int cameraId, Parameters params) {
        if (mContext.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
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


    private void DEBUG(String message) {
        if (isDEBUG) {
            Log.d(TAG, message);
        }
    }

    /**
     * 设置预览宽高
     * 固件最大支持1280*960
     * @param mCamera
     */
    private void setPreviewSize(Camera mCamera){
        try{
            deviceLogger.debug("[setPreviewSize]");
            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> preSize = parameters.getSupportedPreviewSizes();
            for (Camera.Size sizi:preSize) {
                deviceLogger.debug("setSupportPreviewSize: " + sizi.width + sizi.height );
            }
            if(preSize!=null && preSize.size()>0){
                for(Camera.Size sizi:preSize){
                    if(sizi.width<=1280 && sizi.height<=960){
                        previewWidth = sizi.width;
                        previewHeight = sizi.height;
                        deviceLogger.debug("[setPreviewSize] previewWidth:"+previewWidth+";previewHeight:"+previewHeight);
                        break;
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
