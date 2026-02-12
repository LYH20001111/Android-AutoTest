package com.newland.sdk.module.scanner;

import android.content.Context;
import android.view.SurfaceView;

import java.util.concurrent.TimeUnit;

public class ScannerExtParams {

    /**
     *  is it a single scan
     */
    private boolean isOnce=true;

    private StartStopCapability startStopCapability;

    public boolean isOnce() {
        return isOnce;
    }

    public void setOnce(boolean once) {
        isOnce = once;
    }

    private DefaultScannerLayout defaultScannerLayout;

    public DefaultScannerLayout getDefaultScannerLayout() {
        return defaultScannerLayout;
    }

    public void setDefaultScannerLayout(DefaultScannerLayout defaultScannerLayout) {
        this.defaultScannerLayout = defaultScannerLayout;
    }

    /**
     * @return
     */
    public StartStopCapability getStartStopCapability() {
        return startStopCapability;
    }

    /**
     * set scan start-stop alphabet is supported or not.
     * @param startStopCapability
     */
    public void setStartStopCapability(StartStopCapability startStopCapability) {
        this.startStopCapability = startStopCapability;
    }
}
