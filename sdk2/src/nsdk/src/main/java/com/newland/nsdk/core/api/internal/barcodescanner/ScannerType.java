package com.newland.nsdk.core.api.internal.barcodescanner;

public enum ScannerType {
    /**
     * Front camera.
     */
    FRONT_CAMERA,

    /**
     * Back camera.
     */
    BACK_CAMERA,

    /**
     * Payment camera, this is only used in CPOS X5 devices currently.
     */
    PAYMENT_CAMERA,

    /**
     * Hardware scanner.
     */
    HARDWARE_SCANNER

}
