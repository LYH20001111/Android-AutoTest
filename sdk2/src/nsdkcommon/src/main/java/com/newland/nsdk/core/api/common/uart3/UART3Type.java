package com.newland.nsdk.core.api.common.uart3;

/**
 * UART3 type.
 * @deprecated This is instead by {@link com.newland.nsdk.core.api.internal.serialportmanager.SerialPortType}.
 */
public enum UART3Type {
    /**
     * RS232 port of CPOS device.
     */
    RS232_CPOS(62),
    /**
     * PINPAD port of A7 devices.
     * <ul>
     *     <li>N910 A7</li>
     *     <li>N850 A7</li>
     *     <li>N700 A7</li>
     * </ul>
     */
    RS232_A7(61),
    /**
     * PINPAD port of CPOS device.
     */
    PINPAD_CPOS(60),

    /**
     * RS232 port for U2000 device.
     */
    RS232B(64),


    /**
     * PINPAD port of A7 devices(COM2).
     * <ul>
     *     <li>N910 A7</li>
     *     <li>N850 A7</li>
     *     <li>N700 A7</li>
     * </ul>
     */
    PINPAD_A7(-1);

    int code;

    UART3Type(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
