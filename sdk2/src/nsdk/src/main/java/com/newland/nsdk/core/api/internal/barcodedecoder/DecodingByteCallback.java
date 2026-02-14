package com.newland.nsdk.core.api.internal.barcodedecoder;

/**
 * Used for receiving decoding/scanning result, which returns the result of type "byte[]".
 */
public interface DecodingByteCallback extends IDecodingCallback{
    /**
     * Called when decoding/scanning finished successfully.
     * @param eventCode    The event code. When decoding finished successfully, event code is 1.
     * @param scanResult   The decoding/scanning result
     */
    void onDecodingByteCallback(int eventCode, byte[] scanResult);
}
