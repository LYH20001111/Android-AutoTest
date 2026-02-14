package com.newland.nsdk.core.api.internal.devicemanager;

public class ScannerConfig {
    boolean frontCamera;
    boolean backCamera;
    boolean paymentCamera;
    boolean frontScanner;
    boolean softDecoding;
    boolean hardScanning;

    public ScannerConfig(boolean frontCamera, boolean backCamera, boolean paymentCamera, boolean frontScanner, boolean softDecoding, boolean hardScanning){
        this.frontCamera = frontCamera;
        this.backCamera = backCamera;
        this.paymentCamera = paymentCamera;
        this.frontScanner = frontScanner;
        this.softDecoding = softDecoding;
        this.hardScanning = hardScanning;
    }

    public boolean hasFrontCamera() {
        return frontCamera;
    }

    public boolean hasBackCamera() {
        return backCamera;
    }

    public boolean hasPaymentCamera() {
        return paymentCamera;
    }

    public boolean hasFrontScanner() {
        return frontScanner;
    }

    public boolean supportSoftDecoding(){
        return softDecoding;
    }

    public boolean supportHardScanning() {
        return hardScanning;
    }

    public int getCameraID(CameraType type){
        switch (type) {
            case BACK:
                if (backCamera) {
                    return 0;
                }

                break;
            case FRONT:
                if (frontCamera) {
                    if (backCamera) {
                        return 1;
                    }
                    return 0;
                }

                break;
            case PAYMENT:
                if (paymentCamera) {
                    if (frontCamera && backCamera) {
                        return 2;
                    }
                    if ((!frontCamera && backCamera) || (frontCamera && !backCamera)) {
                        return 1;
                    }
                    if (!frontCamera && !backCamera) {
                        return 0;
                    }
                }

                break;
            default:
                break;
        }
        return -1;
    }
}
