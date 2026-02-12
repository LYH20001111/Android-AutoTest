package com.newland.nsdk.core.api.external.pinentry;

import com.newland.nsdk.core.api.common.pinentry.PINBlockMode;

/**
 * Parameters for online PIN input.
 */
public class ExtPINEntryParameters {
    private ExtPINMaskLine maskLine = ExtPINMaskLine.LINE_5;
    private byte maxPINLen = 4;
    private boolean autoComplete;
    private PINBlockMode pinBlockMode;
    private String[] displayMessages;

    private byte[] pinLengthRange;

    private Alignment pinMaskAlignment;
    /**
     * Gets maximum length of PIN input. The minimum length is 4.
     *
     * @return Maximum length of PIN input. Value range: [0x04-0x0C].
     */
    public byte getMaxPINLen() {
        return maxPINLen;
    }

    /**
     * Sets maximum length of PIN input. The minimum length is 4.
     *
     * @param maxPinLen Maximum length of PIN input. Value range: [0x04-0x0C].
     */
    public void setMaxPINLen(byte maxPinLen) {
        this.maxPINLen = maxPinLen;
    }

    /**
     * Whether or not Enter key is needed to complete PIN entry.
     *
     * @return Auto completing flag.
     * <ul>
     *     <li>'true': No need to press Enter key to complete PIN entry. It will end PIN entry automatically when the entered PIN length reaches the maximum length.</li>
     *     <li>'false': Enter key is needed to complete PIN entry.</li>
     * </ul>
     */
    public boolean isAutoComplete() {
        return autoComplete;
    }

    /**
     * Sets whether or not Enter key is needed to complete PIN entry.
     *
     * @param autoComplete Auto completing flag.
     *                     <ul>
     *                         <li>'true': No need to press Enter key to complete PIN entry. It will end PIN entry automatically when the entered PIN length reaches the maximum length.</li>
     *                         <li>'false': Enter key is needed to complete PIN entry.</li>
     *                     </ul>
     */
    public void setAutoComplete(boolean autoComplete) {
        this.autoComplete = autoComplete;
    }

    /**
     * Gets PIN block mode.
     *
     * @return PIN block mode. Following modes supported:
     * <ul>
     *     <li>{@link PINBlockMode#ISO9564_0}</li>
     *     <li>{@link PINBlockMode#ISO9564_1}</li>
     *     <li>{@link PINBlockMode#ISO9564_3}</li>
     *     <li>{@link PINBlockMode#ISO9564_4}</li>
     * </ul>
     */
    public PINBlockMode getPINBlockMode() {
        return pinBlockMode;
    }

    /**
     * Sets PIN block mode.
     *
     * @param pinBlockMode PIN block mode. Following modes supported:
     *                     <ul>
     *                         <li>{@link PINBlockMode#ISO9564_0}</li>
     *                         <li>{@link PINBlockMode#ISO9564_1}</li>
     *                         <li>{@link PINBlockMode#ISO9564_3}</li>
     *                         <li>{@link PINBlockMode#ISO9564_4}</li>
     *                     </ul>
     */
    public void setPINBlockMode(PINBlockMode pinBlockMode) {
        this.pinBlockMode = pinBlockMode;
    }

    /**
     * Gets display messages.
     *
     * @return Messages that will displayed in order on each line. If the message is null or empty, nothing will be displayed on the corresponding line.
     */
    public String[] getDisplayMessages() {
        return displayMessages;
    }

    /**
     * Sets display messages.
     *
     * @param displayMessages Messages that will displayed in order on each line. If the message is null or empty, nothing will be displayed on the corresponding line.
     */
    public void setDisplayMessages(String[] displayMessages) {
        this.displayMessages = displayMessages;
    }

    /**
     * Gets mask line.
     *
     * @return Mask line, indicates which line to display input PIN in asterisks. See {@link ExtPINMaskLine}, default value is {@link ExtPINMaskLine#LINE_5}
     */
    public ExtPINMaskLine getMaskLine() {
        return maskLine;
    }

    /**
     * Sets mask line.
     *
     * @param maskLine Mask line, indicates which line to display input PIN in asterisks. See {@link ExtPINMaskLine}, default value is {@link ExtPINMaskLine#LINE_5}
     */
    public void setMaskLine(ExtPINMaskLine maskLine) {
        this.maskLine = maskLine;
    }


    /**
     * Gets pin length range.
     * @return Pin length range, the range between 4 and maxPINLen will be valid in PIN entry process.
     */
    public byte[] getPinLengthRange() {
        return pinLengthRange;
    }

    /**
     * Sets pin length range, the range between 4 and maxPINLen will be valid in PIN entry process.
     * @param pinLengthRange PIN length range, the range between 4 and maxPINLen will be valid in PIN entry process.
     */
    public void setPinLengthRange(byte[] pinLengthRange) {
        this.pinLengthRange = pinLengthRange;
    }

    /**
     * Gets pin mask alignment.
     * @return Pin mask alignment, indicates which position to display the pin mask. See {@link Alignment}, default value is {@link Alignment#Left}.
     */
    public Alignment getPinMaskAlignment() {
        return pinMaskAlignment;
    }

    /**
     * Sets pin mask alignment.
     * @param pinMaskAlignment Pin mask alignment, indicates which position to display the pin mask. See {@link Alignment}, default value is {@link Alignment#Left}.
     */
    public void setPinMaskAlignment(Alignment pinMaskAlignment) {
        this.pinMaskAlignment = pinMaskAlignment;
    }
}
