package com.newland.nsdk.core.api.external.keyboard;

/**
 * Parameters for keyboard entry.
 */
public class KeyboardParameters {

    private PromptID promptId;
    private KeyboardMode keyboardMode;
    private byte minLen;
    private byte maxLen;

    /**
     * Gets prompt ID.
     *
     * @return Indicates what message to prompt. See {@link PromptID}.
     */
    public PromptID getPromptID() {
        return promptId;
    }

    /**
     * Sets prompt ID.
     *
     * @param promptId Indicates what message to prompt. See {@link PromptID}.
     */
    public void setPromptID(PromptID promptId) {
        this.promptId = promptId;
    }

    /**
     * Gets keyboard mode.
     *
     * @return Keyboard mode, see {@link KeyboardMode}.
     */
    public KeyboardMode getKeyboardMode() {
        return keyboardMode;
    }

    /**
     * Sets keyboard mode.
     *
     * @param keyboardMode Keyboard mode, see {@link KeyboardMode}.
     */
    public void setKeyboardMode(KeyboardMode keyboardMode) {
        this.keyboardMode = keyboardMode;
    }

    /**
     * Gets minimum number of characters to input.
     *
     * @return Minimum number of characters to input, shall be between 1 and maximum number.
     */
    public byte getMinLen() {
        return minLen;
    }

    /**
     * Sets the Minimum number of characters to input.
     *
     * @param minLen Minimum number of characters to input, shall be between 1 and maximum number.
     */
    public void setMinLen(byte minLen) {
        this.minLen = minLen;
    }

    /**
     * Gets maximum number of characters to input.
     *
     * @return Maximum number of characters to input, shall be <= 32 but >= minimum number.
     */
    public byte getMaxLen() {
        return maxLen;
    }

    /**
     * Sets maximum number of characters to input.
     *
     * @param maxLen Maximum number of characters to input, shall be <= 32 but >= minimum number.
     */
    public void setMaxLen(byte maxLen) {
        this.maxLen = maxLen;
    }

}
