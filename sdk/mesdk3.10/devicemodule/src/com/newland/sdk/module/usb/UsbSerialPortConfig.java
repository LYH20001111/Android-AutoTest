package com.newland.sdk.module.usb;

public class UsbSerialPortConfig {
    // Common values
    public static int DATA_BITS_5 = 5;
    public static int DATA_BITS_6 = 6;
    public static int DATA_BITS_7 = 7;
    public static int DATA_BITS_8 = 8;

    public static int STOP_BITS_1 = 1;
    public static int STOP_BITS_15 = 3;
    public static int STOP_BITS_2 = 2;

    public static int PARITY_NONE = 0;
    public static int PARITY_ODD = 1;
    public static int PARITY_EVEN = 2;
    public static int PARITY_MARK = 3;
    public static int PARITY_SPACE = 4;

    public static int FLOW_CONTROL_OFF = 0;
    public static int FLOW_CONTROL_RTS_CTS = 1;
    public static int FLOW_CONTROL_DSR_DTR = 2;
    public static int FLOW_CONTROL_XON_XOFF = 3;

    private int baudRate = 115200;
    private int dataBits = DATA_BITS_8;
    private int stopBits = STOP_BITS_1;
    private int parity = PARITY_NONE;
    private int flowControl = FLOW_CONTROL_OFF;

    public UsbSerialPortConfig() {
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getDataBits() {
        return dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public int getStopBits() {
        return stopBits;
    }

    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public int getFlowControl() {
        return flowControl;
    }

    public void setFlowControl(int flowControl) {
        this.flowControl = flowControl;
    }
}
