package com.newland.sdk.module.serialport;

import com.newland.sdk.module.externalPin.BleBaseParams;

/**
 * @description: Extra serial port parameters
 * @author: Lindan
 * @create: 2019/07/26
 */
public class SerialExtParams {

    private DataBit dataBit = DataBit.DATA_BIT_8;
    private CheckBit oddEvenCheck = CheckBit.NO_CHECK;
    private StopBit stopBit = StopBit.STOP_BIT_ONE;

    private String bleName;
    private String bleAddress;

    private PortType portType;

    // RS232口是否使用UART3
    private boolean isRS232UART3 = false;
    private BleBaseParams bleBaseParams;//底座扩展参数

    public SerialExtParams() {}

    public SerialExtParams(DataBit dataBit, CheckBit oddEvenCheck, StopBit stopBit) {
        this.dataBit = dataBit;
        this.oddEvenCheck = oddEvenCheck;
        this.stopBit = stopBit;
    }


    public DataBit getDataBit() {
        return dataBit;
    }

    public void setDataBit(DataBit dataBit) {
        this.dataBit = dataBit;
    }

    public CheckBit getOddEvenCheck() {
        return oddEvenCheck;
    }

    public void setOddEvenCheck(CheckBit oddEvenCheck) {
        this.oddEvenCheck = oddEvenCheck;
    }

    public StopBit getStopBit() {
        return stopBit;
    }

    public void setStopBit(StopBit stopBit) {
        this.stopBit = stopBit;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public String getBleAddress() {
        return bleAddress;
    }

    public void setBleAddress(String bleAddress) {
        this.bleAddress = bleAddress;
    }

    public PortType getPortType() {
        return portType;
    }

    public void setPortType(PortType portType) {
        this.portType = portType;
    }

    public boolean isRS232UART3() {
        return isRS232UART3;
    }

    public void setRS232UART3(boolean isRS232UART3) {
        this.isRS232UART3 = isRS232UART3;
    }

    public BleBaseParams getBleBaseParams() {
        return bleBaseParams;
    }

    public void setBleBaseParams(BleBaseParams bleBaseParams) {
        this.bleBaseParams = bleBaseParams;
    }
}
