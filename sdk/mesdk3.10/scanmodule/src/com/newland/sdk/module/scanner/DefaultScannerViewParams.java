package com.newland.sdk.module.scanner;


/**
 * @author youjf
 * @description
 * @date 2020/6/1
 * @since
 */
public class DefaultScannerViewParams {
    private static ScannerModule scannerModulel;
    private static int timeOut;
    private static ScannerType scannerType;
    private static boolean enableSurfaceView;
    private static ScannerExtParams scannerExtParams;
    private static boolean enableSound = true;
    private static ScannerListener scannerListener;

    public static ScannerModule getScannerModulel() {
        return scannerModulel;
    }

    public static void setScannerModulel(ScannerModule scannerModulel) {
        DefaultScannerViewParams.scannerModulel = scannerModulel;
    }



    public static int getTimeOut() {
        return timeOut;
    }

    public static void setTimeOut(int timeOut) {
        DefaultScannerViewParams.timeOut = timeOut;
    }

    public static ScannerType getScannerType() {
        return scannerType;
    }

    public static void setScannerType(ScannerType scannerType) {
        DefaultScannerViewParams.scannerType = scannerType;
    }

    public static boolean isEnableSurfaceView() {
        return enableSurfaceView;
    }

    public static void setEnableSurfaceView(boolean enableSurfaceView) {
        DefaultScannerViewParams.enableSurfaceView = enableSurfaceView;
    }

    public static ScannerExtParams getScannerExtParams() {
        return scannerExtParams;
    }

    public static void setScannerExtParams(ScannerExtParams scannerExtParams) {
        DefaultScannerViewParams.scannerExtParams = scannerExtParams;
    }

    public static boolean isEnableSound() {
        return enableSound;
    }

    public static void setEnableSound(boolean enableSound) {
        DefaultScannerViewParams.enableSound = enableSound;
    }

    public static ScannerListener getScannerListener() {
        return scannerListener;
    }

    public static void setScannerListener(ScannerListener scannerListener) {
        DefaultScannerViewParams.scannerListener = scannerListener;
    }
}
