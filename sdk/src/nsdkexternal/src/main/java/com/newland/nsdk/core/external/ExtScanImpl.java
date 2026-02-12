package com.newland.nsdk.core.external;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.external.scanner.CameraType;
import com.newland.nsdk.core.api.external.scanner.ExtScanner;
import com.newland.nsdk.core.api.external.scanner.ExtScannerListener;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.external.command.scanner.ExternalScannerModule;

/**
 * @author hlh
 * @date 2020/7/13
 */
public class ExtScanImpl implements ExtScanner {
    private ExternalScannerModule externalScannerModule;

    private volatile boolean isScanning;

    private volatile static ExtScanImpl instance;
    public static ExtScanImpl getInstance() {
        if (instance == null) {
            synchronized (ExtScanImpl.class) {
                if (instance == null) {
                    instance = new ExtScanImpl();
                }
            }
        }
        return instance;
    }

    private ExtScanImpl() {
        externalScannerModule = new ExternalScannerModule();
    }

    @Override
    public void startScan(final int timeout, final ExtScannerListener extScannerListener) throws NSDKException {
        if (isScanning) {
            throw new NSDKException("Scanning is processing now. Stop before starting a new scanning.");
        }

        if (extScannerListener == null) {
            throw new NSDKIllegalParameterException("Listener should not be null!");
        }

        if (timeout < 0 || timeout > 65535) {
            throw new NSDKIllegalParameterException("Timeout should be between 0 and 65535!");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    isScanning = true;
                    byte[] res = externalScannerModule.scan(timeout);
                    isScanning = false;
                    extScannerListener.onSuccess(new String(res));
                } catch (Exception e) {
                    isScanning = false;
                    e.printStackTrace();
                    if (e instanceof NSDKException) {
                        if (((NSDKException) e).getCode() == ErrorCode.TIMEOUT) {
                            extScannerListener.onTimeout();
                        } else {
                            extScannerListener.onError(((NSDKException) e).getCode(), e.getMessage());
                        }
                    } else {
                        extScannerListener.onError(ErrorCode.EXT_ERROR, e.getMessage());
                    }
                }
            }
        });
    }

//    @Override
//    public void startScan(final int timeout, final CameraType cameraType, final ExtScannerListener extScannerListener) throws NSDKException {
//        if (isScanning) {
//            throw new NSDKException("Scanning is processing now. Stop before starting a new scanning.");
//        }
//        if (cameraType == null) {
//            throw new NSDKIllegalParameterException("Camera type shall not be null.");
//        }
//
//        if (extScannerListener == null) {
//            throw new NSDKIllegalParameterException("Listener should not be null!");
//        }
//
//        if (timeout < 0 || timeout > 65535) {
//            throw new NSDKIllegalParameterException("Timeout should be between 0 and 65535!");
//        }
//
//        NSDKExecutors.threadStart(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    isScanning = true;
//                    byte[] res = externalScannerModule.scan(timeout);
//                    isScanning = false;
//                    extScannerListener.onSuccess(new String(res));
//                } catch (Exception e) {
//                    isScanning = false;
//                    e.printStackTrace();
//                    if (e instanceof NSDKException) {
//                        if (((NSDKException) e).getCode() == ErrorCode.TIMEOUT) {
//                            extScannerListener.onTimeout();
//                        } else {
//                            extScannerListener.onError(((NSDKException) e).getCode(), e.getMessage());
//                        }
//                    } else {
//                        extScannerListener.onError(ErrorCode.EXT_ERROR, e.getMessage());
//                    }
//                }
//            }
//        });
//    }

    @Override
    public void stopScan() throws NSDKException {
        externalScannerModule.stopScan(isScanning);
        //isScanning = false;
    }
}
