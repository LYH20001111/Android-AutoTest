package com.newland.nsdk.core.api.external.scanner;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * <b>[External Module]</b> Provides the ability to scan.
 *
 * <p>Note: This is only supported on SP100 now.</p>
 *
 * <p>How to get this module:</p>
 * <pre>
 *     ExtScanner extScanner = (ExtScanner)ExtNSDKModuleManagerImpl.getInstance().getModule(ModuleType.EXT_SCANNER);
 * </pre>
 */
public interface ExtScanner extends Module {

    /**
     * Starts scanning.
     *
     * @param timeout  <b>[Required]</b> Time for scanning. Unit: second. Value range: [0-0xFFFF].
     * @param listener <b>[Required]</b> Listens to the scanning result. See {@link ExtScannerListener}.
     * @throws NSDKException
     */
    void startScan(int timeout, ExtScannerListener listener) throws NSDKException;

//    void startScan(int timeout, CameraType cameraType, ExtScannerListener listener) throws NSDKException;

    /**
     * Stop scanning.
     *
     * @throws NSDKException
     */
    void stopScan() throws NSDKException;
}
