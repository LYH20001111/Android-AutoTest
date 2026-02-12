package com.newland.sdk.module.externalPin;

 /**
 * @author linsi
 * @date 2025/03/03
 */
public class ScanParams {

    private ScanMode mode = ScanMode.ENABLE_PREVIEW;

    public ScanMode getMode() {
        return mode;
    }

    /**
     * {@link ScanMode}
     * @param mode
     */
    public void setMode(ScanMode mode) {
        this.mode = mode;
    }
}
