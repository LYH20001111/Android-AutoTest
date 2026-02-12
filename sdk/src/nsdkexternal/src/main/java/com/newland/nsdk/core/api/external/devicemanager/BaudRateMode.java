package com.newland.nsdk.core.api.external.devicemanager;

/**
 * Baud rate mode of the external device.
 */
public enum BaudRateMode {
    /**
     * Do not change the baud rate.
     */
    NO_CHANGE("00"),

    /**
     * Baud rate: 19200
     * Data bits: 8
     * Parity bit: No check
     * Stop bits: 1
     */
   MODE_19200_8_N_1("12"),

    /**
     * Baud rate: 2400
     * Data bits: 8
     * Parity bit: No check
     * Stop bits: 1
     */
    MODE_2400_8_N_1("14"),

    /**
     * Baud rate: 9600
     * Data bits: 8
     * Parity bit: No check
     * Stop bits: 1
     */
    MODE_9600_8_N_1("15"),

    /**
     * Baud rate: 38400
     * Data bits: 8
     * Parity bit: No check
     * Stop bits: 1
     */
    MODE_38400_8_N_1("17"),

    /**
     * Baud rate: 57600
     * Data bits: 8
     * Parity bit: No check
     * Stop bits: 1
     */
    MODE_57600_8_N_1("18"),

    /**
     * Baud rate: 115200
     * Data bits: 8
     * Parity bit: No check
     * Stop bits: 1
     */
    MODE_115200_8_N_1("19");

    private String code;
    BaudRateMode(String code){
        this.code = code;
    }
    public String getCode(){
        return this.code;
    }
    public static BaudRateMode fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (BaudRateMode baudRateMode : BaudRateMode.values()) {
            if (baudRateMode.getCode().equals(value)) {
                return baudRateMode;
            }
        }
        return null;
    }
}
