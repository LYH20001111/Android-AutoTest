package com.newland.nsdk.core.api.external.pinentry;

/**
 * Extended class for the "New PIN Entry" instruct.
 */
public class ExtendedExtPINEntryParams extends ExtPINEntryParameters{
    private SessionType sessionType;
    private int minLen;
    private PINMessageMode pinMessageMode = PINMessageMode.DEFAULT;
    private Alignment pinMessageAlignment = Alignment.Left;
    private byte[] additionalData;
    private boolean checkIcPresent;

    /**
     * Gets the minimum pin length of the pin entry process.
     * @return The minimum pin length of the pin entry process.
     */
    public int getMinLen() {
        return minLen;
    }

    /**
     * Sets the minimum pin length of the pin entry process.
     * @param minLen The minimum pin length of the pin entry process.
     */
    public void setMinLen(int minLen) {
        this.minLen = minLen;
    }

    /**
     * Gets the PIN messages display mode.
     * @return The PIN messages display mode. See {@link PINMessageMode}.
     */
    public PINMessageMode getPinMessageMode() {
        return pinMessageMode;
    }

    /**
     * Sets the PIN messages display mode.
     * @param pinMessageMode The PIN messages display mode. See {@link PINMessageMode}, default is {@link PINMessageMode#DEFAULT}.
     */
    public void setPinMessageMode(PINMessageMode pinMessageMode) {
        this.pinMessageMode = pinMessageMode;
    }

    /**
     * Gets the alignment of the PIN messages.
     * @return The alignment of the PIN messages. See {@link Alignment}.
     */
    public Alignment getPinMessageAlignment() {
        return pinMessageAlignment;
    }

    /**
     * Sets the alignment of the PIN messages.
     * @param pinMessageAlignment The alignment of the PIN messages. See {@link Alignment}, default is {@link Alignment#Left}.
     */
    public void setPinMessageAlignment(Alignment pinMessageAlignment) {
        this.pinMessageAlignment = pinMessageAlignment;
    }

    /**
     * Gets the additional data for the PIN process.
     * @return The additional data for the PIN process.
     */
    public byte[] getAdditionalData() {
        return additionalData;
    }

    /**
     * Sets the additional data for the PIN process.
     * @param additionalData The additional data for the PIN process.
     */
    public void setAdditionalData(byte[] additionalData) {
        this.additionalData = additionalData;
    }

    /**
     * Gets the session type of the PIN process.
     * @return The session type of the PIN process.
     */
    public SessionType getSessionType() {
        return sessionType;
    }

    /**
     * Sets the session type of the PIN process.
     * @param sessionType The session type of the PIN process. See {@link SessionType}.
     */
    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    /**
     * Gets whether to check IC present when performed PIN process.
     * @return Whether to check IC present when performed PIN process.
     */
    public boolean isCheckIcPresent() {
        return checkIcPresent;
    }

    /**
     * Sets whether to check IC present when performed PIN process.
     * <p>Note: This is only valid with "startOnlinePINEntry".</p>
     * @param checkIcPresent Whether to check IC present when performed PIN process.
     */
    public void setCheckIcPresent(boolean checkIcPresent) {
        this.checkIcPresent = checkIcPresent;
    }
}
