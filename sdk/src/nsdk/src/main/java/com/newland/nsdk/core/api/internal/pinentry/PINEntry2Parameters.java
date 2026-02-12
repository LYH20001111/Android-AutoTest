package com.newland.nsdk.core.api.internal.pinentry;

import com.newland.nsdk.core.api.common.pinentry.PINBlockMode;

import java.util.Map;

public class PINEntry2Parameters {

    private byte[] pinLengthRange;
    private int maxPINLen = 12;
    private int minPINLen = 4;
    private PINBlockMode pinBlockMode;
    private boolean autoComplete = false;
    private boolean checkIcPresent = false;
    private Map<PINPadButton, PINPadButton> customButtons;

    /**
     * Gets PIN block mode.
     *
     * @return PIN block mode. See {@link PINBlockMode}
     */
    public PINBlockMode getPINBlockMode() {
        return pinBlockMode;
    }

    /**
     * Sets PIN block mode.
     *
     * @param pinBlockMode PIN block mode. See {@link PINBlockMode}
     */
    public void setPINBlockMode(PINBlockMode pinBlockMode) {
        this.pinBlockMode = pinBlockMode;
    }

    /**
     * Gets min length of PIN input.
     *
     * @return Min length of PIN input. Default value is 4.
     */
    public int getMinPINLen() {
        return minPINLen;
    }

    /**
     * Sets min length of PIN input.
     *
     * @param minPINLen Min length of PIN input. Default value is 4.
     */
    public void setMinPINLen(int minPINLen) {
        this.minPINLen = minPINLen;
    }

    /**
     * Gets PIN length range.
     *
     * @return PIN length range. E.g., [4, 6, 8, 10] means only PIN with length 4, 6, 8 or 10 is allowed. To support ByPass, add "0" to the range: [0, 4, 6, 8, 10].
     */
    public byte[] getPINLengthRange() {
        return pinLengthRange;
    }

    /**
     * Sets PIN length range.
     *
     * <p>If length range is not set, PINs with length from min len to max len are allowed.</p>
     * <p>E.g., min len is 4, max len is 8, no length range is set. Then PINs with length [4, 5, 6, 7, 8] are allowed.</p>
     *
     * @param pinLengthRange PIN length range. E.g., [4, 6, 8, 10] means only PIN with length 4, 6, 8 or 10 is allowed.
     */
    public void setPINLengthRange(byte[] pinLengthRange) {
        this.pinLengthRange = pinLengthRange;
    }

    /**
     * Gets max length of PIN input.
     *
     * @return Max length of PIN input. Default value is 12.
     */
    public int getMaxPINLen() {
        return maxPINLen;
    }

    /**
     * Sets max length of PIN input.
     *
     * @param maxPINLen Max length of PIN input. Default value is 12. Max len shall be greater than min len.
     */
    public void setMaxPINLen(int maxPINLen) {
        this.maxPINLen = maxPINLen;
    }

    /**
     * Gets Whether finishing PIN entry when the length of password has reached max length or not.
     * @return Whether finishing PIN entry when the length of password has reached max length or not.
     */
    public boolean isAutoComplete() {
        return autoComplete;
    }

    /**
     * Sets Whether finishing PIN entry when the length of password has reached max length or not.
     * @param autoComplete Whether finishing PIN entry when the length of password has reached max length or not.
     */
    public void setAutoComplete(boolean autoComplete) {
        this.autoComplete = autoComplete;
    }

    /**
     * Gets whether to check IC card is present during online PIN entry process.
     * @return Whether to check IC card is present during online PIN entry process.
     */
    public boolean isCheckIcPresent() {
        return checkIcPresent;
    }

    /**
     * Sets whether to check IC card is present during online PIN entry process.
     * @param checkIcPresent
     */
    public void setCheckIcPresent(boolean checkIcPresent) {
        this.checkIcPresent = checkIcPresent;
    }


    /**
     * Gets the map of the custom button function.
     * @return The map of the custom button function.
     */
    public Map<PINPadButton, PINPadButton> getCustomButtons() {
        return customButtons;
    }

    /**
     * Sets the map of the custom button function.
     * @param customButtons The map of the custom button function.
     */
    public void setCustomButtons(Map<PINPadButton, PINPadButton> customButtons) {
        this.customButtons = customButtons;
    }
}
