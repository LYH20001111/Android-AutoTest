package com.newland.sdk.module.externalPin;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.PortType;

/**
 * @description: Extra pinpad init port parameters
 * @author: Lindan
 * @create: 2019/07/26
 */
public class PinpadInitExtParams {

    private boolean isHostMode = true;
    private boolean autoMatch = true;
    private PortType portType = PortType.PINPAD;
    private Baudrate baudrate = Baudrate.BPS115200;
    private boolean init = false; // 是否每调用一次init就init一次
    /**
     * 国内指令的外接键盘是否使用3.2.19的重构指令
     */
    private boolean isRestructureCommand = true;
    /**
     * 蓝牙底座名称
     */
    private String bleName;

    /**
     * 蓝牙底座地址
     */
    private String bleAddress;

    /**
     * 初始化外接键盘，是否初始化USB口的键盘
     */
    private boolean isUSBPortEnable = true;

    /**
     * 蓝牙底座是否自动重连
     */
    private Boolean isBleBaseAutoConn = true;

    private BleBaseParams bleBaseParams = new BleBaseParams(false,true);

    /**
     * Automatically matches the parameters of the external pinpad.
     *
     * @param autoMatch
     */
    public PinpadInitExtParams(boolean autoMatch) {
        this.autoMatch = autoMatch;
    }

    /**
     * Pinpad initialization(optional).
     *
     * @param portType the selected Port.
     * @param baudrate the preferred baud rate.
     */
    public PinpadInitExtParams(@NonNull PortType portType, @NonNull Baudrate baudrate) {
        this.autoMatch = false;
        this.portType = portType;
        this.baudrate = baudrate;
    }

    public PinpadInitExtParams(@NonNull PortType portType, String bleName, String  bleAddress,@Nullable Baudrate baudrate) {
        this.portType = portType;
        this.bleName = bleName;
        this.bleAddress = bleAddress;
        this.baudrate = baudrate;
    }

    public PortType getPortType() {
        return portType;
    }

    public Baudrate getBaudrate() {
        return baudrate;
    }

    public boolean isAutoMatch() {
        return autoMatch;
    }

    public boolean isRestructureCommand() {
        return isRestructureCommand;
    }

    public void setRestructureCommand(boolean restructureCommand) {
        isRestructureCommand = restructureCommand;
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

    public boolean isUSBPortEnable() {
        return isUSBPortEnable;
    }

    public void setUSBPortEnable(boolean USBPortEnable) {
        isUSBPortEnable = USBPortEnable;
    }

    public boolean isBleBaseAutoConn() {
        return isBleBaseAutoConn;
    }

    public void setBleBaseAutoConn(boolean bleBaseAutoConn) {
        isBleBaseAutoConn = bleBaseAutoConn;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public boolean isHostMode() {
        return isHostMode;
    }

    /**
     * usb host mode or device mode; default:host mode;
     * @param hostMode
     */
    public void setHostMode(boolean hostMode) {
        isHostMode = hostMode;
    }

    @Override
    public String toString() {
        return "PinpadInitExtParams{" +
                "autoMatch=" + autoMatch +
                ", portType=" + portType +
                ", baudrate=" + baudrate +
                ", init=" + init +
                ", isRestructureCommand=" + isRestructureCommand +
                ", bleName='" + bleName + '\'' +
                ", bleAddress='" + bleAddress + '\'' +
                ", isUSBPortEnable=" + isUSBPortEnable +
                ", isBleBaseAutoConn=" + isBleBaseAutoConn +
                ", isHostMode=" + isHostMode +
                '}';
    }

    public BleBaseParams getBleBaseParams() {
        return bleBaseParams;
    }

    public void setBleBaseParams(BleBaseParams bleBaseParams) {
        this.bleBaseParams = bleBaseParams;
    }
}
