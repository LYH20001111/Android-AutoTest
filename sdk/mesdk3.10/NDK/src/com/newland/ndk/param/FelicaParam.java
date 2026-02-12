package com.newland.ndk.param;

/**
 * Author by bxy, Date on 2019/3/5 0005.
 */
public class FelicaParam {
    /**
     * system code
     */
    public byte[] systemCode = new byte[2];
    /**
     * 0x00 - no request
     * 0x01 - system code request
     * 0x02 - communication performance request
     * other: RFU
     */
    public byte requestCode;
    /**
     * time slot
     */
    public byte timeSlot;

    public FelicaParam(){

    }

    public FelicaParam(byte[] systemCode, byte requestCode, byte timeSlot) {
        this.systemCode = systemCode;
        this.requestCode = requestCode;
        this.timeSlot = timeSlot;
    }

    public void setSystemCode(byte[] systemCode) {
        this.systemCode = systemCode;
    }

    public void setRequestCode(byte requestCode) {
        this.requestCode = requestCode;
    }

    public void setTimeSlot(byte timeSlot) {
        this.timeSlot = timeSlot;
    }
}
