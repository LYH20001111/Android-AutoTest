package com.newland.nsdk.core.api.internal.barcodescanner;

import android.newland.scan.ScanUtil;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

/**
 * Parameters for hard scanning.
 */
public class ScanParameters {
    public static final int DEFAULT_SCAN_TIME = 3000;
    public static final int FOCUS_OFF = 0;
    public static final int FOCUS_READING = 1;
    public static final int FOCUS_ON = 2;
    private int timeout = DEFAULT_SCAN_TIME;
    private int focusMode = FOCUS_READING;
    private boolean soundSwitcher = false;
    private ScannerType scannerType = ScannerType.HARDWARE_SCANNER;
    private WeakReference<SurfaceView> surfaceViewRef;

    /**
     * Gets scanning timeout.
     * @return Scanning timeout.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets scanning timeout.
     * @param timeout Scanning timeout, which shall be range from 1000 to 25400 ms
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets scanner focus mode.
     * @return Scanner focus mode.
     */
    public int getFocusMode() {
        return focusMode;
    }

    /**
     * Sets scanner focus mode. This parameter is temporarily useless.
     * @param focusMode Scanner focus mode, which can ScanUtil.FOCUS_ON(2), ScanUtil.FOCUS_OFF(0) or ScanUtil.FOCUS_READING(1).
     */
    public void setFocusMode(int focusMode) {
        this.focusMode = focusMode;
    }

    /**
     * Gets whether to open buzzer when recognize the barcode or not.
     * @return Whether to open buzzer when recognize the barcode or not.
     */
    public boolean isSoundSwitcher() {
        return soundSwitcher;
    }

    /**
     * Sets whether to open buzzer when recognize the barcode or not.
     * @param soundSwitcher Whether to open buzzer when recognize the barcode or not.
     */
    public void setSoundSwitcher(boolean soundSwitcher) {
        this.soundSwitcher = soundSwitcher;
    }

    /**
     * Gets the current scanner type.
     * @return Current scanner type. See {@link ScannerType}.
     */
    public ScannerType getScannerType() {
        return scannerType;
    }

    /**
     * Sets the scanner type to be used.
     * @param scannerType Scanner type to be used. See {@link ScannerType}.
     */
    public void setScannerType(ScannerType scannerType) {
        this.scannerType = scannerType;
    }

    /**
     * Gets the surface view for scanning.
     * @return The surface view for scanning.
     */
    public SurfaceView getSurfaceView() {
        if (surfaceViewRef != null) {
            return surfaceViewRef.get();
        }
        return null;
    }

    /**
     * Sets the surface view for scanning.
     * @param surfaceView
     */
    public void setSurfaceView(SurfaceView surfaceView) {
        this.surfaceViewRef = new WeakReference<>(surfaceView);
    }
}
