package com.newland.sdk.me.module.scanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.newland.NLUART3Manager;
import android.newland.content.NlContext;
import android.newland.os.NlBuild;
import android.newland.scan.ScanUtil;
import android.newland.scan.SoftEngine;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.newland.sdk.me.module.scanner.util.CameraManagerUtil;
import com.newland.sdk.me.module.scanner.util.FileIOUtils;
import com.newland.sdk.me.module.scanner.util.FileUtils;
import com.newland.sdk.module.scanner.DecodeListener;
import com.newland.sdk.module.scanner.DefaultScannerViewParams;
import com.newland.sdk.module.scanner.LightOperType;
import com.newland.sdk.module.scanner.ScanLightType;
import com.newland.sdk.module.scanner.ScanListener;
import com.newland.sdk.module.scanner.ScannerExtParams;
import com.newland.sdk.module.scanner.ScannerListener;
import com.newland.sdk.module.scanner.ScannerModule;
import com.newland.sdk.module.scanner.ScannerType;
import com.newland.sdk.module.scanner.StartStopCapability;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

public class MEScanner extends AbstractModule implements ScannerModule {
    /**
     * 前置扫码<p>
     * front scanner
     */
    public static final int SCAN_FRONT = 1;
    /**
     * 后置扫码<p>
     * back scanner
     */
    public static final int SCAN_BACK = 0;

    /**
     * 硬件扫码头<p>
     *  scanner
     */
    public static final int HARD_SCANNER = 1000;
    /**
     * 对焦灯模式：常灭
     */
    public static final int FOCUS_OFF = 0;
    /**
     * 对焦灯模式：常亮
     */
    public static final int FOCUS_ON = 2;
    /**
     * 对焦灯模式：识读时闪
     */
    public static final int FOCUS_READING = 1;
    /**
     * 连续扫码
     */
    public static final int MODE_CONTINUALLY = 2;
    /**
     * 单次扫码
     */
    public static final int MODE_ONCE = 1;
    /**
     * 扫码类型
     */
    private ScannerType scannerType = ScannerType.FRONT;
    /**
     * 使用nls自动识别解码库
     */
    private static final int NLS = 1;
    /**
     * 使用ZXING开源库
     */
    private static final int ZXING = 0;

    private volatile ScanUtil scanUtil;
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("MEScanner");
    private volatile ScannerListener scannerlistener = null;
    private volatile ScanListener scanListener=null;
    private volatile ScanRunnable scanRunnable = null;
    private LinkedBlockingQueue<String> queue;

    private volatile boolean isStop = false;
    private volatile boolean canbeRelease = true;
    private String scanVersion = null;
    private Properties scanProperties;
    private NLUART3Manager uart3Manager;
    private DecodeListener decodeListener = null;
    private SoftEngine softEngine = null;
    private SurfaceView surfaceView;
    private ScannerExtParams scannerExtParams;
    int cameraID = 0;
    private SurfaceHolder holder;
    int previewWidth = 1280;
    int previewHeight = 720;
    private Camera mCamera;
    boolean isCameraPreview = false;
    private Object decodeObject = new Object();
    private boolean isOnce;
    private Point cameraResolution = new Point(1280, 720);
    private byte[] mBuffer;
    private int mBufferSize;
    private Context context;
    private volatile boolean isTimeOut = false;
    private SurfaceTexture surfaceTexture = new SurfaceTexture(10);
    public final String FACE_PACKAGE = "com.newland.face_service";
    private volatile boolean isReleasing = false;
    private LinkedBlockingQueue<byte[]> queueb;
    private boolean scanResultIsByte=false;


