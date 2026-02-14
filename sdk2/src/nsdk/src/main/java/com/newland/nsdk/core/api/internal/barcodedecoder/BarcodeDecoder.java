package com.newland.nsdk.core.api.internal.barcodedecoder;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * Helper class for decoding image data.
 *
 * <p>Below shows how to use this module to decode image data:</p>
 * <pre>
 *     // 1. Get barcode decoder.
 *     BarcodeDecoder barcodeDecoder = (BarcodeDecoder)NSDKModuleManagerImpl.getInstance()
 *                                            .getModule(ModuleType.BARCODE_DECODER);
 *
 *     // 2. Set callback to monitor decoding result.
 *     barcodeDecoder.setDecodingCallback(new DecodingCallback() {
 *             {@code @Override}
 *             public void onDecodingCallback(int eventCode, final String result) {
 *                 // Handle the result
 *             }
 *     });
 *
 *     // 3. Start to decode when you got image data.
 *     try {
 *         barcodeDecoder.startDecode(imageData, imageWidth, imageHeight);
 *     } catch(NSDKException e) {
 *         // Handle the exception.
 *     }
 *
 *     // 4. Stop decoding if you need.
 *     try {
 *         barcodeDecoder.StopDecode();
 *     } catch(NSDKException e) {
 *         // Handle the exception.
 *     }
 *
 * </pre>
 */
public interface BarcodeDecoder extends Module {
    /**
     * Registers a callback to notify decoding result.
     *
     * <p>This shall be called before starting decoding.</p>
     *
     * @param decodingCallback <b>[Required]</b> The callback which is invoked after decoding finished.
     */
    void setDecodingCallback(IDecodingCallback decodingCallback) throws NSDKException;

    /**
     * Starts to decode.
     *
     * <p>The result of decoding will be given by the registered callback.</p>
     *
     * @param imageData <b>[Required]</b> The image that needs to be decoded.It could be an image captured by camera which's default format is YUV. Maximum resolution: 1280*960.
     * @param nWidth    <b>[Required]</b> The width of the image. Shall be >0.
     * @param nHeight   <b>[Required]</b> The height of the image. Shall be >0.
     * @throws NSDKException
     */
    void startDecode(byte[] imageData, int nWidth, int nHeight) throws NSDKException;

    /**
     * Stops decoding.
     *
     * @throws NSDKException
     */
    void stopDecode() throws NSDKException;
}
