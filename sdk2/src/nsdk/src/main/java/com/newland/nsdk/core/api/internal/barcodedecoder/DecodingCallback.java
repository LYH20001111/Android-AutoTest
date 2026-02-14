package com.newland.nsdk.core.api.internal.barcodedecoder;

/**
 * Used for receiving decoding result.
 */
public interface DecodingCallback extends IDecodingCallback{
    /**
     * Called when decoding finished successfully.
     *
     * @param eventCode The event code. When decoding finished successfully, event code is 1.
     * @param result    The decoding result.
     */
    void onDecodingCallback(int eventCode, String result);
}