    public MEScanner(AbstractDevice owner, final Context context) {
        super(owner);
        String version = getScanVersion();
        if (version != null && !"${scan.version}".equals(version)) {
            deviceLogger.debug("Scan Module Version:" + getScanVersion());
        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    boolean isInstalledFaceService = checkAppInstalled(context,FACE_PACKAGE);
//                    if(!isInstalledFaceService){//有安装人脸服务就不先做初始化解码库，否则可能sdk和人脸服务同时做初始化，会有问题
//                        softEngine = SoftEngine.getInstance();//第一次初始化很慢，1秒多
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }).start();

    }

    @Override
    public boolean isStandardModule() {
        return true;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return ModuleType.SCANNER;
    }

    @Override
    public String getExModuleType() {
        return null;
    }

    class ScanRunnable implements Runnable {
        private Object sync = new Object();
        private int timeSecond;

        public ScanRunnable(int timeSecond) {
            this.timeSecond = timeSecond;
        }

        @Override
        public void run() {
            try {
                if(scanResultIsByte){
                    queueb = new LinkedBlockingQueue<byte[]>();
                    deviceLogger.debug("[ScanRunnable],isStop:" + isStop + ";scanUtil:" + scanUtil);
                    while (true) {
                        if (isStop) {
                            return;
                        }
                        if (null == scanUtil) {
                            deviceLogger.error("[ScanRunnable] scan is not init!");
                            break;
                        }
                        deviceLogger.debug("----------[ScanRunnable]scanUtil.doScan----------");
                        byte[] value = (byte[]) scanUtil.doScanWithRawByte();
                        deviceLogger.error("[ScanRunnable]:value!" + (value == null ? "null" : InnerUtils.hexString(value)));
                        if (value != null && isOnce) {
                            queueb.put(Arrays.copyOfRange(value, 0, value.length));
                            Thread.sleep(10);
                            break;
                        } else if (value != null && scanListener != null) {
                            queueb.put(Arrays.copyOfRange(value, 0, value.length));
                            scanListener.onResponse(new byte[][]{Arrays.copyOfRange(value, 0, value.length)});
                            deviceLogger.debug("[ScanRunnable] scannerlistener.onResponse---");
                        }
                        Thread.sleep(10);
                    }
                    this.notifyWaiting();

                }else{
                    queue = new LinkedBlockingQueue<String>();
                    RetryScanUtils retryScanUtils = new RetryScanUtils();
                    deviceLogger.debug("[ScanRunnable],isStop:" + isStop + ";scanUtil:" + scanUtil);
                    while (true) {
                        if (isStop) {
                            return;
                        }
                        if (null == scanUtil) {
                            deviceLogger.error("[ScanRunnable] scan is not init!");
                            break;
                        }
                        deviceLogger.debug("----------[ScanRunnable]scanUtil.doScan----------");
                        String value = (String) scanUtil.doScan();
                        if (value != null && value.startsWith("S") && isOnce) {
                            if(retryScanUtils.isToDoRetry(value.substring(1))){
                                //规避910 pro机器扫码偶现返回上一笔码值
                                Thread.sleep(10);
                                continue;
                            }else{
                                queue.put(value.substring(1));
                                Thread.sleep(10);
                                break;
                            }
                        } else if (value != null && value.startsWith("S") && scannerlistener != null) {
                            queue.put(value.substring(1));
                            scannerlistener.onResponse((new String[]{value.substring(1)}));
                            deviceLogger.debug("[ScanRunnable] scannerlistener.onResponse---");
                        }
                        if(isSupHardScan(ScannerType.BACK) && !isOnce && Build.MODEL!=null && Build.MODEL.equalsIgnoreCase("N950")){
                            Thread.sleep(25);//硬解码设备，连续扫码，间隔要25ms才够，之前默认10ms 只能第一次成功
                        }else{
                            Thread.sleep(10);
                        }

                    }
                    this.notifyWaiting();

                }

            } catch (Exception e) {
                deviceLogger.debug("scannerlistener==null?" + (scannerlistener == null) + ";isTimeOut:" + isTimeOut);
                if (scannerlistener != null) {
                    if (isTimeOut != true) {//超时release时，扫码线程刚好在做doscan时，doscan会有异常
                        deviceLogger.error("[ScanRunnable] failed to do scan!", e);
                        surfaceView = null;
                        scannerlistener.onError(ErrorCode.UNKNOWN, "" + e);
                        scannerlistener = null;
                    }
                }
                if (scanListener != null) {
                    if (isTimeOut != true) {//超时release时，扫码线程刚好在做doscan时，doscan会有异常
                        deviceLogger.error("[ScanRunnable] failed to do scan!", e);
                        surfaceView = null;
                        scanListener.onError(ErrorCode.UNKNOWN, "" + e);
                        scanListener = null;
                    }
                }
                this.notifyWaiting();
            } catch (NoSuchMethodError e) {
                deviceLogger.error("[ScanRunnable] no support this method", e);
                if (scannerlistener != null) {
                    scannerlistener.onError(ErrorCode.SCANNER_UNSUPPORT, "" + e);
                    scannerlistener = null;
                }
                if (scanListener != null) {
                    scanListener.onError(ErrorCode.SCANNER_UNSUPPORT, "" + e);
                    scanListener = null;
                }
                this.notifyWaiting();

            }
        }

        void startWaiting() {
            synchronized (sync) {
                try {
                    sync.wait(timeSecond * 1000);
                    if (!isStop && scannerlistener != null && queue.isEmpty()) {
                        deviceLogger.debug("[ScanRunnable] timeout notify");
                        isStop = true;
                        canbeRelease = false;
                        isTimeOut = true;
                        release();
                        surfaceView = null;
                        scannerlistener.onTimeout();
                        deviceLogger.debug("[ScanRunnable] startWaiting onTimeout  scannerlistener = null");
                        scannerlistener = null;
                    }
                    if (!isStop && scanListener != null && queueb.isEmpty()) {
                        deviceLogger.debug("[ScanRunnable] timeout notify");
                        isStop = true;
                        canbeRelease = false;
                        isTimeOut = true;
                        release();
                        surfaceView = null;
                        scanListener.onTimeout();
                        deviceLogger.debug("[ScanRunnable] startWaiting onTimeout  scanListener = null");
                        scanListener = null;
                    }

                } catch (Exception e) {
                }
            }
        }

        public void notifyWaiting() {
            synchronized (sync) {
                isStop = true;
                sync.notifyAll();
            }
        }
    }

    public class ScanThread extends Thread {
        private MEScanner MEScanner;
        private int timeout;
        private boolean isFinish;

        public ScanThread(MEScanner MEScanner, int timeout) {
            this.MEScanner = MEScanner;
            this.timeout = timeout;
            isFinish = false;

        }

        @Override
        public void run() {
            isFinish = false;
            deviceLogger.debug("[ScanThread] 超时时间 " + timeout);
            scanRunnable = new ScanRunnable(timeout);
            new Thread(scanRunnable).start();
            scanRunnable.startWaiting();
            if (scanResultIsByte) {
                deviceLogger.debug("[ScanThread] currentTimeMillis2 notify waitting-" + System.currentTimeMillis() + ";queueb.isEmpty()=" + queueb.isEmpty() + ";scanListener==null?" + (scanListener == null));
                if (!queueb.isEmpty() && scanListener != null) {
                    ArrayList<byte[]> containers = new ArrayList<byte[]>();
                    int size = queueb.drainTo(containers);
                    deviceLogger.debug("[ScanThread] onResponse扫码结果长度：" + size);
                    scanListener.onResponse(containers.toArray(new byte[size][]));
                    isFinish = true;
                } else if (isStop && scanListener != null) {
                    deviceLogger.debug("[ScanThread] scanListener.onCancel()" + "scanListener = null");
                    surfaceView = null;
                    scanListener.onCancel();
                    scanListener = null;
                }
                if (canbeRelease) {
                    deviceLogger.debug("[ScanThread] canbeRelease canbeRelease  scanUtil = null");
                    isStop = true;
                    MEScanner.release();
                    scanUtil = null;
                }
                scanRunnable = null;

                if (isFinish && scanListener != null) {
                    deviceLogger.debug("[ScanThread] scanListener.onFinish()" + "scanListener = null;");
                    surfaceView = null;
                    scanListener.onFinish();
                    scanListener = null;
                }
            }else{
                deviceLogger.debug("[ScanThread] currentTimeMillis2 notify waitting-" + System.currentTimeMillis() + ";queue.isEmpty()=" + queue.isEmpty() + ";scannerlistener==null?" + (scannerlistener == null));
                if (!queue.isEmpty() && scannerlistener != null) {
                    ArrayList<String> containers = new ArrayList<String>();
                    int size = queue.drainTo(containers);
                    deviceLogger.debug("[ScanThread] onResponse扫码结果长度：" + size);
                    scannerlistener.onResponse(containers.toArray(new String[size]));
                    isFinish = true;
                } else if (isStop && scannerlistener != null) {
                    deviceLogger.debug("[ScanThread] scannerlistener.onCancel()" + "scannerlistener = null");
                    surfaceView = null;
                    scannerlistener.onCancel();
                    scannerlistener = null;
                }
                if (canbeRelease) {
                    deviceLogger.debug("[ScanThread] canbeRelease canbeRelease  scanUtil = null");
                    isStop = true;
                    MEScanner.release();
                    scanUtil = null;
                }
                scanRunnable = null;

                if (isFinish && scannerlistener != null) {
                    deviceLogger.debug("[ScanThread] scannerlistener.onFinish()" + "scannerlistener = null;");
                    surfaceView = null;
                    scannerlistener.onFinish();
                    scannerlistener = null;
                }

            }

        }
    }

    @Override
    public void startScan(final Context context, ScannerType scannerType, final SurfaceView surfaceView, final int timeout, final ScannerListener listener, final ScannerExtParams scannerExtParams) {
        synchronized (this) {
            if((NlBuild.VERSION.MODEL.equals("N750")|| NlBuild.VERSION.MODEL.equals("N750P") || NlBuild.VERSION.MODEL.equals("N950S-C")) &&  Camera.getNumberOfCameras() == 1){//N750P只有前置摄像头
                scannerType = ScannerType.FRONT;
            }else if(NlBuild.VERSION.MODEL.equals("N950S") && Camera.getNumberOfCameras() == 1){
                scannerType = ScannerType.BACK;
            }
            scanResultIsByte=false;
            deviceLogger.info("[startScan],scannerType:" + scannerType + ";surfaceView:" + surfaceView + ";timeout:" + timeout);
            isOnce = true;
            this.context = context.getApplicationContext();
            this.surfaceView = surfaceView;
            this.scannerExtParams = scannerExtParams;
            this.scannerType = scannerType;
            if (scannerExtParams != null) {
                isOnce = scannerExtParams.isOnce();
            }
            this.isOnce = isOnce;
            this.isStop = false;
            this.canbeRelease = true;
            this.isTimeOut = false;
            this.scannerlistener = listener;
            if (scannerExtParams != null && scannerExtParams.getDefaultScannerLayout() != null) {
                deviceLogger.debug("[startScan] default scanner layout ");
                DefaultScannerViewParams.setScannerExtParams(scannerExtParams);
                DefaultScannerViewParams.setEnableSurfaceView(scannerExtParams.getDefaultScannerLayout().isEnablePreview());
                if(NlBuild.VERSION.MODEL.equals("P300") && scannerExtParams.getDefaultScannerLayout().isEnablePreview()){
                    scannerType = ScannerType.BACK;
                }
                DefaultScannerViewParams.setScannerType(scannerType);
                DefaultScannerViewParams.setEnableSound(scannerExtParams.getDefaultScannerLayout().isEnableSound());
                DefaultScannerViewParams.setScannerModulel(this);
                DefaultScannerViewParams.setTimeOut(timeout);
                DefaultScannerViewParams.setScannerListener(listener);
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClassName(context, "com.newland.sdk.scanner.ScanViewActivity");
                context.startActivity(intent);
                return;
            }
            int cameraNubInHardware = getCameraNumInHardwareConfig();
            boolean isSupFaceRecognition = isSupFaceRecognition();

            if(!getHasSecModule() && (Build.MODEL.startsWith("CPOS") ||  android.os.Build.MODEL.equals("STAR A-6300")) && isSupScanCode(ScannerType.CUSTOMER_DISPLAY) && scannerType == ScannerType.BACK){//没安全模块的CPOS开后置，有副屏的情况，默认开副屏
                scannerType = ScannerType.CUSTOMER_DISPLAY;
            }
            if ((isSupFaceRecognition && cameraNubInHardware == 0 && android.os.Build.MODEL.startsWith("F")) || scannerType == ScannerType.CUSTOMER_DISPLAY || (surfaceView != null && scannerType == ScannerType.FRONT && Build.VERSION.SDK_INT < 24)) {
                deviceLogger.debug("[startScan]  use decode surfaceView:" + surfaceView + ";isOnce:" + isOnce + ";scannerlistener:" + scannerlistener);

                if (surfaceView != null && scannerType == ScannerType.FRONT && Build.VERSION.SDK_INT < 24) {
                    cameraID = 0x01;
                }

                queue = new LinkedBlockingQueue<String>();
                String hardwareConfig = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                if (hardwareConfig != null && hardwareConfig.length() >= 46 && ("02".equals(hardwareConfig.substring(44, 46)) || "03".equals(hardwareConfig.substring(44, 46)))) {
                    previewHeight = 960;
                }

                try {
                    initDecode(context);
                    if (surfaceView != null) {
                        deviceLogger.debug("[startScan]surfaceView!=null");
                        holder = surfaceView.getHolder();
                        holder.addCallback(mcaCallback);
                        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                        deviceLogger.debug("[startScan] holder:" + holder);
                    } else {
                        initCamera();
                    }
                    if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE) {
                        deviceLogger.debug("[startScan]解码开启起止符号");
                        int result = softEngine.scanSet("CODEBAR", "TrsmtStasrtStop", "1");//开启起始符中支付
                        deviceLogger.debug("[startScan]解码开启起止符号结果：result：" + result);
                    } else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.DISENABLE) {
                        deviceLogger.debug("[startScan]解码关闭起止符号");
                        int result = softEngine.scanSet("CODEBAR", "TrsmtStasrtStop", "0");//关闭起始符中支付
                        deviceLogger.debug("[startScan]解码关闭起止符号结果：" + result);
                    }else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE_NORMAL) {
                        deviceLogger.debug("---------[startScan]开启正向码");
                        int result = scanUtil.setNlsScn("QR", "VideoMode", "0");//开启正向码
                        deviceLogger.debug("[startScan]开启正向码：" + result);
                    }else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE_REVERSE) {
                        deviceLogger.debug("---------[startScan]开启反向码");
                        int result = scanUtil.setNlsScn("QR", "VideoMode", "1");//开启反向码
                        deviceLogger.debug("[startScan]开启反向码：" + result);
                    }else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE_NORMAL_REVERSE) {
                        deviceLogger.debug("---------[startScan]开启正反向码");
                        int result = scanUtil.setNlsScn("QR", "VideoMode", "2");//开启正反向码
                        deviceLogger.debug("[startScan]开启正反向码：" + result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    destoryCamera();
                    scannerlistener.onError(ErrorCode.DECODE, "" + e);
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (decodeObject) {
                            try {
                                decodeObject.wait(timeout * 1000);
                                if (isStop && scannerlistener != null) {
                                    deviceLogger.debug("[startScan] decode oncancel----");
                                    scannerlistener.onCancel();
                                }
                                if (!isStop && scannerlistener != null && queue.isEmpty()) {
                                    stopDecode();
                                    destoryCamera();
                                    isCameraPreview = false;
                                    deviceLogger.debug("[startScan] decode onTimeout----");
                                    scannerlistener.onTimeout();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                scannerlistener.onError(ErrorCode.DECODE, "Exception:" + e);
                            }
                        }
                    }
                }).start();

                return;
            }
            if (cameraNubInHardware == 1 && android.os.Build.MODEL.startsWith("F")) {//人脸设备，只有一个摄像头只能开0
                deviceLogger.info("[startScan] Build.MODEL:" + android.os.Build.MODEL + ";cameraNubInHardware==1");
                scannerType = ScannerType.BACK;
            }
            boolean initResult = initScanner(context, timeout, surfaceView, scannerType, listener);
            deviceLogger.debug("initScanner initResult="+initResult);
            if (!initResult) {
                return;
            }
            if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE) {
                deviceLogger.debug("--------[startScan]扫码开启起止符--------------");
                int result = scanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", "1");//开启起始符中支付
                deviceLogger.debug("---------[startScan]扫码开启起止符结果：" + result);
            } else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.DISENABLE) {
                deviceLogger.debug("---------[startScan]扫码关闭起止符");
                int result = scanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", "0");//关闭起始符中支付
                deviceLogger.debug("[startScan]扫码关闭起止符结果：" + result);
            }else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE_NORMAL) {
                deviceLogger.debug("---------[startScan]开启正向码");
                int result = scanUtil.setNlsScn("QR", "VideoMode", "0");//开启正向码
                deviceLogger.debug("[startScan]开启正向码：" + result);
            }else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE_REVERSE) {
                deviceLogger.debug("---------[startScan]开启反向码");
                int result = scanUtil.setNlsScn("QR", "VideoMode", "1");//开启反向码
                deviceLogger.debug("[startScan]开启反向码：" + result);
            }else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE_NORMAL_REVERSE) {
                deviceLogger.debug("---------[startScan]开启正反向码");
                int result = scanUtil.setNlsScn("QR", "VideoMode", "2");//开启正反向码
                deviceLogger.debug("[startScan]开启正反向码：" + result);
            }
            deviceLogger.debug("[startScan] currentTimeMillis startscan1-" + System.currentTimeMillis());
            if (null != scanRunnable) {
                deviceLogger.error("[startScan] scanner has been opened!");
                listener.onError(ErrorCode.SCANNER_INIT_FAILED, "scanner has been opened!");
                return;
            }

            Thread scanThread = new ScanThread(this, timeout);
            scanThread.start();
        }
    }

    @Override
    public void startScanForOversea(Context context, ScannerType scannerType, SurfaceView surfaceView, final int timeout, @NonNull ScanListener listener, ScannerExtParams scannerExtParams) {
        synchronized (this) {
            if((NlBuild.VERSION.MODEL.equals("N750")|| NlBuild.VERSION.MODEL.equals("N750P")) &&  Camera.getNumberOfCameras() == 1){//N750P只有前置摄像头
                scannerType = ScannerType.FRONT;
            }
            scanResultIsByte=true;
            deviceLogger.debug("[startScan],scannerType:" + scannerType + ";surfaceView:" + surfaceView + ";timeout:" + timeout);
            isOnce = true;
            this.context = context.getApplicationContext();
            this.surfaceView = surfaceView;
            this.scannerExtParams = scannerExtParams;
            this.scannerType = scannerType;
            if (scannerExtParams != null) {
                isOnce = scannerExtParams.isOnce();
            }
            this.isStop = false;
            this.canbeRelease = true;
            this.isTimeOut = false;
            this.scanListener = listener;
            if (scannerExtParams != null && scannerExtParams.getDefaultScannerLayout() != null) {
                listener.onError(ErrorCode.SCANNER_UNSUPPORT,"Don`t support.");
                return;
            }
            int cameraNubInHardware = getCameraNumInHardwareConfig();
            boolean isSupFaceRecognition = isSupFaceRecognition();


            if ((isSupFaceRecognition && cameraNubInHardware == 0 && android.os.Build.MODEL.startsWith("F")) || scannerType == ScannerType.CUSTOMER_DISPLAY || (surfaceView != null && scannerType == ScannerType.FRONT && Build.VERSION.SDK_INT < 24)) {
                deviceLogger.debug("[startScan]  use decode surfaceView:" + surfaceView + ";isOnce:" + isOnce + ";scanListener:" + scanListener);

                if (surfaceView != null && scannerType == ScannerType.FRONT && Build.VERSION.SDK_INT < 24) {
                    cameraID = 0x01;
                }

                queueb = new LinkedBlockingQueue<byte[]>();
                String hardwareConfig = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                if (hardwareConfig != null && hardwareConfig.length() >= 46 && ("02".equals(hardwareConfig.substring(44, 46)) || "03".equals(hardwareConfig.substring(44, 46)))) {
                    previewHeight = 960;
                }

                try {
                    initDecode(context);
                    if (surfaceView != null) {
                        deviceLogger.debug("[startScan]surfaceView!=null");
                        holder = surfaceView.getHolder();
                        holder.addCallback(mcaCallback);
                        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                        deviceLogger.debug("[startScan] holder:" + holder);
                    } else {
                        initCamera();
                    }
                    if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE) {
                        deviceLogger.debug("[startScan]解码开启起止符号");
                        int result = softEngine.scanSet("CODEBAR", "TrsmtStasrtStop", "1");//开启起始符中支付
                        deviceLogger.debug("[startScan]解码开启起止符号结果：result：" + result);
                    } else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.DISENABLE) {
                        deviceLogger.debug("[startScan]解码关闭起止符号");
                        int result = softEngine.scanSet("CODEBAR", "TrsmtStasrtStop", "0");//关闭起始符中支付
                        deviceLogger.debug("[startScan]解码关闭起止符号结果：" + result);
                    }else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE_NORMAL) {
                        deviceLogger.debug("---------[startScan]开启正向码");
                        int result = scanUtil.setNlsScn("QR", "VideoMode", "0");//开启正向码
                        deviceLogger.debug("[startScan]开启正向码：" + result);
                    }else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE_REVERSE) {
                        deviceLogger.debug("---------[startScan]开启反向码");
                        int result = scanUtil.setNlsScn("QR", "VideoMode", "1");//开启反向码
                        deviceLogger.debug("[startScan]开启反向码：" + result);
                    }else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE_NORMAL_REVERSE) {
                        deviceLogger.debug("---------[startScan]开启正反向码");
                        int result = scanUtil.setNlsScn("QR", "VideoMode", "2");//开启正反向码
                        deviceLogger.debug("[startScan]开启正反向码：" + result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    destoryCamera();
                    scanListener.onError(ErrorCode.DECODE, "" + e);
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (decodeObject) {
                            try {
                                decodeObject.wait(timeout * 1000);
                                if (isStop && scanListener != null) {
                                    deviceLogger.debug("[startScan] decode oncancel----");
                                    scanListener.onCancel();
                                }
                                if (!isStop && scanListener != null && queueb.isEmpty()) {
                                    stopDecode();
                                    destoryCamera();
                                    isCameraPreview = false;
                                    deviceLogger.debug("[startScan] decode onTimeout----");
                                    scanListener.onTimeout();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                scanListener.onError(ErrorCode.DECODE, "Exception:" + e);
                            }
                        }
                    }
                }).start();

                return;
            }
            if (cameraNubInHardware == 1 && android.os.Build.MODEL.startsWith("F")) {//人脸设备，只有一个摄像头只能开0
                deviceLogger.debug("[startScan] Build.MODEL:" + android.os.Build.MODEL + ";cameraNubInHardware==1");
                scannerType = ScannerType.BACK;
            }
            boolean initResult = initScanner(context, timeout, surfaceView, scannerType, new ScannerListener() {
                @Override
                public void onTimeout() { }
                @Override
                public void onResponse(String[] scanResults) { }

                @Override
                public void onFinish() { }

                @Override
                public void onError(int errorCode, String message) {
                    scanListener.onError(errorCode,message);
                }

                @Override
                public void onCancel() { }
            });
            if (!initResult) {
                return;
            }
            if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE) {
                deviceLogger.debug("--------[startScan]扫码开启起止符--------------");
                int result = scanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", "1");//开启起始符中支付
                deviceLogger.debug("---------[startScan]扫码开启起止符结果：" + result);
            } else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.DISENABLE) {
                deviceLogger.debug("---------[startScan]扫码关闭起止符");
                int result = scanUtil.setNlsScn("CODEBAR", "TrsmtStasrtStop", "0");//关闭起始符中支付
                deviceLogger.debug("[startScan]扫码关闭起止符结果：" + result);
            } else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE_NORMAL) {
                deviceLogger.debug("---------[startScan]开启正向码");
                int result = scanUtil.setNlsScn("QR", "VideoMode", "0");//开启正向码
                deviceLogger.debug("[startScan]开启正向码：" + result);
            }else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE_REVERSE) {
                deviceLogger.debug("---------[startScan]开启反向码");
                int result = scanUtil.setNlsScn("QR", "VideoMode", "1");//开启反向码
                deviceLogger.debug("[startScan]开启反向码：" + result);
            }else if (scannerExtParams != null && scannerExtParams.getStartStopCapability() == StartStopCapability.ENABLE_NORMAL_REVERSE) {
                deviceLogger.debug("---------[startScan]开启正反向码");
                int result = scanUtil.setNlsScn("QR", "VideoMode", "2");//开启正反向码
                deviceLogger.debug("[startScan]开启正反向码：" + result);
            }
            deviceLogger.debug("[startScan] currentTimeMillis startscan1-" + System.currentTimeMillis());
            if (null != scanRunnable) {
                deviceLogger.error("[startScan] scanner has been opened!");
                listener.onError(ErrorCode.SCANNER_INIT_FAILED, "scanner has been opened!");
                return;
            }

            Thread scanThread = new ScanThread(this, timeout);
            scanThread.start();
        }
    }


    @Override
    public void stopScan() {
        synchronized (this) {
            if ((isSupFaceRecognition() && getCameraNumInHardwareConfig() == 0 && android.os.Build.MODEL.startsWith("F")) || scannerType == ScannerType.CUSTOMER_DISPLAY|| (surfaceView != null && scannerType == ScannerType.FRONT && Build.VERSION.SDK_INT < 24)) {
                deviceLogger.debug("[stopScan]:");
                stopDecode();
                destoryCamera();
                return;
            }
            synchronized (this) {
                canbeRelease = false;
            }
            try {
                deviceLogger.debug("[stopScan] currentTimeMillis stop1-" + System.currentTimeMillis());
                if (null == scanRunnable) {
                    deviceLogger.error("[stopScan] scan is stop or not start!");//不扫码 只开关灯的情况
                    return;
                }
                deviceLogger.debug("scanRunnable: " + scanRunnable);
                if (scanRunnable != null) {
                    scanRunnable.notifyWaiting();
                }
                deviceLogger.debug("[stopScan] currentTimeMillis stop2-" + System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                deviceLogger.debug("[stopScan] currentTimeMillis3-" + System.currentTimeMillis());
                this.release();
                deviceLogger.debug("[stopScan] currentTimeMillis4-" + System.currentTimeMillis());
                deviceLogger.debug("[stopScan] scanUtil relese!-" + scanUtil);
                synchronized (this) {
                    scanUtil = null;
                }
            }
        }
    }

    private final synchronized void release() {
        try {
            if (null == scanUtil) {
                deviceLogger.error("[release] release failed,scan is not init!");
            } else {
                deviceLogger.debug("--------[release] scanUtil.relese start");
                isReleasing = true;
                scanUtil.relese();
                scanUtil = null;
                isReleasing = false;
                deviceLogger.debug("-----[release] scanUtil.relese end");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * init scanner
     *
     * @param context
     * @param surfaceView
     * @param scannerType
     * @param scannerListener
     * @return
     */
    private boolean initScanner(Context context, int timeOut, SurfaceView surfaceView, ScannerType scannerType, ScannerListener scannerListener) {
        try {
            Thread.sleep(100);// update20180612由于realse释放需要70ms左右，同时为了提高扫码速度，因此为了避免切换前后置摄像头释放来不及导致的扫码崩溃问题，因此延时添加在此处。
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        try {
//            if(Build.VERSION.SDK_INT >= 24){
//                //scan_back_preview_mode 系统属性设置0，会使得A10以上 机器 scanutil 开摄像头预览比较亮。目前就银商临时打包独立扫码aar包输出过
//                Log.i("initScanner","scan_back_preview_mode");
//                Settings.System.putString(context.getContentResolver(), "scan_back_preview_mode","0");
//            }
//        }catch (Exception | Error r){
//            r.printStackTrace();
//        }
        deviceLogger.debug("[initScanner],surfaceView:" + surfaceView + ";ScannerType:" + scannerType);
        if (android.os.Build.MODEL.equals("IM81")) {
            this.scannerType = scannerType;
            int count = Camera.getNumberOfCameras();
            if (count == 1) {// 当只有一个摄像头的情况下，默认前置不支持，后置支持（左下角的扫码头为前置，屏幕中间的摄像头为后置）
                if (scannerType == ScannerType.FRONT) {
                    scannerListener.onError(ErrorCode.SCANNER_UNSUPPORT, "There's only one camera in IM81 device,not support front scanner ");
                    return false;
                } else {
                    deviceLogger.debug("------scanUtil = new ScanUtil(context, surfaceView, SCAN_BACK, false, timeOut * 1000, ZXING);----");
                    scanUtil = new ScanUtil(context, surfaceView, SCAN_BACK, false, timeOut * 1000, ZXING);
                }
            } else if (count >= 2) {// 当有两个摄像头的情况下，屏幕前的摄像头为后置，底下的为前置
                if (scannerType == ScannerType.FRONT) {
                    deviceLogger.debug("------scanUtil = new ScanUtil(context, surfaceView, SCAN_FRONT, false, timeOut * 1000, ZXING);----");
                    scanUtil = new ScanUtil(context, surfaceView, SCAN_FRONT, false, timeOut * 1000, ZXING);
                } else if (scannerType == ScannerType.BACK) {
                    deviceLogger.debug("---------scanUtil = new ScanUtil(context, surfaceView, SCAN_BACK, false, timeOut * 1000, ZXING);");
                    scanUtil = new ScanUtil(context, surfaceView, SCAN_BACK, false, timeOut * 1000, ZXING);
                } else {
                    scannerListener.onError(ErrorCode.SCANNER_UNSUPPORT, "not support this scanner type");
                    return false;
                }
            }
        } else if (android.os.Build.MODEL.equals("N550")) { // 不论传前置后置
            // 都直接弹出摄像头，zxing扫码后期不维护。
            deviceLogger.debug("----scanUtil = new ScanUtil(context, surfaceView, SCAN_BACK, false, timeOut * 1000, NLS)--");
            scanUtil = new ScanUtil(context, surfaceView, SCAN_BACK, false, timeOut * 1000, NLS);

        } else if (android.os.Build.MODEL.equals("N850") && Camera.getNumberOfCameras() == 1) {//只有一个摄像头，都打开后置
            deviceLogger.debug("--------- scanUtil = new ScanUtil(context, surfaceView, SCAN_BACK, false, timeOut * 1000, NLS);");
            scanUtil = new ScanUtil(context, surfaceView, SCAN_BACK, false, timeOut * 1000, NLS);
        }  else if ((android.os.Build.MODEL.equals("N750") || NlBuild.VERSION.MODEL.equals("N750P") || android.os.Build.MODEL.equals("N950S-C")) && Camera.getNumberOfCameras() == 1) {//只有一个摄像头，都打开前置
            deviceLogger.debug("--------- scanUtil = new ScanUtil(context, surfaceView, SCAN_FRONT, false, timeOut * 1000, 0); N750P");
            scanUtil = new ScanUtil(context, surfaceView, 0, false, timeOut * 1000, NLS);
        } else if (android.os.Build.MODEL.equals("P300")) {//只有一个摄像头，都打开前置
            deviceLogger.debug("--------- scanUtil = new ScanUtil(context, surfaceView, 0, false, timeOut * 1000, 0); P300");
            scanUtil = new ScanUtil(context, surfaceView, 0, false, timeOut * 1000, NLS);
        }else if (android.os.Build.MODEL.equals("N950S")) {
            int count = getCameraNumInHardwareConfig();
            deviceLogger.debug("--------- scanUtil = new ScanUtil(); N950S count="+count);
            if(count == 2){
                if(scannerType == ScannerType.FRONT){
                    scanUtil = new ScanUtil(context, surfaceView, SCAN_FRONT, false, timeOut * 1000, NLS);
                }else {
                    scanUtil = new ScanUtil(context, surfaceView, SCAN_BACK, false, timeOut * 1000, NLS);
                }
            }else {
                String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                if (config != null && config.length() >= 10 && config.substring(8, 10).equals("13")) {
                    scanUtil = new ScanUtil(context, surfaceView, SCAN_BACK, false, timeOut * 1000, NLS);
                }
            }
        }else {
            this.scannerType = scannerType;
            boolean isSupScanCode = isSupScanCode(scannerType);
            deviceLogger.debug("isSupScanCode:"+isSupScanCode);
            if (isSupScanCode) {
                if (scannerType == ScannerType.FRONT && isSupNlsScanCode() && surfaceView != null) {//实现安卓版本7.0的设备，前置可以预览
                    deviceLogger.debug("--------scanUtil = new ScanUtil(context, surfaceView, SCAN_FRONT, false, timeOut * 1000, NLS);");
                    scanUtil = new ScanUtil(context, surfaceView, SCAN_FRONT, false, timeOut * 1000, NLS);
                } else if (scannerType == ScannerType.FRONT) {
                    deviceLogger.debug("------scanUtil = new ScanUtil(context);");
                    scanUtil = new ScanUtil(context);
                    int rslt = scanUtil.init(MODE_ONCE, timeOut * 1000, FOCUS_READING, false);
                    if (rslt == 0) {
                        scannerListener.onError(ErrorCode.SCANNER_INIT_FAILED, "return 0!scanner init failed");
                        return false;
                    }
                } else if (scannerType == ScannerType.BACK && isSupNlsScanCode()) {
                    deviceLogger.debug("---------scanUtil = new ScanUtil(context, surfaceView, SCAN_BACK, false, timeOut * 1000, NLS)");
                    if(isSupHardScan(scannerType)){ //TODO 兼容N950U 后置硬扫
                        scanUtil = new ScanUtil(context, surfaceView, HARD_SCANNER, false, timeOut * 1000, NLS);
                    }else{
                        scanUtil = new ScanUtil(context, surfaceView, SCAN_BACK, false, timeOut * 1000, NLS);
                    }
                } else if (scannerType == ScannerType.BACK && !isSupNlsScanCode()) {
                    deviceLogger.debug("-------scanUtil = new ScanUtil(context, surfaceView, SCAN_BACK, false, timeOut * 1000, ZXING)");
                    scanUtil = new ScanUtil(context, surfaceView, SCAN_BACK, false, timeOut * 1000, ZXING);

                } else {
                    deviceLogger.error("---not support this scanner type--");
                    scannerListener.onError(ErrorCode.SCANNER_UNSUPPORT, "not support this scanner type");
                    return false;
                }
            } else {
                scannerListener.onError(ErrorCode.SCANNER_UNSUPPORT, "not support this scanner type");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isSupScanCode(ScannerType scannerType) {
        deviceLogger.debug("isSupScanCode scannerType="+scannerType);
        if (android.os.Build.MODEL.equals("NLS-MT90")) {
            return true;
        }

        switch (scannerType) {
            case FRONT:
                if (android.os.Build.MODEL.equals("IM81")) {
                    int count = Camera.getNumberOfCameras();
                    if (count >= 1) {
                        return true;
                    }
                    return false;
                }

                if (Build.MODEL.equals("CPOS X1") || android.os.Build.MODEL.equals("CPOS X3") || android.os.Build.MODEL.equals("CPOS X5") || android.os.Build.MODEL.equals("STAR A-6300")) {
                    int count = Camera.getNumberOfCameras();
                    if (count == 3) {
                        return true;
                    }
                }

                if (android.os.Build.MODEL.equals("N550")) {//550虽然只有一个摄像头 但是都当作前后置支持，实际定位为前置 但是有些客户当后置使用。
                    return true;
                }

                if (isSupFaceRecognition() && getCameraNumInHardwareConfig() >= 0) {
                    return true;
                }
                if(!getHasSecModule() && (Build.MODEL.startsWith("CPOS") ||  android.os.Build.MODEL.equals("STAR A-6300"))){
                    File cameraFile = new File("/sys/class/front_camera");
                    boolean isExist = cameraFile.exists();
                    deviceLogger.debug("/sys/class/front_camera isExist:"+isExist);
                    if(isExist){
                        return true;
                    }
                }

                if (isSupportFunction(scannerType)) {
                    String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (config != null && config.length() >= 10) {
                        String cameraType = config.substring(8, 10);
                        if ("01".equals(cameraType) || "03".equals(cameraType) || "04".equals(cameraType) || "11".equals(cameraType) || "12".equals(cameraType)
                                || "20".equals(cameraType)||"14".equals(cameraType)||"15".equals(cameraType)) {
                            return true;
                        }
                        return false;
                    }
                } else {
                    String version = NlBuild.VERSION.NL_FIRMWARE;
                    version = version.replaceAll("V", "").replace("T", "");
                    if ("SA1".equals(NlBuild.VERSION.NL_HARDWARE_ID)) { // 3G版本1.1.12之前不支持硬件配置码，但是支持硬扫码
                        return "1.1.12".compareToIgnoreCase(version) > 0;
                    }
                }
                break;
            case BACK:
                if (android.os.Build.MODEL.equals("IM81")) {
                    int count = Camera.getNumberOfCameras();
                    if (count >= 2) {
                        return true;
                    }
                    return false;
                }
                if(!getHasSecModule() && (Build.MODEL.startsWith("CPOS") ||  android.os.Build.MODEL.equals("STAR A-6300")) && isSupScanCode(ScannerType.CUSTOMER_DISPLAY) && scannerType == ScannerType.BACK){//没安全模块的CPOS开后置，有副屏的情况，默认开副屏
                    File cameraFile = new File("/sys/class/back_camera");
                    boolean isExist = cameraFile.exists();
                    deviceLogger.debug("/sys/class/back_camera isExist:"+isExist);
                    if(isExist){
                        return true;
                    }
                }
                if (Build.MODEL.equals("CPOS X1") || android.os.Build.MODEL.equals("CPOS X3") || android.os.Build.MODEL.equals("CPOS X5") || android.os.Build.MODEL.equals("STAR A-6300")) {
                    String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (config != null && config.length() >= 10) {
                        String cameraType = config.substring(8, 10);
                        if ("20".equals(cameraType)) {  //支付扫码头
                            return true;
                        }
                        return false;
                    }


                }
                if (android.os.Build.MODEL.equals("N550")) {
                    return true;
                }

                if (android.os.Build.MODEL.equals("N850")) {
                    return true;
                }

                if (isSupFaceRecognition() && getCameraNumInHardwareConfig() == 0) {
                    return false;
                }
                if (getCameraNumInHardwareConfig() >= 1) {
                    if(!android.os.Build.MODEL.equals("P300")){
                        return true;
                    }
                }
                if (isSupportFunction(scannerType)) {

                    String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (config != null && config.length() >= 10) {
                        String cameraType = config.substring(8, 10);
                        if ("10".equals(cameraType) || "11".equals(cameraType) || "12".equals(cameraType) || "13".equals(cameraType)
                                || "14".equals(cameraType) || "20".equals(cameraType)) {
                            return true;
                        }
                        return false;
                    }
                }
                break;
            case CUSTOMER_DISPLAY:
                try {
                    if(Build.MODEL.startsWith("CPOS")){
                        File cameraFile = new File("/sys/class/back_camera");
                        boolean isExist = cameraFile.exists();
                        deviceLogger.debug("/sys/class/back_camera isExist:"+isExist);
                        if(isExist){
                            return true;
                        }
                    }
                }catch (Error r){
                    r.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }

    @Override
    public boolean operateLight(ScanLightType type, LightOperType lightOperType) {
        deviceLogger.debug("[operLight]-type:" + type + ",LightOperType:" + lightOperType);
        switch (type) {
            case LED_LIGHT: {
                switch (lightOperType) {
                    case CLOSE:
                        try {
                            setFileValue("/sys/class/scan_ctrl/wled", "0");//设置N700 扫码照明灯
                        } catch (Exception e) {
                            deviceLogger.debug("exception:"+e.getMessage());
                            return false;
                        }
                        break;
                    case OPEN:
                        try {
                            setFileValue("/sys/class/scan_ctrl/wled", "1");//设置N700 扫码照明灯
                        } catch (Exception e) {
                            deviceLogger.debug("exception:"+e.getMessage());
                            return false;
                        }
                        break;
                    default:
                        break;
                }
            }
            break;
            case RED_LIGHT: {
                switch (lightOperType) {
                    case CLOSE:
                        try {
                            setFileValue("/sys/class/scan_ctrl/led", "0");// 设置N700 扫码红外灯
                        } catch (Exception e) {
                            deviceLogger.debug("exception:"+e.getMessage());
                            return false;
                        }
                        break;
                    case OPEN:
                        try {
                            setFileValue("/sys/class/scan_ctrl/led", "1");// 设置N700 扫码红外灯
                        } catch (Exception e) {
                            deviceLogger.debug("exception:"+e.getMessage());
                            return false;
                        }
                        break;
                    default:
                        break;
                }
            }
            break;
            case FLASH_LIGHT:
                if (scanUtil == null || isReleasing) {
                    deviceLogger.error("[operLight] scan is not init! or isReleasing,scanUtil:"+scanUtil+"; isReleasing:"+isReleasing);
                    return false;
                }
                switch (lightOperType) {
                    case CLOSE:
                        try {
                            deviceLogger.debug("--- scanUtil.closeLight()------");
                            scanUtil.closeLight();
                        } catch (Exception e) {
                            deviceLogger.debug("exception:"+e.getMessage());
                            return false;
                        }
                        break;
                    case OPEN:
                        try {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(500);
                                        if (scanUtil == null || isReleasing) {
                                            deviceLogger.error("[operLight] scan is not init! or isReleasing,scanUtil:"+scanUtil+"; isReleasing:"+isReleasing);
                                            return;
                                        }
                                        deviceLogger.debug("--scanUtil.openLight();--");
                                        scanUtil.openLight();   //由于开灯要在摄像头完全打开后 所以加了线程和延时。
                                    } catch (Exception e) {
                                        deviceLogger.debug("exception:"+e.getMessage());
                                    }
                                }
                            }).start();
                        } catch (Exception e) {
                            deviceLogger.debug("exception:"+e.getMessage());
                            return false;
                        }
                        break;
                    default:
                        break;
                }

                break;
            default:
                break;
        }
        return true;
    }


    /**
     * 是否支持硬件配置码
     *
     * @param scannerType
     * @return
     */
    private boolean isSupportFunction(ScannerType scannerType) {
        String version = NlBuild.VERSION.NL_FIRMWARE;
        version = version.replaceAll("V", "").replace("T", "");
        if ("SA1".equals(NlBuild.VERSION.NL_HARDWARE_ID)) {// 硬件识别码
            // 3G版本1.1.12之前不支持硬件配置码，但是支持硬扫码
            if (scannerType == ScannerType.BACK) { // 3g设备不支持后置zxing扫码以及nls扫码
                return false;
            } else {
                return "1.1.12".compareToIgnoreCase(version) <= 0;
            }
        } else if ("SA2".equals(NlBuild.VERSION.NL_HARDWARE_ID)) {
            if (scannerType == ScannerType.BACK) {
                return "2.0.15".compareToIgnoreCase(version) <= 0; // 从2.0.15版本之后，后置才支持zxing以及nls扫码
            }
        }
        return true;
    }

    /**
     * 判断后置是否支持软解码
     *
     * @return
     */
    private boolean isSupNlsScanCode() {
        if (isSupportFunction(ScannerType.BACK)) {
            String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;// 硬件配置码
            if (config != null && config.length() >= 10) {
                String cameraType = config.substring(8, 10);
                if ("03".equals(cameraType) || "12".equals(cameraType) || "13".equals(cameraType)|| "14".equals(cameraType)|| "15".equals(cameraType)
                        || "20".equals(cameraType)) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    private boolean isSupHardScan(ScannerType scannerType){
        if (isSupportFunction(ScannerType.BACK)) {
            String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;// 硬件配置码
            if (config != null && config.length() >= 10) {
                String cameraType = config.substring(8, 10);
                if(scannerType==ScannerType.BACK){
                    if ( "14".equals(cameraType)||"15".equals(cameraType)) {
                        return true;
                    }
                }else if(scannerType==ScannerType.FRONT){
                    if ("01".equals(cameraType) || "11".equals(cameraType)) {
                        return true;
                    }
                }

                return false;
            }
        }
        return false;
    }

    /**
     * 是否支持人脸识别
     *
     * @return
     */
    private boolean isSupFaceRecognition() {
        String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;// 硬件配置码
        deviceLogger.debug("[isSupFaceRecognition] config:" + config);
        if (config != null && config.length() >= 46) {
            String faceRecognitionParam = config.substring(44, 46);
            deviceLogger.debug("[isSupFaceRecognition] faceRecognitionParam:" + faceRecognitionParam);
            if ("01".equals(faceRecognitionParam) || "02".equals(faceRecognitionParam) || "03".equals(faceRecognitionParam)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取硬件配置码里面照相机个数
     *
     * @return
     */
    private int getCameraNumInHardwareConfig() {
        String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;// 硬件配置码
        deviceLogger.debug("[getCameraNumInHardwareConfig] config:" + config);
        if (config != null && config.length() >= 26) {
            String camereParam = config.substring(24, 26);
            deviceLogger.debug("[getCameraNumInHardwareConfig] camereParam:" + camereParam);
            if ("FF".equals(camereParam)) {
                return 0;
            } else {
                int numbers = Integer.parseInt(camereParam);
                return numbers;
            }
        }
        return 0;
    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean initDecode(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {//A7 以上平台用SoftEngine解码
            deviceLogger.debug("[initDecode] A7 SoftEngine start init decode");
            String[] sf = NlBuild.VERSION.NL_FIRMWARE.split("\\.");
            boolean isOldF7 = false;
            if (android.os.Build.MODEL.equalsIgnoreCase("FPOS F7") && null != sf && sf.length >= 3) {
                StringBuilder version = new StringBuilder();
                version = version.append(sf[0]).append(sf[1]).append(sf[2]);
                String versionStr = version.substring(1);
                isOldF7 = (versionStr.compareToIgnoreCase("1007") < 0);
                deviceLogger.debug("[initDecode]isOldF7:" + isOldF7);
            }

            if (isOldF7) {//F7 V1.0.07以下固件不支持带参数的getInstance
                softEngine = SoftEngine.getInstance();
            } else {
                softEngine = SoftEngine.getInstance(context);//F7 V1.0.07 以上固件版本，以及  A10设备，要带context参数
            }
            //1.
            softEngine.setScanningCallback(new SoftEngine.ScanningCallback() {

                public void onScanningCallback(int eventCode, int param1,
                                               byte[] param2, int length) {
                    String strResult;
                    try {
                        deviceLogger.debug("[initDecode] eventCode:" + eventCode + "；encode type：" + param1 + "length:" + length);
                        if(decodeListener==null){
                            deviceLogger.error("[initDecode] decodeListener==null" );
                            return;
                        }
                        strResult = new String(param2, "UTF-8");
                        deviceLogger.debug("[initDecode] decode result:" + strResult);
                        if (eventCode == 1 && decodeListener != null) {
                            decodeListener.onResult(param2);
                        } else if (eventCode != 1) {
                            decodeListener.onError(eventCode, "YUV decode failed");
                        } else {
                            throw new DeviceRTException(ErrorCode.DECODE, "decodeListener is null");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
            deviceLogger.debug("[initDecode]SoftEngine init decode end");
        } else {
            deviceLogger.debug("[initDecode]ScanUtil init decode ");
            if (surfaceView != null && scannerType == ScannerType.FRONT) {
                uart3Manager = (NLUART3Manager) context.getSystemService(NlContext.UART3_SERVICE);
                return true;
            }
            deviceLogger.debug("--scanUtil = new ScanUtil(context);-----");
            scanUtil = new ScanUtil(context);
            deviceLogger.debug("--scanUtil.initDecode-----");
            int rs = scanUtil.initDecode(new ScanUtil.ResultCallBack() {
                @Override
                public void onResult(int eventCode, int codeType, byte[] data1,
                                     byte[] data2, int length) {
                    deviceLogger.debug("[initDecode] eventCode=" + eventCode + ",codeType=" + codeType + ",data=" + new String(data1) + ",data2 =" + data2 + ",length="
                            + length);
                    if(decodeListener==null){
                        deviceLogger.error("[initDecode] decodeListener==null" );
                        return;
                    }
                    try {

                        if (decodeListener != null) {
                            if (eventCode == 1) {
                                decodeListener.onResult(data1);
                            } else {
                                decodeListener.onError(ErrorCode.DECODE, "decode failed eventcode=" + eventCode);
                            }
                        } else {
                            throw new DeviceRTException(ErrorCode.DECODE, "decodeListener is null");

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new DeviceRTException(ErrorCode.DECODE, "decode failed:" + e.getMessage());

                    }


                }
            });
            deviceLogger.debug("[initDecode] rs:" + rs);

            if (rs != 0) {
                scanUtil = null;
                return false;
            }
        }

        return true;
    }

    @Override
    public void startYUVDecode(byte[] yuv, int width, int height, DecodeListener decodeListener) {
        this.decodeListener = decodeListener;
        deviceLogger.debug("[startYUVDecode] startDecode width:" + width + ";height:" + height);
        if (Build.VERSION.SDK_INT >= 24) {//A7 平台用softEnginer解码
            if (softEngine == null) {
                decodeListener.onError(ErrorCode.DECODE, "please invoke initDecode method first ");
                return;
            }
            boolean isSucess = softEngine.startDecode(yuv, width, height);
            deviceLogger.debug("[startYUVDecode] startDecode result:" + isSucess);
            if (!isSucess) {
                deviceLogger.error("[startYUVDecode] startDecode failed");
                decodeListener.onError(ErrorCode.DECODE, "startDecode failed");
            }
        } else {

            if (surfaceView != null && scannerType == ScannerType.FRONT) {
                uart3Manager.StartDecode(yuv, width, height);//传输数据到解码库
                String result = uart3Manager.ReadResult();//读取结果
                deviceLogger.debug("[uart3Manager] startDecode result:" + result );
                if (result != null) {
                    uart3Manager.StopDecode();
                    decodeListener.onResult(result.getBytes());
                }
            } else {
                if (scanUtil != null) {
                    deviceLogger.debug("-----scanUtil.startYUVDecode----------");
                    scanUtil.startYUVDecode(yuv, width, height);
                } else {
                    throw new DeviceRTException(ErrorCode.DECODE, "please invoke initDecode method first ");
                }
            }
        }
    }

    @Override
    public void stopDecode() {
        try {
            deviceLogger.debug("[stopDecode]");
            if (Build.VERSION.SDK_INT >= 24 && softEngine != null) {//A7 平台用softEnginer解码
                softEngine.stopDecode();
            } else {
                if (surfaceView != null && scannerType == ScannerType.FRONT) {
                    if(uart3Manager!=null){
                        uart3Manager.StopDecode();
                    }
                } else {
                    if (scanUtil != null) {
                        deviceLogger.debug("----  scanUtil.stopDecode()-------");
                        scanUtil.stopDecode();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.decodeListener = null;
        }
    }

    @Override
    public boolean setIlluminateLight(boolean isOn) {
        if (NlBuild.VERSION.MODEL.equalsIgnoreCase("N550") || NlBuild.VERSION.MODEL.equalsIgnoreCase("N850")) {
            String scanLedPath = "/sys/class/scan_ctrl/scan_status_led";
            boolean isExists = FileUtils.isFileExists(scanLedPath);
            if (isExists) {
                if (isOn) {
                    return FileIOUtils.writeFileFromString(scanLedPath, "1");
                } else {
                    return FileIOUtils.writeFileFromString(scanLedPath, "0");
                }
            }
            return false;
        } else {
            throw new DeviceRTException(ErrorCode.DEVICE_INVOKE_FAILED, "Device not supported");
        }
    }

    private void setFileValue(String FileName, String value) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FileName));
        writer.write(value);
        writer.flush();
        writer.close();
    }

    /**
     * get Scan module version
     *
     * @return
     */
    public String getScanVersion() {
        if (scanVersion == null)
            initSDKVersion();
        return scanVersion;
    }

    private void initSDKVersion() {
        initScanProperties();
        try {
            if (scanProperties == null)
                return;
            scanVersion = scanProperties.getProperty("scan.version");
        } catch (Exception e) {
            deviceLogger.error("[initSDKVersion] failed to init sdk version!", e);
        }
    }

    /**
     * init Scan module Properties
     */
    private void initScanProperties() {
        if (scanProperties == null) {
            Properties p = new Properties();
            URL url = getClass().getClassLoader().getResource("scan.properties");
            if (url == null)
                return;
            else {
                try {
                    p.load(url.openStream());
                    scanProperties = p;
                } catch (Exception e) {
                    deviceLogger.error("[initScanProperties] load scanProperties failed!", e);
                }
            }
        }
    }

    public ScannerListener getScannerlistener() {
        return scannerlistener;
    }

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    /**************************************/
    /*********** 摄像头处理模块 ************/
    /*************************************/
//    class MySurfaceHolderCallBack implements SurfaceHolder.Callback{
//
//    }
    private SurfaceHolder.Callback mcaCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            deviceLogger.debug("[mcaCallback] surfaceDestroyed destoryCamera,:isFirstSurfaceCreate:");
            // destoryCamera();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            deviceLogger.debug("[mcaCallback] surfaceCreated initCamera,surfaceView:" + surfaceView + ";scannerType:" + scannerType);
            initCamera();

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            deviceLogger.debug("[mcaCallback] surfaceChanged setPreviewDisplay");

            if (mCamera != null && surfaceView != null) {
                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    /**
     * 关闭照相机
     */
    public void destoryCamera() {
        try {
            deviceLogger.debug("[destoryCamera] MEScanner destoryCamera mCamera:" + mCamera + ";holder:" + holder + ";mcaCallback:" + mcaCallback + ";isCameraPreview:" + isCameraPreview);
            if (mCamera != null) {
                if (isCameraPreview) {
                    deviceLogger.debug("[destoryCamera] stopPreview   start");
                    mCamera.stopPreview();
                    deviceLogger.debug("[destoryCamera] stopPreview   end");
                    isCameraPreview = false;
                }

                mCamera.setPreviewCallback(null);
                mCamera.setPreviewCallbackWithBuffer(null);
                mCamera.release();
                mCamera = null;
                deviceLogger.debug("[destoryCamera] destoryCamera end");
            }
            isStop = true;
            synchronized (decodeObject) {
                decodeObject.notify();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Camera.PreviewCallback MyPreviewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            deviceLogger.debug("[MyPreviewCallback]  data's length=" + data.length + ",width="
                    + previewWidth + ",height=" + previewHeight);
            try {
                startYUVDecode(data, previewWidth, previewHeight, new DecodeListener() {
                    @Override
                    public void onResult(byte[] decodeResult) {
                        try {
                            deviceLogger.debug("[MyPreviewCallback] onResult,decodeResult:" + (decodeResult == null ? null : new String(decodeResult)));
                            if(scanResultIsByte){
                                if (mCamera != null && decodeResult != null && isOnce) {
                                    queueb.put(decodeResult);
                                    ArrayList<byte[]> containers = new ArrayList<byte[]>();
                                    int size = queueb.drainTo(containers);
                                    scanListener.onResponse(containers.toArray(new byte[size][]));
                                    synchronized (decodeObject) {
                                        decodeObject.notify();
                                    }
                                    stopDecode();
                                    destoryCamera();
                                    isCameraPreview = false;
                                    surfaceView = null;
                                    scanListener.onFinish();
                                    scanListener = null;
                                } else if (decodeResult != null && !isOnce) {
                                    queueb.put(decodeResult);
                                    Thread.sleep(10);
                                    ArrayList<byte[]> containers = new ArrayList<byte[]>();
                                    int size = queueb.drainTo(containers);
                                    scanListener.onResponse(containers.toArray(new byte[size][]));
                                    stopDecode();
                                }
                            }else{
                                if (mCamera != null && decodeResult != null && isOnce) {
                                    queue.put(new String(decodeResult));
                                    getScannerlistener().onResponse(new String[]{new String(decodeResult)});
                                    synchronized (decodeObject) {
                                        decodeObject.notify();
                                    }
                                    stopDecode();
                                    destoryCamera();
                                    isCameraPreview = false;
                                    surfaceView = null;
                                    getScannerlistener().onFinish();
                                    scannerlistener = null;
                                } else if (decodeResult != null && !isOnce) {
                                    queue.put(new String(decodeResult));
                                    Thread.sleep(10);
                                    getScannerlistener().onResponse(new String[]{new String(decodeResult)});
                                    stopDecode();
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            stopDecode();
                            destoryCamera();
                            isCameraPreview = false;
                            surfaceView = null;
                            if(scanResultIsByte){
                                scanListener.onError(ErrorCode.DECODE, e + "");
                                scanListener=null;
                            }else{
                                getScannerlistener().onError(ErrorCode.DECODE, e + "");
                                scannerlistener = null;
                            }

                        }

                    }

                    @Override
                    public void onError(int errorCode, String errMsg) {
                        surfaceView = null;

                        if(scanResultIsByte){
                            scanListener.onError(ErrorCode.DECODE, errMsg);
                        }else {
                            getScannerlistener().onError(ErrorCode.DECODE, errMsg);
                        }
                    }
                });
                requestPreviewFrame();
                isCameraPreview = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 开启照相机
     *
     * @param
     */
    private void initCamera() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                    deviceLogger.debug("[initCamera] mCamera:" + mCamera + ";surfaceView:" + surfaceView);//防止前后切换，先回调onsurfaceCreated,在调用startscan
                    if (mCamera != null) {
                        return;
                    }
                    if (surfaceView != null) {
                        mCamera = Camera.open(cameraID);
                        if (scannerType == ScannerType.CUSTOMER_DISPLAY) {
                            Camera.Parameters parameters = mCamera.getParameters();
                            parameters.setPreviewSize(previewWidth, previewHeight);
                            mCamera.setParameters(parameters);
                        } else {
                            setPreviewSize(mCamera);
                        }
                        deviceLogger.debug("[initCamera]  width = " + mCamera.getParameters().getPreviewSize().width + ",height = " + mCamera.getParameters().getPreviewSize().height);
                        try {
                            mCamera.setPreviewDisplay(holder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mCamera.setPreviewCallback(MyPreviewCallback);
                        mCamera.startPreview();
                        isCameraPreview = true;
                        deviceLogger.debug("[initCamera] start preview");
                    } else {
                        deviceLogger.debug("[initCamera] open camera with no surfaceView---------");
                        mCamera = Camera.open(cameraID);// 支付摄像头CameraID  = 2....如果三个摄像头都有的话。0是副屏。。1是主屏。 2是扫码
                        mCamera.setPreviewTexture(surfaceTexture);

                        setDesiredCameraParameters(mCamera, cameraID);
                        mCamera.setPreviewCallbackWithBuffer(MyPreviewCallback);
                        deviceLogger.debug("[initCamera] mCamera.startPreview()----");
                        mCamera.startPreview();
                        isCameraPreview = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    destoryCamera();
                    if (getScannerlistener() != null) {
                        getScannerlistener().onError(ErrorCode.DECODE, e + "");
                    }
                }
            }
        }).start();

    }

    void setDesiredCameraParameters(Camera camera, int cameraId) {
        if (scannerType == ScannerType.CUSTOMER_DISPLAY) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(previewWidth, previewHeight);
            mCamera.setParameters(parameters);
        } else {//F7,F10的usb 照相机，需要按这些参数设置
            CameraManagerUtil cameraManagerUtil = new CameraManagerUtil(context, mCamera, null);
            cameraManagerUtil.setCameraParams();
        }
        deviceLogger.debug("[setDesiredCameraParameters] previewWidth:" + camera.getParameters().getPreviewSize().width + ";previewView height:" + camera.getParameters().getPreviewSize().height);
        previewWidth = camera.getParameters().getPreviewSize().width;
        previewHeight = camera.getParameters().getPreviewSize().height;
        cameraResolution = new Point(camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height);
        deviceLogger.debug("[setDesiredCameraParameters] cameraResolution:" + cameraResolution.x + ";" + cameraResolution.y);
        mBufferSize = cameraResolution.x * cameraResolution.y;
        mBufferSize = mBufferSize * 3 / 2;
        deviceLogger.debug("[setDesiredCameraParameters] mBufferSize" + mBufferSize);
        mBuffer = new byte[mBufferSize];
        camera.addCallbackBuffer(mBuffer);
    }

    private void requestPreviewFrame() {
        deviceLogger.debug("[setDesiredCameraParameters] requestPreviewFrame");
        if (mCamera != null) {
            mCamera.setOneShotPreviewCallback(MyPreviewCallback);
        }
    }


    public boolean checkAppInstalled(Context context, String pkgName) {
        if (pkgName == null || TextUtils.isEmpty(pkgName)) {
            deviceLogger.error("pkgName == null");
            return false;
        }
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        deviceLogger.debug("---FaceService is installed: " + (packageInfo != null ? "true" : "false"));
        return packageInfo != null;
    }


    /**
     * 设置预览宽高
     * 固件最大支持1280*960
     *
     * @param mCamera
     */
    private void setPreviewSize(Camera mCamera) {
        try {
            deviceLogger.debug("[setPreviewSize]");
            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> preSize = parameters.getSupportedPreviewSizes();
            for (Camera.Size sizi : preSize) {
                deviceLogger.debug("setSupportPreviewSize: " + sizi.width + sizi.height);
            }
            if (preSize != null && preSize.size() > 0) {
                for (Camera.Size sizi : preSize) {
                    if (sizi.width <= 1280 && sizi.height <= 960) {
                        previewWidth = sizi.width;
                        previewHeight = sizi.height;
                        deviceLogger.debug("[setPreviewSize] previewWidth:" + previewWidth + ";previewHeight:" + previewHeight);
                        break;
                    }
                }

            }
            parameters.setPreviewSize(previewWidth, previewHeight);
            deviceLogger.debug("previewWidth:" + previewWidth + "; previewHeight:" + previewHeight);
            if ((android.os.Build.MODEL.equalsIgnoreCase("FPOS F10") && surfaceView != null) || (surfaceView != null && scannerType == ScannerType.FRONT && Build.VERSION.SDK_INT < 24)) {
                deviceLogger.debug("setDisplayOrientation");
                parameters.set("rotation", 270);
                mCamera.setDisplayOrientation(270);
            }
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e1) {
            e1.printStackTrace();
        }
    }

    private String getSysProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method method = c.getMethod("get", String.class);
            value = (String) (method.invoke(c, key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    public boolean getHasSecModule(){
        boolean hasSecModule = true;
        if(getSysProperty("persist.sys.HasSecModule","yes").equals("no")){
            hasSecModule = false;
        }
        deviceLogger.debug(">>>hasSecModule="+hasSecModule);
        return hasSecModule;
    }

}
