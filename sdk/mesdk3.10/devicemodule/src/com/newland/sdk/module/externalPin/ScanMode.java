package com.newland.sdk.module.externalPin;

public enum ScanMode {
    /**
     * Enable preview
     * Default mode.
     */
    ENABLE_PREVIEW(1),
    /**
     * Disable preview
     */
    DISABLE_PREVIEW(0);

    private int mode;

    private ScanMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }
}
