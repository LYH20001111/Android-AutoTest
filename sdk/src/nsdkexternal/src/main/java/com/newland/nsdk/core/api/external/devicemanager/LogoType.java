package com.newland.nsdk.core.api.external.devicemanager;

/**
 * Logo type.
 */
public enum LogoType {
    /**
     * Fully-charged icon when on power off status.
     */
    POWER_OFF_FULLY_CHARGED((byte)0x03),
    /**
     * Power off icon.
     */
    POWER_OFF((byte)0x04),
    /**
     * Charging icon when on power off status.
     */
    POWER_OFF_CHARGING((byte)0x05);
    private byte code;
    LogoType(byte code) {
        this.code = code;
    }
    public byte getCode(){
        return this.code;
    }
}
