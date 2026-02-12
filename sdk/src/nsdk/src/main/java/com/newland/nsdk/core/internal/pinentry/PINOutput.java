package com.newland.nsdk.core.internal.pinentry;

/**
 * PIN output.
 */
public class PINOutput {
    private PINKeyEvent event;
    private byte[] pinBlock;
    private byte[] ksn;
    private byte[] SW;
    private int pinlen;

    /**
     * Gets PIN length.
     *
     * @return PIN length.
     */
    public int getPinlen() {
        return pinlen;
    }

    /**
     * Sets PIN length.
     *
     * @param pinlen PIN length.
     */
    public void setPinlen(int pinlen) {
        this.pinlen = pinlen;
    }

    /**
     * Gets SW.
     *
     * @return SW, result of APDU command.
     */
    public byte[] getSW() {
        return SW;
    }

    /**
     * Sets SW.
     *
     * @param SW SW, result of APDU command.
     */
    public void setSW(byte[] SW) {
        this.SW = SW;
    }

    /**
     * Gets PIN key event.
     *
     * @return PIN key event, see {@link PINKeyEvent}.
     */
    public PINKeyEvent getEvent() {
        return event;
    }

    /**
     * Sets PIN key event.
     *
     * @param event PIN key event, see {@link PINKeyEvent}.
     */
    public void setEvent(PINKeyEvent event) {
        this.event = event;
    }

    /**
     * Gets PIN block.
     *
     * @return PIN block.
     */
    public byte[] getPinBlock() {
        return pinBlock;
    }

    /**
     * Sets PIN block.
     *
     * @param pinBlock PIN block
     */
    public void setPinBlock(byte[] pinBlock) {
        this.pinBlock = pinBlock;
    }

    /**
     * Gets KSN.
     *
     * @return KSN.
     */
    public byte[] getKsn() {
        return ksn;
    }

    /**
     * Sets KSN.
     *
     * @param ksn KSN.
     */
    public void setKsn(byte[] ksn) {
        this.ksn = ksn;
    }
}
