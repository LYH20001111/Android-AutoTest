package com.newland.nsdk.core.api.external.signature;

public interface ExtESignatureListener {
    /**
     * Invoked when finishing receive signature data.
     * @param imageData  The signature picture data.
     */
    void onComplete(byte[] imageData);

    /**
     * Invoked when error occurred.
     * @param code   Error code.
     * @param errorMessage  Error message.
     */
    void onError(int code, String errorMessage);

    /**
     * Invoked when cancel signature procedure.
     */
    void onCancel();
}
