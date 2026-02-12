package com.newland.sdk.module.serialport;


public enum Baudrate {

    BPS300(300),
    BPS1200(1200),
    BPS2400(2400),
    BPS4800(4800),
    BPS7200(7200),
    BPS9600(9600),
    BPS19200(19200),
    BPS38400(38400),
    BPS57600(57600),
    BPS115200(115200);
    private int bValue;

    private Baudrate(int bValue) {
        this.bValue = bValue;
    }

    public int toValue() {
        return bValue;
    }
}
