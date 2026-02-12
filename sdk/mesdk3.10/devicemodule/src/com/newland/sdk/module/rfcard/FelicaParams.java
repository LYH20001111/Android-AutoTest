package com.newland.sdk.module.rfcard;

/**
 * <p>Felica params</p>
 * <p>It is used in {@link RFCardPowerOnExtParams#setFelicaParams(FelicaParams[])}</p>
 *
 * @author linsi
 * @since V3.10.01
 */
public class FelicaParams {

    private byte[] systemCode;
    private byte requestCode;
    private byte timeSlot;

    /**
     * System code
     *
     * @param systemCode
     */
    public void setSystemCode(byte[] systemCode) {
        this.systemCode = systemCode;
    }

    /**
     * <p>Request code</p>
     * <li>0x00 - no request</li>
     * <li>0x01 - system code request</li>
     * <li>0x02 - communication performance request</li>
     * <li>other: RFU</li>
     *
     * @param requestCode
     */
    public void setRequestCode(byte requestCode) {
        this.requestCode = requestCode;
    }

    /**
     * Time slot
     *
     * @param timeSlot
     */
    public void setTimeSlot(byte timeSlot) {
        this.timeSlot = timeSlot;
    }

    public byte[] getSystemCode() {
        return systemCode;
    }

    public byte getRequestCode() {
        return requestCode;
    }

    public byte getTimeSlot() {
        return timeSlot;
    }
}
