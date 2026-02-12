package com.newland.nsdk.core.api.internal.barcodescanner;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.internal.barcodedecoder.IDecodingCallback;

/**
 * Helper class for hard-scanning decode.
 *
 * <p>Below shows how to use this module to perform hard-scanning decoding to decode barcode:</p>
 * <pre>
 *     //1.Get hard scan.
 *     BarcodeScanner barcodeScanner = (BarcodeScanner)NSDKModuleManagerImpl.getInstance()
 *                              .getModule(ModuleType.BARCODE_SCANNER);
 *     //2. Set callback to monitor decoding result, which has two selections.
 *     //2.1:This will return decoding result,which is type of String
 *     barcodeScanner.setDecodingCallback(new DecodingCallback() {
 *         {@code @Override}
 *         public void onDecodingCallback(int eventCode, final String result) {
 *             //Handle the result.
 *         }
 *     }
 *     //2.2:This will return original decoding result,which is type of byte[]
 *     barcodeScanner.setDecodingCallback(new DecodingByteCallback() {
 *         {@code @Override}
 *         public void onDecodingByteCallback(int eventCode, byte[] scanResult) {
 *             //Handle the result.
 *         }
 *     }
 *
 *     //3.Init ScanUtil and parameters for scanning.
 *     //scanParameters has default value, you can input "null" here.
 *     barcodeScanner.initScan(new ScanParameters());
 *
 *     //4.Start Scan
 *     barcodeScanner.startScan();
 *
 *     //5.Stop Scan if you need.
 *     barcodeScanner.stopScan();
 *
 *     //6.Release Scan. It shall be called at finished the whole scanning procedure at last.
 *     barcodeScanner.releaseScan();
 *
 * </pre>
 */
public interface BarcodeScanner extends Module {

    /**
     * Registers a callback to notify hard-scanning decoding result.
     *
     * <p>This shall be called before starting hard-scanning decode.</p>
     * @param callBack  <b>[Required]</b> The callback which is invoked after hard scanning decoding finished.
     * @throws NSDKException
     */
    void setDecodingCallback(IDecodingCallback callBack) throws NSDKException;

    /**
     * Init ScanUtil and set hard-scanning related parameters.
     *
     * @param scanParameters   <b>[Optional]</b> The scanning related parameter which has default value as following:
     *                         <ul>
     *                              <li>Timeout: Scanning process timeout.Default is {@link ScanParameters#DEFAULT_SCAN_TIME}, it can be set to any value ranged from 1000 to 25400 ms.</li>
     *                              <li>FocusMode: Focus light mode. Default is {@link ScanParameters#FOCUS_READING}, it can be also set to {@link ScanParameters#FOCUS_ON} or {@link ScanParameters#FOCUS_OFF}</li>
     *                              <li>SoundSwitcher: Whether to open buzzer when recognize the barcode or not. Default is false.</li>
     *                              <li>ScannerType: The type of scanner. Default is {@link ScannerType#HARDWARE_SCANNER}</li>
     *                              <li>SurfaceView: The surface view set to scanning process, which is available with all kinds of scanner except {@link ScannerType#HARDWARE_SCANNER}.</li>
     *                         </ul>
     * @throws NSDKException
     */
    void initScan(ScanParameters scanParameters) throws NSDKException;

    /**
     * Starts to hard-scanning decoding.
     *
     * <p>It can be use repeatedly if you have already init scanner.</p>
     *
     * @throws NSDKException
     */
    void startScan() throws NSDKException;

    /**
     * Stops hard-scanning decoding if you need.
     *
     * @throws NSDKException
     */
    void stopScan() throws NSDKException;

    /**
     * Opens light.
     * @throws NSDKException
     */
    void openLight() throws NSDKException;

    /**
     * Closes light.
     * @throws NSDKException
     */
    void closeLight() throws NSDKException;

    /**
     * Sets the scan settings of the code config and preview.
     * @param scanSettings <b>[Required]</b> The scan settings of the code config and preview. See {@link ScanSettings}.
     * @throws NSDKException
     */
    void set(ScanSettings scanSettings) throws NSDKException;

    /**
     * Release hard scanner when you finished the whole scanning procedure and before you exit the scanning module.
     *
     * @throws NSDKException
     */
    void releaseScan() throws NSDKException;
}
