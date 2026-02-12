package com.newland.nsdk.core.internal.barcodescanner;

import android.content.Context;
import android.newland.scan.ScanUtil;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.barcodedecoder.DecodingByteCallback;
import com.newland.nsdk.core.api.internal.barcodedecoder.DecodingCallback;
import com.newland.nsdk.core.api.internal.barcodedecoder.IDecodingCallback;
import com.newland.nsdk.core.api.internal.barcodescanner.BarcodeScanner;
import com.newland.nsdk.core.api.internal.barcodescanner.ScanCodeOption;
import com.newland.nsdk.core.api.internal.barcodescanner.ScanParameters;
import com.newland.nsdk.core.api.internal.barcodescanner.ScanSettings;
import com.newland.nsdk.core.api.internal.barcodescanner.ScannerType;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceInfo;
import com.newland.nsdk.core.api.internal.devicemanager.ScannerConfig;
import com.newland.nsdk.core.api.internal.setting.SettingsManager;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class BarcodeScannerImpl implements BarcodeScanner {
    private static final String TAG = "BarcodeScannerImpl";
    private Context mContext;
    private IDecodingCallback callback;
    private  volatile static BarcodeScannerImpl instance;
    private boolean isScanning;
    private Object scanSync = new Object();
    private ScanUtil mScanUtil;
    private ScanParameters scanParameters;
    private ScannerConfig scannerConfig;
    public static BarcodeScannerImpl getInstance(Context mContext, ScannerConfig scannerConfig) {
        if (instance == null) {
            synchronized (BarcodeScannerImpl.class) {
                if (instance == null || instance.mContext != mContext || instance.scannerConfig != scannerConfig) {
                    instance = new BarcodeScannerImpl(mContext, scannerConfig);
                }
            }
        } else {
            if (instance.scannerConfig != scannerConfig || instance.mContext != mContext) {
                instance = new BarcodeScannerImpl(mContext, scannerConfig);
            }
        }
        return instance;
    }

    private BarcodeScannerImpl(Context mContext, ScannerConfig scannerConfig) {
        this.mContext = mContext;
        this.scannerConfig = scannerConfig;

    }


    @Override
    public void setDecodingCallback(IDecodingCallback callBack) throws NSDKException{
        if (callBack == null) {
            throw new NSDKIllegalParameterException("Callback shall not be null.");
        }
        if (callBack instanceof DecodingCallback) {
            this.callback = (DecodingCallback) callBack;
        } else if (callBack instanceof DecodingByteCallback) {
            if (Build.MODEL.contains("CPOS") && Build.VERSION.SDK_INT <= 25) {
                throw new NSDKException(ErrorCode.NOT_SUPPORTED, "CPOS X5 A7 devices can not support DecodingByteCallback.");
            }
            this.callback = (DecodingByteCallback) callBack;
        }
    }

    @Override
    public void initScan(ScanParameters scanParameters) throws NSDKException {
        if (mScanUtil != null) {
            mScanUtil.release();
            mScanUtil = null;
        }
        ScanParameters mScanParameters = scanParameters;
        if (scanParameters == null) {
            mScanParameters = new ScanParameters();
        }
        this.scanParameters = mScanParameters;
        ScannerType scannerType = mScanParameters.getScannerType();
        if (scannerType == null) {
            throw new NSDKIllegalParameterException("Scanner type shall not be null.");
        }
        if (scannerType == ScannerType.FRONT_CAMERA || scannerType == ScannerType.BACK_CAMERA || scannerType == ScannerType.PAYMENT_CAMERA) {
            isSupportedSoftDecode();
            int cameraId = getCameraId(mScanParameters.getScannerType());
            mScanUtil = new ScanUtil(mContext, mScanParameters.getSurfaceView(), cameraId, mScanParameters.isSoundSwitcher(), mScanParameters.getTimeout(), 1);
        } else if (scannerType == ScannerType.HARDWARE_SCANNER) {
            isSupportedHardScanning();
            mScanUtil = new ScanUtil(mContext, null, 1000, mScanParameters.isSoundSwitcher(),  mScanParameters.getTimeout(), 0);
            mScanUtil.init(ScanUtil.MODE_ONCE, mScanParameters.getTimeout(), mScanParameters.getFocusMode(), mScanParameters.isSoundSwitcher());
        }
    }

    @Override
    public void startScan() throws NSDKException {
        synchronized (scanSync) {
            if (callback == null) {
                throw new NSDKIllegalParameterException("Callback shall not be null.");
            }
            if (isScanning) {
                return;
            }
            if (mScanUtil == null) {
                throw new NSDKException("Please init scan first.");
            }
            final long startTime = System.currentTimeMillis();
            final long lastTime = scanParameters.getTimeout();
            NSDKExecutors.threadStart(new Runnable() {
                @Override
                public void run() {
                    isScanning = true;
                    if (callback instanceof DecodingCallback) {
                        String scanResult = (String) mScanUtil.doScan();
                        if (scanResult != null && scanResult.length() > 0 ) {
                            if (scanResult.substring(0, 1).equalsIgnoreCase("S")) {
                                ((DecodingCallback) callback).onDecodingCallback(ErrorCode.OK, scanResult.substring(1));
                            } else if (scanResult.substring(0, 1).equalsIgnoreCase("F")){
                                ((DecodingCallback) callback).onDecodingCallback(ErrorCode.TIMEOUT, "onTimeout");
                            }
                        } else if (scanResult == null){
                            if (System.currentTimeMillis() - startTime >= lastTime) {
                                scanResult = "OnTimeout!";
                                ((DecodingCallback) callback).onDecodingCallback(ErrorCode.TIMEOUT, scanResult);
                            } else {
                                scanResult = "OnStop!";
                                ((DecodingCallback) callback).onDecodingCallback(ErrorCode.CANCELLED, scanResult);
                            }
                        }
                        isScanning = false;
                    } else if (callback instanceof DecodingByteCallback) {
                        byte[] scanResult = (byte[]) mScanUtil.doScanWithRawByte();
                        if (scanResult != null && scanResult.length > 0) {
                            ((DecodingByteCallback) callback).onDecodingByteCallback(ErrorCode.OK, scanResult);
                        } else if (scanResult == null){
                            byte[] result = null;
                            if (System.currentTimeMillis() - startTime >= lastTime) {
                                result = "OnTimeout!".getBytes(StandardCharsets.UTF_8);
                                ((DecodingByteCallback) callback).onDecodingByteCallback(ErrorCode.TIMEOUT, result);
                            } else {
                                result = "OnStop!".getBytes(StandardCharsets.UTF_8);
                                ((DecodingByteCallback) callback).onDecodingByteCallback(ErrorCode.CANCELLED, result);
                            }
                        }
                    }
                    isScanning = false;

                }
            });
        }

    }

    @Override
    public void stopScan() throws NSDKException {
        if (mScanUtil == null) {
            throw new NSDKException("Please init scan first.");
        }
        if (!isScanning) {
            throw new NSDKException("No existing scanning thread");
        }
        synchronized (scanSync) {
            mScanUtil.stopScan();
            isScanning = false;
        }
    }

    @Override
    public void openLight() throws NSDKException {
        isSupportedSoftDecode();
        if (mScanUtil == null) {
            throw new NSDKException("Please init scan first.");
        }
        try {
            mScanUtil.openLight();
        } catch (Exception e) {
            throw new NSDKException(ErrorCode.ERROR, String.format(Locale.US, "Failed to open light: %s", e.getMessage()));
        }

    }

    @Override
    public void closeLight() throws NSDKException {
        isSupportedSoftDecode();
        if (mScanUtil == null) {
            throw new NSDKException("Please init scan first.");
        }
        try {
            mScanUtil.closeLight();
        } catch (Exception e) {
            throw new NSDKException(ErrorCode.ERROR, String.format(Locale.US, "Failed to close light: %s", e.getMessage()));
        }

    }

    @Override
    public void set(ScanSettings scanSettings) throws NSDKException {
        isSupportedSoftDecode();
        if (scanSettings == null) {
            throw new NSDKIllegalParameterException("Scan settings shall not be null.");
        }
        if (mScanUtil == null) {
            throw new NSDKException("Please init scan first.");
        }
        int ret = 0;
        Boolean isUPCEANSwitch = scanSettings.isUPCEANSwitch();
        if (isUPCEANSwitch != null) {
            if (isUPCEANSwitch) {
                ret = mScanUtil.setNlsUPCEANSwitch(1);
            } else {
                ret = mScanUtil.setNlsUPCEANSwitch(0);
            }
        }
        if (ret < 0) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to set NLS UPC EAN switch key, ret = %d", ret));
        }

        List<ScanCodeOption> scanCodeOptions = scanSettings.getScanCodeOptions();
        if (scanCodeOptions != null && scanCodeOptions.size() != 0) {
            for (ScanCodeOption scanCodeOption : scanCodeOptions) {
                String codeId = scanCodeOption.getCodeId();
                if (TextUtils.isEmpty(codeId)) {
                    throw new NSDKIllegalParameterException("Code id shall not be null.");
                }
                String key = scanCodeOption.getKey();
                if (TextUtils.isEmpty(key)) {
                    throw new NSDKIllegalParameterException("Attribute key shall not be null.");
                }
                String value = scanCodeOption.getValue();
                if (TextUtils.isEmpty(value)) {
                    throw new NSDKIllegalParameterException("Attribute value shall not be null.");
                }
                ret = mScanUtil.setNlsScn(codeId, key, value);
                if (ret < 0) {
                    throw new NSDKException(ret, String.format(Locale.US, "Failed to set NLS scn, ret = %d", ret));
                }
            }
        }
    }

    @Override
    public void releaseScan() throws NSDKException {
        if (mScanUtil == null) {
            throw new NSDKException("Please init scan first.");
        }
        mScanUtil.release();
        mScanUtil = null;
        isScanning = false;
        this.callback = null;
    }

    private void isSupportedSoftDecode() throws NSDKException {
        if (!scannerConfig.hasFrontCamera() && !scannerConfig.hasBackCamera() && !scannerConfig.hasPaymentCamera()) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "Unsupported method.");
        }
    }

    private void isSupportedHardScanning() throws NSDKException{
        if (!scannerConfig.supportHardScanning()) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "Unsupported method.");
        }
    }

    private boolean isSingleFrontCamera() {
        return scannerConfig.hasFrontCamera() && !scannerConfig.hasBackCamera() && !scannerConfig.hasPaymentCamera();
    }

    private int getCameraId(ScannerType scannerType) {
        switch (scannerType) {
            case FRONT_CAMERA:
                if (isSingleFrontCamera()) {
                    return 0;
                } else {
                    return 1;
                }
            case PAYMENT_CAMERA:
                return 2;
            default:
                return 0;
        }
    }
}
