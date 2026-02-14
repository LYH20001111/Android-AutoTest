package com.newland.nsdk.core.api.internal.pinentry;

public class KeyboardParameters {
    /**
     * The minimum swipe distance, unit: pixels
     */
    private int swipeDistance;
    /**
     * The interval of the clicks, unit:ms
     */
    private int clickInterval;
    /**
     * The press time to be regarded as long-press, unit:ms
     */
    private int pressTime;
    /**
     * The effective click mode, default is {@link ClickMode#RELEASE}.
     */
    private ClickMode clickMode = ClickMode.RELEASE;
    /**
     * The effective selected mode, default is {@link EffectMode#RELEASE}.
     */
    private EffectMode effectMode = EffectMode.RELEASE;
    /**
     * Whether to use random key pad.
     */
    private boolean isRandomPinpad;
    /**
     * Reversed data.
     */
    private byte[] additionalData;


    /**
     * Gets the minimum swipe distance, unit: pixels
     * @return The minimum swipe distance, unit: pixels
     */
    public int getSwipeDistance() {
        return swipeDistance;
    }

    /**
     * Sets the minimum swipe distance, unit: pixels
     * @param swipeDistance The minimum swipe distance, unit: pixels
     */
    public void setSwipeDistance(int swipeDistance) {
        this.swipeDistance = swipeDistance;
    }

    /**
     * Gets the interval of the clicks, unit:ms
     * @return The interval of the clicks, unit:ms
     */
    public int getClickInterval() {
        return clickInterval;
    }

    /**
     * Sets the interval of the clicks, unit:ms
     * @param clickInterval The interval of the clicks, unit:ms
     */
    public void setClickInterval(int clickInterval) {
        this.clickInterval = clickInterval;
    }

    /**
     * Gets the press time to be regarded as long-press, unit:ms
     * @return The press time to be regarded as long-press, unit:ms
     */
    public int getPressTime() {
        return pressTime;
    }

    /**
     * Sets the press time to be regarded as long-press, unit:ms
     * @param pressTime The press time to be regarded as long-press, unit:ms
     */
    public void setPressTime(int pressTime) {
        this.pressTime = pressTime;
    }

    /**
     * Gets the effective click mode.
     * @return The effective click mode, see {@link ClickMode}.
     */
    public ClickMode getClickMode() {
        return clickMode;
    }

    /**
     * Sets the effective click mode.
     * @param clickMode The effective click mode, see {@link ClickMode}.
     */
    public void setClickMode(ClickMode clickMode) {
        this.clickMode = clickMode;
    }

    /**
     * Gets the effective selected mode.
     * @return The effective selected mode, see {@link EffectMode}.
     */
    public EffectMode getEffectMode() {
        return effectMode;
    }

    /**
     * Sets the effective selected mode.
     * @param effectMode The effective selected mode, see {@link EffectMode}.
     */
    public void setEffectMode(EffectMode effectMode) {
        this.effectMode = effectMode;
    }

    /**
     * Gets whether to used random key pad.
     * @return Whether to used random key pad.
     */
    public boolean isRandomPinpad() {
        return isRandomPinpad;
    }

    /**
     * Sets whether to used random key pad.
     * @param randomPinpad Whether to used random key pad.
     */
    public void setRandomPinpad(boolean randomPinpad) {
        isRandomPinpad = randomPinpad;
    }

    /**
     * Gets the additional data. It is a reversed parameter.
     * @return The additional data.
     */
    public byte[] getAdditionalData() {
        return additionalData;
    }

    /**
     * Sets the additional data. It is a reversed parameter.
     * @param additionalData The additional data.
     */
    public void setAdditionalData(byte[] additionalData) {
        this.additionalData = additionalData;
    }
}
