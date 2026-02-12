package com.newland.sdk.module.externalScan;

import com.newland.sdk.module.externalPin.BleBaseParams;
import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.PortType;

/**
 * Author by bxy, Date on 2019/12/10.
 */
public class ScanBoxInitExtParams {

    private CommMode commMode = CommMode.UART;
    private PortType portType = PortType.RS232;
    private Baudrate baudrate = Baudrate.BPS9600;
    private String bleName;
    private String bleAddress;
    private BleBaseParams bleBaseParams = new BleBaseParams(false,true);
    /**
     * Select communication mode and set parameters
     * @param commMode {@link CommMode}
     * @param portType the selected port. if commMode equal to UART is valid
     * @param baudrate the baud rate. if commMode equal to UART is valid.
     */
    public ScanBoxInitExtParams(CommMode commMode, PortType portType, Baudrate baudrate) {
        this.commMode = commMode;
        this.portType = portType;
        this.baudrate = baudrate;
    }

    /**
     * @param portType
     */
    public ScanBoxInitExtParams(String bleName,String bleAddress,PortType portType){
        this.portType = portType;
        this.bleName = bleName;
        this.bleAddress = bleAddress;
    }

    public CommMode getCommMode() {
        return commMode;
    }

    public PortType getPortType() {
        return portType;
    }

    public Baudrate getBaudrate() {
        return baudrate;
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

    public enum CommMode{
        /**
         * Serial
         */
        UART,
        /**
         * USB
         */
        USB,
        /**
         * Bluetoothbase USB2 port
         */
        BLE_USB2,

        /**
         * Bluetoothbase USB1 port
         */
        BLE_USB1
    }

    public BleBaseParams getBleBaseParams() {
        return bleBaseParams;
    }

    public void setBleBaseParams(BleBaseParams bleBaseParams) {
        this.bleBaseParams = bleBaseParams;
    }
}
