package com.newland.nsdk.core.api.common.serialport;

/**
 * The rate at which information is transferred in a communication channel.
 */
public enum BaudRate {

    /**
     * 300 bits per second
     */
    BPS300(300),

    /**
     * 1200 bits per second
     */
    BPS1200(1200),

    /**
     * 2400 bits per second
     */
    BPS2400(2400),

    /**
     * 4800 bits per second
     */
    BPS4800(4800),

    /**
     * 7200 bits per second
     */
    BPS7200(7200),

    /**
     * 9600 bits per second
     */
    BPS9600(9600),

    /**
     * 19200 bits per second
     */
    BPS19200(19200),

    /**
     * 38400 bits per second
     */
    BPS38400(38400),

    /**
     * 57600 bits per second
     */
    BPS57600(57600),

    /**
     * 115200 bits per second
     */
    BPS115200(115200);

    private int bValue;

    BaudRate(int bValue) {
        this.bValue = bValue;
    }

    public int toValue() {
        return bValue;
    }
}
